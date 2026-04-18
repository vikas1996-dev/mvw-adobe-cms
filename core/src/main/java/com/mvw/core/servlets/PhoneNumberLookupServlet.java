package com.mvw.core.servlets;

import com.google.gson.JsonObject;
import com.mvw.core.services.LocDataService;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Map;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/mvw/phone-number-lookup")
public class PhoneNumberLookupServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    @Reference
    private transient LocDataService locDataService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // Call the service we updated above
        Map<String, String> data = locDataService.processLocData(request, response);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Convert Map to JSON string
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        json.addProperty("loc", data.get("loc"));
        json.addProperty("phoneNumber", data.get("phoneNumber"));

        response.getWriter().write(json.toString());
    }
}

