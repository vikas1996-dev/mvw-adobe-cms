<%@page import="java.util.*" %>
<%
Random rand = new Random();
int n = rand.nextInt(90000) + 10000;
out.println("random number: "+n);
%>