<%--
    Point of sale - the till screen.
    OWNER: AMIR (Sale & Reporting Module)
    STATUS: COMPLETE.

    This is the most interactive page in the system and the best one to lead
    the live demo with. Layout that works well:

      +-------------------------+-------------------------+
      |  Search / pick medicine |  Basket                 |
      |  (name, price, stock)   |  lines + subtotals      |
      |  [ qty ] [ Add ]        |  ---------------------  |
      |                         |  TOTAL   RM 57.90       |
      |                         |  [ Complete sale ]      |
      +-------------------------+-------------------------+
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="New sale"/>
<c:set var="activeNav" value="sale"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div><h1>New sale</h1><p>Add medicines to the basket, then complete the transaction.</p></div>
</div>

<form method="get" action="${pageContext.request.contextPath}/sale" class="search-bar">
    <input type="text" name="keyword" value="<c:out value='${keyword}'/>"
           placeholder="Search by medicine name or category" aria-label="Search medicines">
    <button type="submit" class="btn">Search</button>
    <c:if test="${not empty keyword}">
        <a href="${pageContext.request.contextPath}/sale" class="btn btn-secondary">Clear search</a>
    </c:if>
</form>

<div class="pos-grid">
    <section class="card pos-products">
        <h2>Available medicines</h2>
        <c:choose>
            <c:when test="${empty medicines}">
                <div class="empty compact-empty">
                    <h3>No medicines found</h3>
                    <p>Try a different name or category.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="product-list">
                    <c:forEach var="medicine" items="${medicines}">
                        <article class="product-row">
                            <div class="product-details">
                                <strong><c:out value="${medicine.name}"/></strong>
                                <span><c:out value="${medicine.category}"/></span>
                                <span>
                                    RM <fmt:formatNumber value="${medicine.price}"
                                                         minFractionDigits="2" maxFractionDigits="2"/>
                                    &middot; ${medicine.quantityInStock} in stock
                                </span>
                            </div>
                            <c:choose>
                                <c:when test="${medicine.quantityInStock gt 0}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/sale?action=add"
                                          class="add-to-cart-form">
                                        <input type="hidden" name="medicineId" value="${medicine.medicineId}">
                                        <input type="number" name="quantity" value="1" min="1"
                                               max="${medicine.quantityInStock}"
                                               aria-label="Quantity for ${fn:escapeXml(medicine.name)}" required>
                                        <button type="submit" class="btn btn-sm">Add</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-low">Out of stock</span>
                                </c:otherwise>
                            </c:choose>
                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <section class="card pos-basket">
        <div class="basket-heading">
            <h2>Basket</h2>
            <span class="badge badge-ok">${currentSale.totalUnits} unit(s)</span>
        </div>
        <c:choose>
            <c:when test="${empty currentSale.items}">
                <div class="empty compact-empty">
                    <h3>Your basket is empty</h3>
                    <p>Add a medicine from the list to begin.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap basket-table">
                    <table>
                        <thead>
                            <tr>
                                <th>Medicine</th>
                                <th class="num">Qty</th>
                                <th class="num">Price</th>
                                <th class="num">Subtotal</th>
                                <th><span class="sr-only">Action</span></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${currentSale.items}">
                                <tr>
                                    <td><c:out value="${item.medicineName}"/></td>
                                    <td class="num">${item.quantity}</td>
                                    <td class="num">RM <fmt:formatNumber value="${item.unitPrice}"
                                            minFractionDigits="2" maxFractionDigits="2"/></td>
                                    <td class="num">RM <fmt:formatNumber value="${item.subtotal}"
                                            minFractionDigits="2" maxFractionDigits="2"/></td>
                                    <td class="num">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/sale?action=remove">
                                            <input type="hidden" name="medicineId" value="${item.medicineId}">
                                            <button type="submit" class="btn btn-danger btn-sm">Remove</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="basket-total">
                    <span>Total</span>
                    <strong>RM <fmt:formatNumber value="${currentSale.totalAmount}"
                            minFractionDigits="2" maxFractionDigits="2"/></strong>
                </div>
                <div class="basket-actions">
                    <form method="post" action="${pageContext.request.contextPath}/sale?action=clear">
                        <button type="submit" class="btn btn-secondary">Clear basket</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/sale?action=complete">
                        <button type="submit" class="btn">Complete sale</button>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</div>

<%@ include file="/includes/footer.jspf" %>
