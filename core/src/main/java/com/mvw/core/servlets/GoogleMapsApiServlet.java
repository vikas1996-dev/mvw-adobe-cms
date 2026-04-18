package com.mvw.core.servlets;

import com.mvw.core.services.impl.GoogleMapApiKeyConfigServiceImpl;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/mvw/googleMapsApi",
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=json"
        }
)
public class GoogleMapsApiServlet extends SlingSafeMethodsServlet {

    @Reference
    private GoogleMapApiKeyConfigServiceImpl configService;

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String googleMapApiKey = configService.getGoogleMapApiKey();

        // fallback safety
        if (googleMapApiKey == null || googleMapApiKey.isEmpty()) {
            googleMapApiKey = "";
        }

        String jsonResponse = "{ \"googleMapApiKey\": \"" + googleMapApiKey + "\" }";

        response.getWriter().write(jsonResponse);
    }
}