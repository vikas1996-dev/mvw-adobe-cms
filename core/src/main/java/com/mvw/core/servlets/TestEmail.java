package com.mvw.core.servlets;

import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.jcr.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/mvw/testEmail",
                "sling.servlet.methods=GET"
        }
)
public class TestEmail extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(TestEmail.class);
    private static final String DEFAULT_FROM_NAME = "MVW Test Email";
    private static final String TEST_EMAIL_TEMPLATE = "/etc/notification/email/mvw/test-email.txt";

    @Reference
    private transient MessageGatewayService messageGatewayService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String fromAddress = request.getParameter("from");
        String to = request.getParameter("to");
        String subject = request.getParameter("subject");

        if (isBlank(to) || isBlank(subject)) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Required query params: to, subject\"}");
            return;
        }

        MessageGateway<HtmlEmail> gateway = messageGatewayService.getGateway(HtmlEmail.class);
        if (gateway == null) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Email gateway unavailable\"}");
            return;
        }

        Session session = request.getResourceResolver().adaptTo(Session.class);
        if (session == null) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Mail template session unavailable\"}");
            return;
        }

        try {
            MailTemplate mailTemplate = MailTemplate.create(TEST_EMAIL_TEMPLATE, session);
            if (mailTemplate == null) {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Mail template unavailable\"}");
                return;
            }

            Map<String, String> emailParams = new HashMap<>();
            emailParams.put("subject", subject);
            emailParams.put("message", "This is a test email triggered from /bin/mvw/testEmail.");

            HtmlEmail email = mailTemplate.getEmail(emailParams, HtmlEmail.class);
            if (email == null) {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Failed to build email from template\"}");
                return;
            }

            email.setCharset("UTF-8");
            email.setFrom(fromAddress, DEFAULT_FROM_NAME);
            email.setTextMsg("This is a test email triggered from /bin/mvw/testEmail.");
            email.addTo(to);

            gateway.send(email);

            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Email sent successfully to " + to + "\"}");
            LOG.info("{} email sent successfully to {}", subject, to);
        } catch (Exception e) {
            Throwable rootCause = getRootCause(e);
            LOG.error("Failed to send test email to {}. Reason: {}. Root cause: {}",
                    to,
                    e.getMessage(),
                    rootCause.getMessage(),
                    e);
            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"error\",\"message\":\""
                    + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private String escapeJson(String value) {
        String input = value == null ? "Failed to send email" : value;
        StringBuilder escaped = new StringBuilder(input.length() + 16);
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (ch < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) ch));
                    } else {
                        escaped.append(ch);
                    }
                    break;
            }
        }
        return escaped.toString();
    }
}
