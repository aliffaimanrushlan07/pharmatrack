<%--
    PharmaTrack - entry point
    OWNER: Aliff (Architecture)

    Not a page in its own right: it looks at whether a session exists and
    bounces the visitor to the right place. Keeping this logic out of the
    login page means the login page has exactly one job.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="my.edu.uptm.pharmatrack.security.AuthFilter" %>
<%
    Object user = (session != null) ? session.getAttribute(AuthFilter.SESSION_USER) : null;
    String target = (user != null) ? "dashboard.jsp" : "login.jsp";
    response.sendRedirect(request.getContextPath() + "/" + target);
%>
