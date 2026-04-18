package com.mvw.core.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ResourceResolver;


/**
 * Utility class for url related functionality.
 */
public final class UrlUtils {

  /**
   * Maps a string path based on the env settings for Sling Mappings if the path is an AEM path,
   * otherwise the original path is returned.
   */
  public static String getNormalizedUrl(String path,
                                        ResourceResolver resolver) {
    if (resolver == null || isBlank(path)) {
      return null;
    }


    String url = path.trim();

    Boolean isExternal = isExternalUrl(path);

    if (!isExternal && UrlUtils.isAemContentPath(url)) {
      url = UrlUtils.getAemPath(url);
      url = resolver.map(url);
    }

    return url;
  }

  public static String getNormalizedUrl(Page page,
                                        ResourceResolver resolver) {
    return page != null ? getNormalizedUrl(page.getPath(), resolver) : null;
  }

  public static boolean isExternalUrl(String url) {
      return url.startsWith("http://") || url.startsWith("https://");
  }

  /**
   * Is the passed string a relative AEM path (i.e. "/content/../.")
   */
  public static boolean isAemContentPath(String path) {
    return !path.isEmpty() && path.startsWith("/content");
  }

  private static String getAemPath(String path) {
    String fragment = null;
    int hashIdx = path.indexOf('#');
    if (hashIdx >= 0) {
      fragment = path.substring(hashIdx); // includes '#'
      path = path.substring(0, hashIdx);
    }

    // Separate query string
    String query = null;
    int queryIdx = path.indexOf('?');
    if (queryIdx >= 0) {
      query = path.substring(queryIdx); // includes '?'
      path = path.substring(0, queryIdx);
    }

    // If already has extension or is a folder path, skip appending .html
    if (path.contains(".") || path.endsWith("/") || path.isEmpty()) {
      return path + (query != null ? query : "") + (fragment != null ? fragment : "");
    }

    String htmlPath = appendHtml(path);
    return htmlPath + (query != null ? query : "") + (fragment != null ? fragment : "");
  }

  private static String appendHtml(String path) {
    return isNotBlank(path) && !path.endsWith(".html") ? (path + ".html") : path;
  }
}
