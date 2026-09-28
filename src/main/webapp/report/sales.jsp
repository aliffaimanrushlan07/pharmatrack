<%--
    Sales summary report.
    OWNER: YASIERUL
    STATUS: COMPLETE

    Same skeleton as report/stock.jsp: tabs, summary tiles, table, grand total
    in the <tfoot>. Everything printed here was calculated in Java -
    SaleDAO.getDailySummary() for the per-day rows (SQL GROUP BY), and
    SalesCalculator for the totals. There is no arithmetic in this file.

    The overall average is total revenue / total transactions, NOT the mean
    of the daily averages - the days have different transaction counts, so
    averaging the averages would over-weight the quiet days.
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Sales summary"/>
<c:set var="activeNav" value="report"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div><h1>Sales summary</h1><p>Daily revenue, transaction count and average sale.</p></div>
    <button type="button" class="btn btn-secondary report-print" onclick="window.print()">Print</button>
</div>

<%-- Report tabs. Both links hit the same servlet with a different ?type=,
     mirroring the ?action= pattern in MedicineServlet. --%>
<div class="search-bar" style="margin-bottom:18px;">
    <%-- Inventory valuation is ADMIN-only (see RoleFilter), so cashiers do
         not get the tab. Without this a cashier reading the sales summary
         would be one click away from a 403. --%>
    <c:if test="${loggedInUser.admin}">
        <a href="${pageContext.request.contextPath}/report?type=stock"
           class="btn btn-secondary">Inventory valuation</a>
    </c:if>
    <a href="${pageContext.request.contextPath}/report?type=sales"
       class="btn">Sales summary</a>
    
</div>

<c:choose>

    <c:when test="${empty dailySummary}">
        <div class="empty">
            <h3>No sales recorded yet</h3>
            <p>Once the first sale is completed, daily revenue and averages will appear here.</p>
            <a href="${pageContext.request.contextPath}/sale" class="btn">Start a sale</a>
        </div>
    </c:when>

    <c:otherwise>

        <%-- Receipt-total integrity check. A receipt whose stored total does
             not equal the sum of its lines is flagged here; an admin can fix
             them all with one click (SaleDAO.recalculateTotals). --%>
        <c:if test="${not empty mismatches}">
            <div class="alert alert-warning">
                <strong>${fn:length(mismatches)} receipt total(s) do not match their line items.</strong>
                <ul style="margin:8px 0 0 18px;">
                    <c:forEach var="m" items="${mismatches}">
                        <li>Receipt #${m.sale_id}: stored RM
                            <fmt:formatNumber value="${m.stored_total}" minFractionDigits="2" maxFractionDigits="2"/>,
                            lines add up to RM
                            <fmt:formatNumber value="${m.calculated_total}" minFractionDigits="2" maxFractionDigits="2"/>
                            (difference RM <fmt:formatNumber value="${m.difference}" minFractionDigits="2" maxFractionDigits="2"/>)
                        </li>
                    </c:forEach>
                </ul>
                <c:choose>
                    <c:when test="${loggedInUser.admin}">
                        <form method="post" action="${pageContext.request.contextPath}/report?action=recalculate"
                              style="margin-top:10px;">
                            <button type="submit" class="btn btn-sm">Recalculate totals</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <p style="margin-top:8px;">Ask an Admin to recalculate the totals.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <div class="stat-grid">
            <div class="stat">
                <div class="label">Total revenue</div>
                <div class="value">RM
                    <fmt:formatNumber value="${totalRevenue}" type="number"
                                      minFractionDigits="2" maxFractionDigits="2"/>
                </div>
            </div>
            <div class="stat">
                <div class="label">Transactions</div>
                <div class="value">${totalTransactions}</div>
            </div>
            <div class="stat">
                <div class="label">Average sale</div>
                <div class="value">RM
                    <fmt:formatNumber value="${overallAverage}" type="number"
                                      minFractionDigits="2" maxFractionDigits="2"/>
                </div>
            </div>
            <div class="stat">
                <div class="label">Best day</div>
                <div class="value" style="font-size:28px;">
                    <fmt:formatDate value="${bestDay.sale_day}" pattern="dd MMM yyyy"/>
                </div>
            </div>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>Date</th>
                        <th class="num">Transactions</th>
                        <th class="num">Revenue (RM)</th>
                        <th class="num">Average sale (RM)</th>
                    </tr>
                </thead>

                <tbody>
                <c:forEach var="d" items="${dailySummary}">
                    <tr>
                        <td><strong><fmt:formatDate value="${d.sale_day}" pattern="EEE, dd MMM yyyy"/></strong></td>
                        <td class="num">${d.transactions}</td>
                        <td class="num">
                            <fmt:formatNumber value="${d.revenue}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                        <td class="num">
                            <fmt:formatNumber value="${d.average_sale}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>

                <%-- Grand total row - calculated by SalesCalculator, not here.
                     The average is weighted by transaction count. --%>
                <tfoot>
                    <tr style="font-weight:700; border-top:2px solid var(--border);">
                        <td>ALL DAYS (${tradingDays})</td>
                        <td class="num">${totalTransactions}</td>
                        <td class="num">
                            <fmt:formatNumber value="${totalRevenue}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                        <td class="num">
                            <fmt:formatNumber value="${overallAverage}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                    </tr>
                </tfoot>
            </table>
        </div>

       

    </c:otherwise>
</c:choose>

<%@ include file="/includes/footer.jspf" %>
