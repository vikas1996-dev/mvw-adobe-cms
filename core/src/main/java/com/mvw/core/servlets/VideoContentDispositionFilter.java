package com.mvw.core.servlets;

import org.osgi.service.component.annotations.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component(service = Filter.class, property = {
        "sling.filter.scope=request",
        "sling.filter.pattern=/content/dam/.*\\.(mp4|mov|mkv)"
})
public class VideoContentDispositionFilter implements Filter {

    private static final String[] VIDEO_EXTENSIONS = {
            ".mp4", ".mov", ".mkv"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        String fileName = extractFileName(uri);
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");

        if (isVideo(uri)) {
            httpResponse.setHeader("Content-Disposition",
                    "inline; filename*=UTF-8''" + encodedFileName);
        }

        chain.doFilter(request, response);
    }

    private boolean isVideo(String uri) {
        if (uri == null)
            return false;

        for (String ext : VIDEO_EXTENSIONS) {
            if (uri.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private String extractFileName(String uri) {
        return uri.substring(uri.lastIndexOf('/') + 1);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
