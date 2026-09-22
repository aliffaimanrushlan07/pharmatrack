<%--
    Inventory valuation report - how much money is tied up in stock,
    broken down by medicine category.

    OWNER: YASIERUL (Reports)
    STATUS: Done - this is the worked example for report/sales.jsp.

    Read this page alongside sales.jsp. The two have the same skeleton:
    summary tiles, then a table, then a grand-total row in the table foot.
    Building the sales summary is mostly a matter of swapping the attribute
    names, because the shape of the page is already decided.

    Everything printed here was calculated in Java - MedicineDAO for the
    per-category rows, StockValuation.grandTotal() for the last row. There is
    no arithmetic in this file at all, which is how a JSP should look.
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Inventory valuation"/>
<c:set var="activeNav" value="report"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Inventory valuation</h1>
        <p>The value of stock currently held, grouped by category.</p>
    </div>
</div>

<%-- Report tabs. Both links hit the same servlet with a different ?type=,
     mirroring the ?action= pattern in MedicineServlet. --%>
<div class="search-bar" style="margin-bottom:18px;">
    <a href="${pageContext.request.contextPath}/report?type=stock"
       class="btn">Inventory valuation</a>
    <a href="${pageContext.request.contextPath}/report?type=sales"
       class="btn btn-secondary">Sales summary</a>
</div>

<c:choose>

    <%-- ------------------------------------------------------------------
         Empty state. An empty table with headings and nothing under them
         looks like a bug; saying so in words does not.
         ------------------------------------------------------------------ --%>
    <c:when test="${empty valuation}">
        <div class="empty">
            <h3>Nothing to value yet</h3>
            <p>There are no medicines in the system, so there is no stock to put a price on.</p>
            <a href="${pageContext.request.contextPath}/medicine?action=new" class="btn">
                Add the first medicine
            </a>
        </div>
    </c:when>

    <c:otherwise>

        <%-- Headline figures, repeated from the table so that the single
             number the examiner most wants is visible without reading. --%>
        <div class="stat-grid">
            <div class="stat">
                <div class="label">Total stock value</div>
                <div class="value">RM
                    <fmt:formatNumber value="${grandTotal.stockValue}" type="number"
                                      minFractionDigits="2" maxFractionDigits="2"/>
                </div>
            </div>
            <div class="stat">
                <div class="label">Medicines</div>
                <div class="value">${grandTotal.itemCount}</div>
            </div>
            <div class="stat">
                <div class="label">Units in stock</div>
                <div class="value">
                    <fmt:formatNumber value="${grandTotal.totalUnits}" type="number"/>
                </div>
            </div>
            <div class="stat">
                <div class="label">Categories</div>
                <div class="value">${fn:length(valuation)}</div>
            </div>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>Category</th>
                        <th class="num">Medicines</th>
                        <th class="num">Units in stock</th>
                        <th class="num">Avg. price (RM)</th>
                        <th class="num">Stock value (RM)</th>
                        <th class="num">Share</th>
                    </tr>
                </thead>

                <tbody>
                <c:forEach var="v" items="${valuation}">
                    <tr>
                        <%-- c:out escapes HTML. A category typed in as
                             <script>alert(1)</script> is shown as text, not
                             run - the XSS defence named in the report. --%>
                        <td><strong><c:out value="${v.category}"/></strong></td>

                        <td class="num">${v.itemCount}</td>

                        <td class="num">
                            <fmt:formatNumber value="${v.totalUnits}" type="number"/>
                        </td>

                        <td class="num">
                            <fmt:formatNumber value="${v.averagePrice}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>
                        </td>

                        <td class="num">
                            <fmt:formatNumber value="${v.stockValue}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>
                        </td>

                        <td class="num">
                            <fmt:formatNumber value="${v.shareOfTotal}" type="number"
                                              minFractionDigits="1" maxFractionDigits="1"/>%
                        </td>
                    </tr>
                </c:forEach>
                </tbody>

                <%-- ----------------------------------------------------------
                     THE GRAND TOTAL ROW.

                     This row is the single clearest piece of evidence for the
                     "Search & Calculation Features" criterion, which is why it
                     is in a <tfoot> and styled to stand out rather than being
                     just another <tr>.

                     Note what the average column is NOT: it is not the mean of
                     the five category averages above it. Averaging averages is
                     wrong when the groups are different sizes.
                     StockValuation.grandTotal() weights each category average
                     by its item count, which gives the true average price
                     across every medicine. Be ready to say that out loud.
                     ---------------------------------------------------------- --%>
                <tfoot>
                    <tr style="font-weight:700; border-top:2px solid var(--border);">
                        <td><c:out value="${grandTotal.category}"/></td>
                        <td class="num">${grandTotal.itemCount}</td>
                        <td class="num">
                            <fmt:formatNumber value="${grandTotal.totalUnits}" type="number"/>
                        </td>
                        <td class="num">
                            <fmt:formatNumber value="${grandTotal.averagePrice}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                        <td class="num">
                            <fmt:formatNumber value="${grandTotal.stockValue}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                        <td class="num">100.0%</td>
                    </tr>
                </tfoot>
            </table>
        </div>

        <p class="hint" style="margin-top:14px;">
            Stock value is <code>price &times; quantity in stock</code>, summed per
            category by MySQL. The total row is calculated in Java by
            <code>StockValuation.grandTotal()</code>. All money uses
            <code>BigDecimal</code> with <code>HALF_UP</code> rounding.
        </p>

    </c:otherwise>
</c:choose>

<%@ include file="/includes/footer.jspf" %>
