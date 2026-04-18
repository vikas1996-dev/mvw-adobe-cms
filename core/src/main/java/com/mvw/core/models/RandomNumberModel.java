package com.mvw.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;

@Model(adaptables = SlingHttpServletRequest.class)
public class RandomNumberModel {

    private int requestRandom;   // changes every request
    private int sessionRandom;   // persists in session

    public RandomNumberModel(SlingHttpServletRequest request) {
        SecureRandom rand = new SecureRandom();

        // Request-scoped random number
        requestRandom = rand.nextInt(90000) + 10000;

        // Session-scoped random number
        HttpSession session = request.getSession();
        Object sessionAttr = session.getAttribute("sessionRandom");
        if (sessionAttr != null) {
            sessionRandom = (int) sessionAttr;
        } else {
            sessionRandom = rand.nextInt(90000) + 10000;
            session.setAttribute("sessionRandom", sessionRandom);
        }
    }

    public int getRequestRandom() {
        return requestRandom;
    }

    public int getSessionRandom() {
        return sessionRandom;
    }
}
