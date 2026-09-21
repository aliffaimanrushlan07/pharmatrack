<%--
    Low-stock report.
    OWNER: YASIERUL (Search, Business Logic & Documentation)
    STATUS: COMPLETE - ReportServlet already supplies ${lowStockList}.

    A second worked example, and evidence of the calculation feature: the
    shortfall column below is computed, not stored.
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Low stock report"/>
<c:set var="activeNav" value="report"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Low stock report</h1>
        <p>Medicines at or below their reorder level.</p>
    </div>
    <a href="${pageContext.request.contextPath}/report?type=sales" class="btn btn-secondary">
        Sales summary
    </a>
</div>

<c:choose>
    <c:when test="${empty lowStockList}">
        <div class="card empty">
            <h3>Everything is in stock</h3>
            <p>No medicine has fallen to its reorder level.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="alert alert-warning">
            <strong>${fn:length(lowStockList)}</strong> medicine(s) need reordering.
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>#</th><th>Medicine</th><th>Category</th>
                        <th class="num">In stock</th><th class="num">Reorder at</th>
                        <th class="num">Shortfall</th><th>Supplier</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="m" items="${lowStockList}">
                    <tr>
                        <td>${m.medicineId}</td>
                        <td><strong><c:out value="${m.name}"/></strong></td>
                        <td><c:out value="${m.category}"/></td>
                        <td class="num">${m.quantityInStock}</td>
                        <td class="num">${m.reorderLevel}</td>
                        <%-- CALCULATED in the view from two stored values. --%>
                        <td class="num">
                            <span class="badge badge-low">
                                ${m.reorderLevel - m.quantityInStock}
                            </span>
                        </td>
                        <td><c:out value="${m.supplierName}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/includes/footer.jspf" %>
