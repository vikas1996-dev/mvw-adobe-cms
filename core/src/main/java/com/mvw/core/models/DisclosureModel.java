package com.mvw.core.models;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.genericlists.GenericList;
import com.day.cq.dam.api.Asset;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import lombok.Getter;

@Model(adaptables = { SlingHttpServletRequest.class,
        Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class DisclosureModel {

    private static final Logger logger = LoggerFactory.getLogger(DisclosureModel.class);

    @ValueMapValue
    private String welcomeText;

    @ValueMapValue
    private String copyText;

    @ValueMapValue
    private String resortName;

    @ValueMapValue
    private String active_tab;

    @ValueMapValue
    private String previous_tab;

    @ValueMapValue
    private String first_column;

    @ValueMapValue
    private String second_column;

    @ValueMapValue
    private String download;

    @ValueMapValue
    private String showDownloadAll;

    @Inject
    private ResourceResolverFactory resolverFactory;

    // tag id that indicates archive
    private static final String ARCHIVE_TAG_ID = "legal:archive";

    // generic list path for language token -> display label mapping
    private static final String GENERIC_LIST_PATH = "/etc/acs-commons/lists/mvwlegal-language-specifier";

    @ValueMapValue(name = "folderPath", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String folderPath;

    private ResourceResolver resourceResolver;

    // frontend expects: key = doc name, value = map<language, path>
    private Map<String, Object> activeMap = new LinkedHashMap<>();
    private Map<String, Object> archiveMap = new LinkedHashMap<>();

    private int activePdfsCount;
    private int archivePdfsCount;

    // token -> display language
    private Map<String, String> languageMap = new HashMap<>();

    @PostConstruct
    protected void init() {
        logger.info("Initializing DisclosureModel with folderPath: " + folderPath);
        if (StringUtils.isBlank(folderPath)) {
            logger.warn("folderPath is empty/null");
            return;
        }
        resourceResolver = getServiceResolver();
        if (resourceResolver == null) {
            logger.error("resourceResolver is null, cannot proceed futher, check service user permissions");
            return;
        }

        QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        Session session = resourceResolver.adaptTo(Session.class);
        if (queryBuilder == null || session == null) {
            logger.error("QueryBuilder/Session not available, cannot proceed further, check service user permissions");
            return;
        }

        // 1) Load language mappings
        loadLanguageMap();

        // 2) Query PDFs
        SearchResult result = queryPdfs(queryBuilder, session, folderPath);
        if (result == null || result.getHits().isEmpty()) {
            logger.info("No PDF assets found in the specified folderPath.");
        }

        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);

        // Internal buckets keyed by stable groupKey (number|baseKey)
        Map<String, Map<String, String>> activeTmp = new HashMap<>();
        Map<String, Map<String, String>> archiveTmp = new HashMap<>();

        // Store display title per groupKey (dc:title preferred)
        Map<String, String> displayTitleByGroup = new HashMap<>();
        Map<String, Integer> numberByGroup = new HashMap<>();
        for (Hit hit : result.getHits()) {
            try {
                Resource assetRes = hit.getResource();

                if (assetRes == null)
                    continue;

                Asset asset = assetRes.adaptTo(Asset.class);
                if (asset == null) {
                    logger.warn("could not adapt resource to asset for resource:" + assetRes.getPath()
                            + " asset may not be a PDF");
                    continue;
                }

                String assetName = asset.getName(); // download filename

                ParsedName parsed = parseFileName(assetName);
                boolean isArchive = hasArchiveTag(assetRes, tagManager);

                // display title should be dc:title (fallback to the cleaned file name)
                String dcTitle = safeTrim(asset.getMetadataValue("dc:title"));
                String displayTitle = !dcTitle.isBlank() ? dcTitle
                        : (!parsed.baseKey.isBlank() ? parsed.baseKey : stripPdf(assetName));

                // Group by the resolved title. When dc:title is missing, the cleaned file
                // name becomes the title and grouping key.
                String groupKey = displayTitle;

                String languageDisplay = mapLanguage(parsed.languageToken);

                // value expected by FE = language -> path/url
                String url =  asset.getPath();

                Map<String, Map<String, String>> bucket = isArchive ? archiveTmp : activeTmp;

                Map<String, String> langMap = bucket.getOrDefault(groupKey, new TreeMap<>());
                langMap.put(languageDisplay, url);
                bucket.put(groupKey, langMap);

                displayTitleByGroup.putIfAbsent(groupKey, displayTitle);
                numberByGroup.putIfAbsent(groupKey, parsed.number);

            } catch (Exception e) {
                logger.warn("Error processing PDF hit: {}", e.getMessage(), e);
            }
        }

        // 3) Convert to FE structure, sorted by number
        this.activeMap = toFrontendMap(activeTmp, displayTitleByGroup, numberByGroup);
        this.archiveMap = toFrontendMap(archiveTmp, displayTitleByGroup, numberByGroup);

        this.activePdfsCount = activeMap.size();
        this.archivePdfsCount = archiveMap.size();
    }

    private ResourceResolver getServiceResolver() {
        try {

            Map<String, Object> auth = Map.of(ResourceResolverFactory.SUBSERVICE, "mvwServiceReader");
            return resolverFactory.getServiceResourceResolver(auth);
        } catch (LoginException e) {
            logger.error("service user is null, check user mapping permissions");
        }
        return null;
    }

    private SearchResult queryPdfs(QueryBuilder queryBuilder, Session session, String rootPath) {
        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put("path", rootPath);
        predicateMap.put("path.flat", "true");
        predicateMap.put("type", "dam:Asset");
        predicateMap.put("property", "jcr:content/metadata/dc:format");
        predicateMap.put("property.value", "application/pdf");
        predicateMap.put("nodename", "*.pdf");
        predicateMap.put("p.limit", "-1");

        Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);
        return query.getResult();
    }

    private boolean hasArchiveTag(Resource assetRes, TagManager tagManager) {
        if (tagManager == null || assetRes == null) {
            logger.warn("tag manager is null");
            return false;
        }

        Resource metadata = assetRes.getChild("jcr:content/metadata");
        if (metadata == null) {
            logger.warn("asset doesn't have meta data:" + assetRes.getPath());
            return false;
        }

        String[] tagIds = metadata.getValueMap().get("cq:tags", String[].class);
        if (tagIds == null)
            return false;

        for (String tagId : tagIds) {
            Tag tag = tagManager.resolve(tagId);
            if (tag == null) {
                continue;
            }

            if (ARCHIVE_TAG_ID.equalsIgnoreCase(tag.getTagID())) {
                return true;
            }
        }
        return false;
    }

    private void loadLanguageMap() {
        Map<String, String> map = new HashMap<>();
        try {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            if (pageManager == null) {
                logger.warn("resource resolver could not adapt to page manager: service user may not have read access");
                this.languageMap = map;
                return;
            }

            Page listPage = pageManager.getPage(GENERIC_LIST_PATH);
            if (listPage == null) {
                logger.warn("Generic list page not found at path: " + GENERIC_LIST_PATH);
                this.languageMap = map;
                return;
            }

            GenericList genericList = listPage.adaptTo(GenericList.class);
            if (genericList == null) {
                logger.warn("Generic list page not found at path: " + GENERIC_LIST_PATH);
                this.languageMap = map;
                return;
            }

            for (GenericList.Item item : genericList.getItems()) {
                // token stored in title, display label stored in value (per your latest logic)
                String token = safeTrim(item.getTitle()).toLowerCase(Locale.ROOT);
                String display = safeTrim(item.getValue());
                if (!token.isBlank() && !display.isBlank()) {
                    map.put(token, display);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed loading language generic list: {}", e.getMessage(), e);
        }
        this.languageMap = map;
        logger.info("number of languages configured:" + languageMap.size());

    }

    private String mapLanguage(String token) {
        if (token == null || token.isBlank())
            return "English";
        return languageMap.getOrDefault(token.toLowerCase(Locale.ROOT).trim(), "English");
    }

    /**
     * Filename format expected: "number name-language.pdf"
     * - Extract leading number for sorting
     * - Detect language token only if it exists in the generic list
     * - baseKey = "number + name" portion WITHOUT "-language"
     */
    private ParsedName parseFileName(String fileName) {
        ParsedName pn = new ParsedName();
        pn.number = Integer.MAX_VALUE;
        pn.suffix = "";
        pn.baseKey = "";
        pn.languageToken = "";

        if (StringUtils.isBlank(fileName)) {
            return pn;
        }

        String nameNoExt = stripPdf(fileName).trim();

        // 1) Extract leading number (digits at start) and optional letter suffix
        int i = 0;
        while (i < nameNoExt.length() && Character.isDigit(nameNoExt.charAt(i))) {
            i++;
        }

        String numberStr = "";
        if (i > 0) {
            numberStr = nameNoExt.substring(0, i);
            try {
                pn.number = Integer.parseInt(numberStr);
            } catch (Exception ignored) {
                pn.number = Integer.MAX_VALUE;
                numberStr = ""; // if parse fails, don't prepend a broken number
            }
        }

        if (i < nameNoExt.length() && Character.isLetter(nameNoExt.charAt(i))) {
            pn.suffix = String.valueOf(Character.toLowerCase(nameNoExt.charAt(i)));
            i++;
        }

        // Remainder after the leading number
        String remainder = nameNoExt.substring(i).trim();
        if (remainder.isBlank()) {
            // If filename is just a number or weird, keep original
            pn.baseKey = nameNoExt;
            return pn;
        }

        // 2) Candidate language token = last token split by '-' or whitespace
        String[] parts = remainder.split("[-\\s]+");
        String lastToken = (parts.length > 0)
                ? safeTrim(parts[parts.length - 1]).toLowerCase(Locale.ROOT)
                : "";

        String base;
        if (!lastToken.isBlank() && languageMap.containsKey(lastToken)) {
            pn.languageToken = lastToken;

            // Remove the last token occurrence from the end (best-effort)
            int idx = remainder.toLowerCase(Locale.ROOT).lastIndexOf(lastToken);
            base = (idx > 0) ? remainder.substring(0, idx).trim() : remainder.trim();
            base = cleanupBase(base);
        } else {
            pn.languageToken = "";
            base = cleanupBase(remainder);
        }

        // 3) IMPORTANT: Preserve number in the display/base key
        // e.g. "6 Rules and Regulations" or "1a Public Offering Statement"
        String prefix = numberStr;
        if (!pn.suffix.isBlank()) {
            prefix += pn.suffix;
        }
        pn.baseKey = (!prefix.isBlank() ? (prefix + " " + base).trim() : base);

        return pn;
    }

    private String stripPdf(String fileName) {
        return fileName.replaceAll("(?i)\\.pdf$", "");
    }

    private String cleanupBase(String s) {
        String b = safeTrim(s);
        // Remove trailing separators left by cutting language token
        while (b.endsWith("-") || b.endsWith("_")) {
            b = b.substring(0, b.length() - 1).trim();
        }
        return b;
    }

    /**
     * Final frontend structure:
     * Map<DocDisplayKey, Object(langMap)>
     * DocDisplayKey uses number + dc:title (so order and uniqueness are safe)
     */
    private Map<String, Object> toFrontendMap(
            Map<String, Map<String, String>> tmp,
            Map<String, String> titleByGroup,
            Map<String, Integer> numberByGroup) {

        return tmp.entrySet().stream()
                .sorted((left, right) -> compareDisplayTitles(
                        titleByGroup.getOrDefault(left.getKey(), left.getKey()),
                        titleByGroup.getOrDefault(right.getKey(), right.getKey())))
                .collect(Collectors.toMap(
                        e -> formatDisplayKey(
                                numberByGroup.getOrDefault(e.getKey(), Integer.MAX_VALUE),
                                titleByGroup.getOrDefault(e.getKey(), e.getKey())),
                        e -> (Object) e.getValue(),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    private String formatDisplayKey(int number, String title) {
        String t = safeTrim(title);
        if (number == Integer.MAX_VALUE) {
            return t;
        }
        // Keep number visible (matches filename-driven ordering expectations)
        // If you don't want the number shown, return just `t` but keep LinkedHashMap
        // ordering.
        return t;
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private int compareDisplayTitles(String leftTitle, String rightTitle) {
        LeadingSortKey left = parseLeadingSortKey(leftTitle);
        LeadingSortKey right = parseLeadingSortKey(rightTitle);

        if (left.number != right.number) {
            return Integer.compare(left.number, right.number);
        }

        if (!left.suffix.equals(right.suffix)) {
            if (left.suffix.isBlank()) {
                return -1;
            }
            if (right.suffix.isBlank()) {
                return 1;
            }
            return left.suffix.compareTo(right.suffix);
        }

        int byText = left.text.compareTo(right.text);
        if (byText != 0) {
            return byText;
        }

        return safeTrim(leftTitle).toLowerCase(Locale.ROOT)
                .compareTo(safeTrim(rightTitle).toLowerCase(Locale.ROOT));
    }

    private LeadingSortKey parseLeadingSortKey(String value) {
        LeadingSortKey key = new LeadingSortKey();
        String trimmed = safeTrim(value);
        if (trimmed.isBlank()) {
            return key;
        }

        int i = 0;
        while (i < trimmed.length() && Character.isDigit(trimmed.charAt(i))) {
            i++;
        }

        if (i == 0) {
            key.text = trimmed.toLowerCase(Locale.ROOT);
            return key;
        }

        try {
            key.number = Integer.parseInt(trimmed.substring(0, i));
        } catch (NumberFormatException e) {
            key.number = Integer.MAX_VALUE;
        }

        if (i < trimmed.length() && Character.isLetter(trimmed.charAt(i))) {
            key.suffix = String.valueOf(Character.toLowerCase(trimmed.charAt(i)));
            i++;
        }

        key.text = safeTrim(trimmed.substring(i)).toLowerCase(Locale.ROOT);
        return key;
    }

    private static class ParsedName {
        int number;
        String suffix;
        String baseKey;
        String languageToken;
    }

    private static class LeadingSortKey {
        int number = Integer.MAX_VALUE;
        String suffix = "";
        String text = "";
    }
}
