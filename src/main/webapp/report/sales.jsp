<%--
    Sales summary report.
    OWNER: YASIERUL
    STATUS: STUB - Yasierul to build.
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Sales summary"/>
<c:set var="activeNav" value="report"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div><h1>Sales summary</h1><p>Daily revenue, transaction count and average sale.</p></div>
    <a href="${pageContext.request.contextPath}/report?type=lowstock" class="btn btn-secondary">
        Low stock report
    </a>
</div>

<div class="todo-banner">
    <strong>Assigned to Yasierul &mdash; not built yet</strong>
    Needs <code>SaleDAO.getDailySummary()</code> (TODO 5) and
    <code>ReportServlet</code> TODO 1-2.
</div>

<%-- TODO (YASIERUL): loop over ${dailySummary} - each entry is a Map with the
     keys sale_day, transactions, revenue and average_sale. Add a grand-total
     row at the bottom; that row is the clearest single piece of evidence for
     the calculation-feature criterion. --%>

<%@ include file="/includes/footer.jspf" %>
