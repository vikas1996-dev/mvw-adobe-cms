<%@page session="false"%>
<%@include file="/libs/granite/ui/global.jsp"%>
<%@taglib prefix="sling2" uri="http://sling.apache.org/taglibs/sling" %>
<%@page import="java.util.Arrays"%>
<%@page import="org.apache.sling.api.resource.Resource"%>
<%@page import="org.apache.sling.api.resource.ValueMap"%>
<sling2:adaptTo var="cell" adaptable="${slingRequest}" adaptTo="com.adobe.acs.commons.reports.models.ReportCellValue" />

<%
    Resource result = (Resource) request.getAttribute("result");
    if (result == null) {
        result = (Resource) request.getAttribute("resultResource");
    }
    if (result == null) {
        result = (Resource) request.getAttribute("row");
    }

    boolean hasArchive = false;

    if (result != null) {
        Resource metadata = result.getChild("jcr:content/metadata");
        if (metadata != null) {
            ValueMap vm = metadata.getValueMap();
            String[] tags = vm.get("cq:tags", String[].class);
            if (tags != null) {
                hasArchive = Arrays.asList(tags).contains("legal:archive");
               

            }
        }
    }
    request.setAttribute("hasArchive", hasArchive ? "Yes" : "No");

%>

<td is="coral-table-cell" value="${sling2:encode(hasArchive,'HTML_ATTR')}">
  ${hasArchive}
</td>

