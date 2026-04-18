package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LegalHeroBanner {

    @ValueMapValue(name = "bannerImage/fileReference")
    private String bannerImageFileReference;

    @ValueMapValue
    private String copyText;

    @ValueMapValue(name = "decorationImage/fileReference")
    private String decorationImage;

    private static final Pattern TAGS = Pattern.compile("<[^>]+>");

public String getPlainCopyText() {
    if (copyText == null || copyText.isBlank()) {
        return "";
    }

    String s = copyText;

    // 1) Preserve line breaks for common block elements
    s = s.replaceAll("(?i)<\\s*br\\s*/?\\s*>", "\n");
    s = s.replaceAll("(?i)<\\s*/\\s*p\\s*>", "\n");
    s = s.replaceAll("(?i)<\\s*/\\s*div\\s*>", "\n");
    s = s.replaceAll("(?i)<\\s*/\\s*li\\s*>", "\n");
    s = s.replaceAll("(?i)</\\s*h[1-6]\\s*>", "\n");

    // 2) Remove all remaining tags
    s = TAGS.matcher(s).replaceAll("");

    // 3) Decode common HTML entities (minimal set)
    s = s.replace("&nbsp;", " ")
         .replace("&amp;", "&")
         .replace("&lt;", "<")
         .replace("&gt;", ">")
         .replace("&quot;", "\"")
         .replace("&#39;", "'");

    // 4) Normalize whitespace and newlines
    s = s.replaceAll("[\\t\\x0B\\f\\r]+", " ");
    s = s.replaceAll("\\s*\\n\\s*", "\n");      // trim spaces around newlines
    s = s.replaceAll("\\n{3,}", "\n\n");        // collapse excessive blank lines

    return s.trim();
}

}
