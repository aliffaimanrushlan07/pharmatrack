<%--
    Receipt shown after a completed sale.
    OWNER: AMIR
    STATUS: COMPLETE.

    Print-friendly is a nice touch and costs two lines:
        <style media="print"> .navbar, .footer, .btn { display: none; } </style>
    Printing a receipt live in the demo shows the calculation feature working
    end to end.
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Receipt"/>
<c:set var="activeNav" value="sale"/>
<%@ include file="/includes/header.jspf" %>

<style media="print">
    .navbar, .footer, .receipt-actions { display: none !important; }
    body { background: #fff; }
    .container { max-width: none; padding: 0; }
    .receipt { border: 0; box-shadow: none; }
</style>

<div class="page-header receipt-actions">
    <div>
        <h1>Sale completed</h1>
        <p>The transaction was saved and stock was updated.</p>
    </div>
    <div>
        <button type="button" class="btn btn-secondary" onclick="window.print()">Print receipt</button>
        <a href="${pageContext.request.contextPath}/sale" class="btn">New sale</a>
    </div>
</div>

<article class="card receipt">
    <header class="receipt-header">
        <div>
            <h2>PharmaTrack</h2>
            <p>Pharmacy Inventory and Sales Management System</p>
        </div>
        <div class="receipt-meta">
            <strong>Receipt #${sale.saleId}</strong>
            <span><fmt:formatDate value="${sale.saleDate}" pattern="dd MMM yyyy, hh:mm a"/></span>
            <span>Cashier: <c:out value="${sale.cashierName}"/></span>
        </div>
    </header>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Medicine</th>
                    <th class="num">Quantity</th>
                    <th class="num">Unit price</th>
                    <th class="num">Subtotal</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${sale.items}">
                    <tr>
                        <td><c:out value="${item.medicineName}"/></td>
                        <td class="num">${item.quantity}</td>
                        <td class="num">RM <fmt:formatNumber value="${item.unitPrice}"
                                minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td class="num">RM <fmt:formatNumber value="${item.subtotal}"
                                minFractionDigits="2" maxFractionDigits="2"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="receipt-total">
        <span>${sale.totalUnits} unit(s)</span>
        <strong>Total&nbsp;&nbsp; RM <fmt:formatNumber value="${sale.totalAmount}"
                minFractionDigits="2" maxFractionDigits="2"/></strong>
    </div>
    <p class="receipt-thanks">Thank you. Please keep this receipt for your records.</p>
</article>

<%@ include file="/includes/footer.jspf" %>
