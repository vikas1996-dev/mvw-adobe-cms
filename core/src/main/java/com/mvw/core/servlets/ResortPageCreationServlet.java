package com.mvw.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/mvw/create-resort-page")
public class ResortPageCreationServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResortPageCreationServlet.class);

    @Reference
    private transient SlingSettingsService slingSettingsService;

    private static final String DEFAULT_PARENT_PATH = "/content/tmvcs/us/en/experiences/resorts";
    private static final String DEFAULT_TEMPLATE = "/conf/tmvc/settings/wcm/templates/resort-details-page-template";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        if (!slingSettingsService.getRunModes().contains("author")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Error: This servlet is only available on Author instance.");
            return;
        }

        ResourceResolver resolver = request.getResourceResolver();
        PageManager pageManager = resolver.adaptTo(PageManager.class);

        String parentPath = request.getParameter("parentPath");
        if (parentPath == null)
            parentPath = DEFAULT_PARENT_PATH;

        String template = request.getParameter("template");
        if (template == null)
            template = DEFAULT_TEMPLATE;

        response.setContentType("text/plain");

        if (pageManager == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: PageManager is null");
            return;
        }

        try {
            LOGGER.info("Fetching resorts internally from /bin/mvw/resort-list");

            // Capture the response from the other servlet
            CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/bin/mvw/resort-list");
            if (dispatcher == null) {
                throw new IOException("Could not get RequestDispatcher for /bin/mvw/resort-list");
            }

            dispatcher.include(request, responseWrapper);

            String jsonResponse = responseWrapper.toString();
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            if (!jsonObject.has("resorts")) {
                response.getWriter().write("No resorts found in response.");
                return;
            }

            JsonArray resorts = jsonObject.getAsJsonArray("resorts");

            for (JsonElement resortElement : resorts) {
                JsonObject resort = resortElement.getAsJsonObject();
                if (!resort.has("slug") || resort.get("slug").isJsonNull())
                    continue;

                String pageName = resort.get("slug").getAsString();

                String marshaCode = "";
                if (resort.has("marshaCode") && !resort.get("marshaCode").isJsonNull()) {
                    marshaCode = resort.get("marshaCode").getAsString();
                }

                String upcCode = "";
                if (resort.has("universalPropertyCode") && !resort.get("universalPropertyCode").isJsonNull()) {
                    upcCode = resort.get("universalPropertyCode").getAsString();
                }

                String title = pageName.replace("-", " ");

                Page page = pageManager.getPage(parentPath + "/" + pageName);
                if (page == null) {
                    page = pageManager.create(parentPath, pageName, template, title);
                    response.getWriter().write("Created page: " + page.getPath() + "\n");
                } else {
                    response.getWriter().write("Page already exists: " + page.getPath() + "\n");
                }

                Resource contentResource = page.getContentResource();
                if (contentResource != null) {
                    ModifiableValueMap properties = contentResource.adaptTo(ModifiableValueMap.class);
                    if (properties != null) {
                        properties.put("upcCode", upcCode);
                        properties.put("marshaCode", marshaCode);
                    }
                }
            }
            resolver.commit();
            response.getWriter().write("All pages processed successfully.");

        } catch (Exception e) {
            LOGGER.error("Error creating pages", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }

    /**
     * Wrapper to capture the response body of the included servlet.
     */
    private static class CharResponseWrapper extends HttpServletResponseWrapper {
        private final CharArrayWriter charWriter = new CharArrayWriter();
        private final PrintWriter writer = new PrintWriter(charWriter);

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() {
            return writer;
        }

        @Override
        public String toString() {
            writer.flush();
            return charWriter.toString();
        }

        @Override
        public void setContentType(String type) {
            // Prevent the included servlet from changing the content type
        }

        @Override
        public void setCharacterEncoding(String charset) {
            // Prevent the included servlet from changing the encoding
        }
    }
}
