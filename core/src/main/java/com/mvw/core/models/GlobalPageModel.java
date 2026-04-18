package com.mvw.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.wcm.core.components.models.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import com.adobe.cq.export.json.ExporterConstants;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import com.mvw.core.services.LocDataService;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import lombok.experimental.Delegate;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {Page.class, GlobalPageModel.class, ComponentExporter.class, ContainerExporter.class},
        resourceType = "mvw/components/page",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
        name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
        extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class GlobalPageModel implements Page {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalPageModel.class);

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private SlingHttpServletResponse response;

    @OSGiService
    private LocDataService locDataService;

    @Self
    @Via(type = ResourceSuperType.class)
    @Delegate
    private Page delegate;

    @PostConstruct
    protected void init() {
        LOG.info("GlobalPageModel initialized for page: {}", request.getResource().getPath());
        locDataService.processLocData(request, response);
    }

    @Self
    private ModelFactory modelFactory;

    private Map<String, ComponentExporter> children = new LinkedHashMap<>();

    
    // ---------------- CHILD COMPONENT EXPORT ----------------
   
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        if (children.isEmpty()) {
            // Adjust this path if your components are in a different container
            Resource container = request.getResource().getChild("root/container");
            if (container != null) {
                container.getChildren().forEach(child -> {
                    ComponentExporter childModel = child.adaptTo(ComponentExporter.class);
                    if (childModel != null) {
                        children.put(child.getName(), childModel);
                    }
                });
            }
        }
        return children;
    }

    
    @Override
    public String[] getExportedItemsOrder() {
        return children.keySet().toArray(new String[0]);
    }

    // ---------------- REQUIRED FOR EXPORTER ----------------
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }
}


