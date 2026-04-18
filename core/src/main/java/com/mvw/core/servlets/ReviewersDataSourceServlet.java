package com.mvw.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.*;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
    resourceTypes = "/apps/mvw/components/tmvc/datasources/reviewersDatasource",
    methods = "GET"
)
@ServiceDescription("Reviewers DataSource Servlet using Service User")
public class ReviewersDataSourceServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewersDataSourceServlet.class);
    private static final String SUB_SERVICE = "mvwWorkflowServiceUser";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        List<Resource> reviewerResources = new ArrayList<>();

        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SUB_SERVICE);

        try (ResourceResolver serviceResolver = resolverFactory.getServiceResourceResolver(param)) {
            UserManager userManager = serviceResolver.adaptTo(UserManager.class);

            String targetGroupId = Optional.of(request.getResource())
                                        .map(res -> res.getChild("datasource"))
                                        .map(Resource::getValueMap)
                                        .map(vm -> vm.get("targetGroup", StringUtils.EMPTY))
                                        .orElse(null);

            if (userManager != null && targetGroupId != null) {
                Authorizable authorizable = userManager.getAuthorizable(targetGroupId);

                if (authorizable instanceof Group) {
                    Iterator<Authorizable> members = ((Group) authorizable).getMembers();

                    while (members.hasNext()) {
                        Authorizable member = members.next();

                        if (!member.isGroup()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("text", member.getID());
                            map.put("value", member.getID());

                            // Use the request's resolver for the Resource,
                            // but the data from the Service User's UserManager.
                            reviewerResources.add(new ValueMapResource(
                                request.getResourceResolver(),
                                new ResourceMetadata(),
                                JcrConstants.NT_UNSTRUCTURED,
                                new ValueMapDecorator(map))
                            );
                        }
                    }
                } else {
                    LOG.warn("Target group '{}' not found via service user.", targetGroupId);
                }
            }
        } catch (LoginException e) {
            LOG.error("Login Exception: Could not get service resolver for {}", SUB_SERVICE, e);
        } catch (Exception e) {
            LOG.error("General Error in ReviewersDatasourceServlet", e);
        }

        DataSource ds = new SimpleDataSource(reviewerResources.iterator());
        request.setAttribute(DataSource.class.getName(), ds);
    }
}
 