<%--
    Sales summary report.
    OWNER: YASIERUL
    STATUS: STUB - Yasierul to build.
    TEMPLATE: report/stock.jsp is the finished sibling of this page. Same
    skeleton - tabs, summary tiles, table, grand total in the <tfoot>. Copy it
    and swap the attribute names.
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Sales summary"/>
<c:set var="activeNav" value="report"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div><h1>Sales summary</h1><p>Daily revenue, transaction count and average sale.</p></div>
</div>

<%-- Report tabs. Both links hit the same servlet with a different ?type=,
     mirroring the ?action= pattern in MedicineServlet. --%>
<div class="search-bar" style="margin-bottom:18px;">
    <a href="${pageContext.request.contextPath}/report?type=stock"
       class="btn btn-secondary">Inventory valuation</a>
    <a href="${pageContext.request.contextPath}/report?type=sales"
       class="btn">Sales summary</a>
</div>

<div class="todo-banner">
    <strong>Assigned to Yasierul &mdash; not built yet</strong>
    Needs <code>SaleDAO.getDailySummary()</code> (TODO 5) and
    <code>ReportServlet</code> TODO 1-2. The inventory valuation report on the
    other tab already works - build this one in its image.
</div>

<%-- TODO (YASIERUL): loop over ${dailySummary} - each entry is a Map with the
     keys sale_day, transactions, revenue and average_sale. Add a grand-total
     row at the bottom; that row is the clearest single piece of evidence for
     the calculation-feature criterion.

     Do the totalling in Java, not here. StockValuation.grandTotal() is the
     pattern: the JSP prints, the model calculates. Watch the average - the
     overall average sale is total revenue / total transactions, NOT the mean
     of the daily averages, because the days have different transaction
     counts. StockValuation.grandTotal() has the same trap commented. --%>

<%@ include file="/includes/footer.jspf" %>
