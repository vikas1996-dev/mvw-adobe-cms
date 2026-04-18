package com.mvw.core.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import java.lang.reflect.Proxy;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class SyncLegalPdfTitleProcessTest {

    private static final String ASSET_PATH =
            "/content/dam/legal/marriottvacationclubs/test-folder/test-document.pdf";
    private static final String ASSET_CONTENT_PATH = ASSET_PATH + "/jcr:content";
    private static final String METADATA_PATH = ASSET_CONTENT_PATH + "/metadata";

    private final SyncLegalPdfTitleProcess process = new SyncLegalPdfTitleProcess();

    @Test
    void executeUpdatesMetadataJcrTitleForPdfPayload(AemContext context) throws Exception {
        createLegalPdf(context, "Updated Title", "Old Title");

        process.execute(workItem(ASSET_PATH), workflowSession(context.resourceResolver()), null);

        assertEquals("Updated Title", context.resourceResolver().getResource(METADATA_PATH).getValueMap().get("jcr:title", String.class));
        assertNull(context.resourceResolver().getResource(ASSET_CONTENT_PATH).getValueMap().get("jcr:title", String.class));
    }

    @Test
    void executeUpdatesMetadataJcrTitleForMetadataPayload(AemContext context) throws Exception {
        createLegalPdf(context, "Metadata Title", "");

        process.execute(workItem(METADATA_PATH), workflowSession(context.resourceResolver()), null);

        assertEquals("Metadata Title", context.resourceResolver().getResource(METADATA_PATH).getValueMap().get("jcr:title", String.class));
    }

    @Test
    void executeSkipsWhenDcTitleIsBlank(AemContext context) throws Exception {
        createLegalPdf(context, "   ", "Existing Title");

        process.execute(workItem(ASSET_PATH), workflowSession(context.resourceResolver()), null);

        assertEquals("Existing Title", context.resourceResolver().getResource(METADATA_PATH).getValueMap().get("jcr:title", String.class));
    }

    @Test
    void executeSkipsWhenJcrTitleAlreadyMatches(AemContext context) throws Exception {
        createLegalPdf(context, "Same Title", "Same Title");

        process.execute(workItem(ASSET_CONTENT_PATH), workflowSession(context.resourceResolver()), null);

        assertEquals("Same Title", context.resourceResolver().getResource(METADATA_PATH).getValueMap().get("jcr:title", String.class));
    }

    @Test
    void executeSkipsWhenPayloadIsNotLegalPdf(AemContext context) throws Exception {
        context.create().resource(
                "/content/dam/tmvcs/test-document.pdf/jcr:content/metadata",
                "dc:title", "Wrong Asset",
                "jcr:title", "Original Title");
        context.resourceResolver().commit();

        process.execute(workItem("/content/dam/tmvcs/test-document.pdf"), workflowSession(context.resourceResolver()), null);

        assertEquals(
                "Original Title",
                context.resourceResolver().getResource("/content/dam/tmvcs/test-document.pdf/jcr:content/metadata")
                        .getValueMap().get("jcr:title", String.class));
    }

    @Test
    void executeThrowsWhenResolverIsNull() {
        assertThrows(WorkflowException.class, () -> process.execute(workItem(ASSET_PATH), workflowSession(null), null));
    }

    private void createLegalPdf(AemContext context, String dcTitle, String jcrTitle) throws Exception {
        context.create().resource(ASSET_CONTENT_PATH);
        context.create().resource(METADATA_PATH, "dc:title", dcTitle, "jcr:title", jcrTitle);
        context.resourceResolver().commit();
    }

    private WorkItem workItem(String payload) {
        WorkflowData workflowData = (WorkflowData) Proxy.newProxyInstance(
                WorkflowData.class.getClassLoader(),
                new Class<?>[]{WorkflowData.class},
                (proxy, method, args) -> {
                    if ("getPayload".equals(method.getName())) {
                        return payload;
                    }
                    return defaultValue(method.getReturnType());
                });

        return (WorkItem) Proxy.newProxyInstance(
                WorkItem.class.getClassLoader(),
                new Class<?>[]{WorkItem.class},
                (proxy, method, args) -> {
                    if ("getWorkflowData".equals(method.getName())) {
                        return workflowData;
                    }
                    if ("getId".equals(method.getName())) {
                        return "sync-legal-pdf-title-test";
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private WorkflowSession workflowSession(ResourceResolver resolver) {
        return (WorkflowSession) Proxy.newProxyInstance(
                WorkflowSession.class.getClassLoader(),
                new Class<?>[]{WorkflowSession.class},
                (proxy, method, args) -> {
                    if ("adaptTo".equals(method.getName()) && args != null && args.length == 1
                            && args[0] == ResourceResolver.class) {
                        return resolver;
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (boolean.class.equals(returnType)) {
            return false;
        }
        if (char.class.equals(returnType)) {
            return '\0';
        }
        if (byte.class.equals(returnType)) {
            return (byte) 0;
        }
        if (short.class.equals(returnType)) {
            return (short) 0;
        }
        if (int.class.equals(returnType)) {
            return 0;
        }
        if (long.class.equals(returnType)) {
            return 0L;
        }
        if (float.class.equals(returnType)) {
            return 0F;
        }
        if (double.class.equals(returnType)) {
            return 0D;
        }
        return null;
    }
}
