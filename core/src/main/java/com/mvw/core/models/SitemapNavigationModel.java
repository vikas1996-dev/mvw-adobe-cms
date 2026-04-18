package com.mvw.core.models;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.mvw.core.utils.LocalizationUtils;
import lombok.experimental.Delegate;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = Navigation.class,
        resourceType = {SitemapNavigationModel.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SitemapNavigationModel implements Navigation {

    public static final String RESOURCE_TYPE = "mvw/components/tmvc/components/sitemapnavigation";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private Resource resource;

    @Self
    @Via(type = ResourceSuperType.class)
    @Delegate(excludes = Navigation.class)
    private Navigation delegate;

    @ScriptVariable
    private Page currentPage;

    private int structureDepth;

    private Page navigationRootPage;

    private List<NavigationItem> items;

    @ScriptVariable
    private Style currentStyle;

    private int structureStart;

    @Self
    private SlingHttpServletRequest request;

    @Self
    private LinkManager linkManager;

    @OSGiService
    private LanguageManager languageManager;

    @OSGiService
    private LiveRelationshipManager relationshipManager;

    @ScriptVariable
    private Component component;

    @PostConstruct
    protected void init() {
        try {
            ValueMap properties = this.resource.getValueMap();
            structureDepth = properties.get(PN_STRUCTURE_DEPTH, currentStyle.get(PN_STRUCTURE_DEPTH, -1));
            boolean collectAllPages = properties.get(PN_COLLECT_ALL_PAGES, currentStyle.get(PN_COLLECT_ALL_PAGES, true));
            if (collectAllPages) {
                structureDepth = -1;
            }
            if (currentStyle.containsKey(PN_STRUCTURE_START) || properties.containsKey(PN_STRUCTURE_START)) {
                //workaround to maintain the content of Navigation component of users in case they update to the current i.e. the `structureStart` version.
                structureStart = properties.get(PN_STRUCTURE_START, currentStyle.get(PN_STRUCTURE_START, 1));
            } else {
                boolean skipNavigationRoot = properties.get(PN_SKIP_NAVIGATION_ROOT, currentStyle.get(PN_SKIP_NAVIGATION_ROOT, true));
                if (skipNavigationRoot) {
                    structureStart = 1;
                } else {
                    structureStart = 0;
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error in DestinationResponse.init(): {}", e.getMessage(), e);
        }
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getAccessibilityLabel() {
        return delegate.getAccessibilityLabel();
    }

    @Override
    public List<NavigationItem> getItems() {
        if (this.items == null) {
            this.items = Optional.ofNullable(this.getNavigationRoot())
                .map(navigationRoot -> customGetRootItems(navigationRoot, structureStart))
                .orElseGet(Stream::empty)
                .map(item -> this.createNavigationItem(item, customGetItems(item)))
                .collect(Collectors.toList());
        }
        logger.info("items size: {} & items: {}", items.size(), items);

        return Collections.unmodifiableList(items);
    }

    private List<NavigationItem> customGetItems(Page subtreeRoot) {
        if (this.structureDepth < 0 || subtreeRoot.getDepth() - this.getNavigationRoot().getDepth() < this.structureDepth) {
            Iterator<Page> childIterator = subtreeRoot.listChildren(page -> page.isValid() &&
                !page.getProperties().get("hideFromHtmlSitemap", false));

            return StreamSupport.stream(((Iterable<Page>) () -> childIterator).spliterator(), false)
                .map(item -> this.createNavigationItem(item, customGetItems(item)))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Stream<Page> customGetRootItems(Page navigationRoot, int structureStart) {
        if (structureStart < 1) {
            return Stream.of(navigationRoot);
        }

        Iterator<Page> childIterator = navigationRoot.listChildren(page -> page.isValid() &&
            !page.getProperties().get("hideFromHtmlSitemap", false));

        return StreamSupport.stream(((Iterable<Page>) () -> childIterator).spliterator(), false)
            .flatMap(child -> customGetRootItems(child, structureStart - 1));
    }

    private Page getNavigationRoot() {
        if (this.navigationRootPage == null) {
            String navigationRootPath = Optional.ofNullable(this.resource.getValueMap().get(PN_NAVIGATION_ROOT, String.class))
                .orElseGet(() -> currentStyle.get(PN_NAVIGATION_ROOT, String.class));
            this.navigationRootPage = LocalizationUtils.getLocalPage(navigationRootPath,
                    this.currentPage,
                    this.request.getResourceResolver(),
                    this.languageManager,
                    this.relationshipManager)
                .orElseGet(() -> currentPage.getPageManager().getPage(navigationRootPath));
        }
        return this.navigationRootPage;
    }

    private NavigationItem createNavigationItem(final Page page, final List<NavigationItem> children) {
        int level = page.getDepth() - (this.getNavigationRoot().getDepth() + structureStart);
        boolean current = checkCurrent(page);
        boolean selected = checkSelected(page, current);
        return newNavigationItem(page, selected, current, linkManager, level, children, getId(), component);
    }

    private NavigationItem newNavigationItem(Page page,
                                             boolean active,
                                             boolean current,
                                             LinkManager linkManager,
                                             int level,
                                             List<NavigationItem> children,
                                             String parentId,
                                             Component component) {

        NavigationItem baseItem = new NavigationItem() {
            @Override public Page getPage() { return page; }
            @Override public boolean isActive() { return active; }
            @Override public boolean isCurrent() { return current; }
            @Override public List<NavigationItem> getChildren() { return children; }
            @Override public int getLevel() { return level; }

            @Override
            public String getTitle() {
                return Optional.ofNullable(page.getNavigationTitle())
                    .orElse(Optional.ofNullable(page.getPageTitle())
                        .orElse(page.getTitle()));
            }

            @Override
            public Link<Page> getLink() {
                return linkManager.get(page).build();
            }

            @Override
            public ComponentData getData() {
                return null;
            }
        };

        return wrapItem(baseItem);
    }

    private NavigationItem wrapItem(NavigationItem item) {
        List<NavigationItem> wrappedChildren = item.getChildren()
            .stream()
            .map(this::wrapItem)
            .collect(Collectors.toList());

        return new SitemapNavigationItemImpl(item, wrappedChildren);
    }


    private boolean checkCurrent(final Page page) {
        return this.currentPage.equals(page)
            || currentPageIsRedirectTarget(page);
    }

    private boolean currentPageIsRedirectTarget(final Page page) {
        return false;
    }

    private boolean checkSelected(final Page page, boolean current) {
        return current || this.currentPage.getPath().startsWith(page.getPath() + "/");
    }
}
