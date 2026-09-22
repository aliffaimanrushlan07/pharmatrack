<%--
    Dashboard - the landing page after login.
    OWNER: AMIR (Interface Design and Sale module).
    STATUS: PARTIAL - the medicine count is live; the sales tiles are TODOs.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="my.edu.uptm.pharmatrack.dao.MedicineDAO" %>
<%@ page import="my.edu.uptm.pharmatrack.model.Medicine" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Dashboard"/>
<c:set var="activeNav" value="dashboard"/>
<%@ include file="/includes/header.jspf" %>

<%--
    NOTE FOR THE GROUP: querying the database from inside a JSP scriptlet like
    this breaks MVC - the view is supposed to receive data, not fetch it.
    It is here only so the dashboard shows something real from day one.

    TODO (AMIR): create a DashboardServlet mapped to /dashboard that does these
    lookups and forwards here, then change the navigation links to point at it.
    That change alone strengthens the "separation of presentation and data
    access" point in the architecture section of the report.
--%>
<%
    int totalMedicines = 0;
    String dashError   = null;
    try {
        MedicineDAO dao = new MedicineDAO();
        totalMedicines = dao.findAll().size();
    } catch (Exception ex) {
        dashError = ex.getMessage();
    }
    request.setAttribute("totalMedicines", totalMedicines);
    request.setAttribute("dashError",      dashError);
%>

<div class="page-header">
    <div>
        <h1>Welcome back, <c:out value="${loggedInUser.fullName}"/></h1>
        <p>Here is the current state of the pharmacy.</p>
    </div>
</div>

<c:if test="${not empty dashError}">
    <div class="alert alert-error">
        Could not load dashboard figures: <c:out value="${dashError}"/><br>
        Check that MySQL is running and that <code>db.properties</code> has your password.
    </div>
</c:if>

<div class="stat-grid">
    <div class="stat">
        <div class="label">Medicines</div>
        <div class="value">${totalMedicines}</div>
    </div>
    <div class="stat">
        <div class="label">Sales today</div>
        <div class="value">&mdash;</div>
    </div>
    <div class="stat">
        <div class="label">Revenue today</div>
        <div class="value">&mdash;</div>
    </div>
</div>

<div class="todo-banner">
    <strong>Dashboard is partially built</strong>
    The two sales tiles need <em>Yasierul</em>'s <code>SaleDAO.getDailySummary()</code>
    (TODO 5), and the whole page should move behind a <code>DashboardServlet</code>.
    Delete this banner once both are done.
</div>

<div class="card">
    <h2>Quick actions</h2>
    <div style="display:flex; gap:10px; flex-wrap:wrap;">
        <a href="${pageContext.request.contextPath}/medicine?action=new" class="btn">Add medicine</a>
        <a href="${pageContext.request.contextPath}/sale" class="btn btn-secondary">New sale</a>
        <a href="${pageContext.request.contextPath}/search" class="btn btn-secondary">Search records</a>
        <a href="${pageContext.request.contextPath}/report" class="btn btn-secondary">Reports</a>
    </div>
</div>

<%@ include file="/includes/footer.jspf" %>
