<%--
    Dashboard - the landing page after login.
    OWNER: AMIR (Interface Design and Sale module).
    STATUS: COMPLETE - figures are supplied by DashboardServlet.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Dashboard"/>
<c:set var="activeNav" value="dashboard"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Welcome back, <c:out value="${loggedInUser.fullName}"/></h1>
        <p>Here is the current state of the pharmacy.</p>
    </div>
</div>

<div class="stat-grid">
    <div class="stat">
        <div class="label">Medicines</div>
        <div class="value">${totalMedicines}</div>
    </div>
    <div class="stat">
        <div class="label">Sales today</div>
        <div class="value">${salesToday}</div>
    </div>
    <div class="stat">
        <div class="label">Revenue today</div>
        <div class="value">RM <fmt:formatNumber value="${revenueToday}"
                minFractionDigits="2" maxFractionDigits="2"/></div>
    </div>
</div>

<div class="card">
    <h2>Quick actions</h2>
    <div style="display:flex; gap:10px; flex-wrap:wrap;">
        <%-- Only admins can manage medicines (RoleFilter enforces it; this
             just avoids offering cashiers a button that would refuse them). --%>
        <c:if test="${loggedInUser.admin}">
            <a href="${pageContext.request.contextPath}/medicine?action=new" class="btn">Add medicine</a>
        </c:if>
        <a href="${pageContext.request.contextPath}/sale" class="btn ${loggedInUser.admin ? 'btn-secondary' : ''}">New sale</a>
        <a href="${pageContext.request.contextPath}/search" class="btn btn-secondary">Search records</a>
        <a href="${pageContext.request.contextPath}/report" class="btn btn-secondary">Reports</a>
    </div>
</div>

<%@ include file="/includes/footer.jspf" %>
