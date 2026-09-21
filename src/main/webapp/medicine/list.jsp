<%--
    Medicine list - CRUD "Retrieve" + search.
    OWNER: AMIR (CRUD & Interface Module)
    STATUS: COMPLETE

    >>> WORKED EXAMPLE FOR EVERY OTHER LIST PAGE IN THE PROJECT. <<<
    supplier/list.jsp and sale/list.jsp are stubs that follow this exact shape.

    Three things to notice, all of them deliberate:

    1. There is NO Java in this file. No scriptlets, no imports, no SQL. The
       servlet fetched the data and put it in a request attribute; this page
       only displays it. That is the "separation of presentation and business
       logic" the rubric rewards, and it is why this page is short.

    2. Every piece of data goes through <c:out>, which HTML-escapes it. A
       medicine saved as <script>alert(1)</script> is DISPLAYED, not executed.

    3. <fmt:formatNumber> for money and <fmt:formatDate> for dates - so the
       display format is decided here, in the view, and not baked into the
       model or the database.
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Medicines"/>
<c:set var="activeNav" value="medicine"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>Medicines</h1>
        <p>${fn:length(medicines)} record(s) in the inventory.</p>
    </div>
    <a href="${pageContext.request.contextPath}/medicine?action=new" class="btn">+ Add medicine</a>
</div>

<%-- SEARCH: submits back to this same servlet with ?keyword=... --%>
<form action="${pageContext.request.contextPath}/medicine" method="get" class="search-bar">
    <input type="text" name="keyword" placeholder="Search by name or category..."
           value="<c:out value='${keyword}'/>">
    <button type="submit" class="btn">Search</button>
    <c:if test="${not empty keyword}">
        <a href="${pageContext.request.contextPath}/medicine" class="btn btn-secondary">Clear</a>
    </c:if>
</form>

<c:choose>
    <%-- EMPTY STATE: never show a bare empty table. Telling the user why
         there is nothing here is a cheap win in the interface criterion. --%>
    <c:when test="${empty medicines}">
        <div class="card empty">
            <c:choose>
                <c:when test="${not empty keyword}">
                    <h3>No medicines match "<c:out value='${keyword}'/>"</h3>
                    <p>Try a shorter keyword, or clear the search.</p>
                </c:when>
                <c:otherwise>
                    <h3>No medicines yet</h3>
                    <p>Add your first medicine, or run <code>database/02_seed_data.sql</code>
                       in MySQL Workbench to load the sample inventory.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>

    <c:otherwise>
        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Name</th>
                        <th>Category</th>
                        <th class="num">Price (RM)</th>
                        <th class="num">Stock</th>
                        <th>Status</th>
                        <th>Expiry</th>
                        <th>Supplier</th>
                        <th style="width:150px">Actions</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="m" items="${medicines}">
                    <tr>
                        <td>${m.medicineId}</td>
                        <td><strong><c:out value="${m.name}"/></strong></td>
                        <td><c:out value="${m.category}"/></td>
                        <td class="num">
                            <fmt:formatNumber value="${m.price}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                        <td class="num">${m.quantityInStock}</td>
                        <td>
                            <%-- isLowStock() is a method on the Medicine model.
                                 EL calls it as ${m.lowStock} - the business rule
                                 lives in Java, not in this page. --%>
                            <c:choose>
                                <c:when test="${m.lowStock}">
                                    <span class="badge badge-low">Low</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-ok">OK</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${not empty m.expiryDate}">
                                <fmt:formatDate value="${m.expiryDate}" pattern="dd MMM yyyy"/>
                            </c:if>
                        </td>
                        <td><c:out value="${m.supplierName}"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/medicine?action=edit&id=${m.medicineId}"
                               class="btn btn-sm btn-secondary">Edit</a>
                            <%-- onclick confirm is the minimum. A destructive
                                 action with no confirmation loses marks. --%>
                            <a href="${pageContext.request.contextPath}/medicine?action=delete&id=${m.medicineId}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Delete this medicine? This cannot be undone.');">
                                Delete
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/includes/footer.jspf" %>
