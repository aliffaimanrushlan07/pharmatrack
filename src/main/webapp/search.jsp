<%--
    Unified search page.
    OWNER: YASIERUL (Search, Business Logic & Documentation)
    STATUS: COMPLETE

    Two modes, chosen by ?type=:
      medicine - keyword matched against name or category (MedicineDAO.search)
      sale     - date range (SaleDAO.findByDateRange), optional keyword that
                 matches a receipt number or cashier name

    No Java in this file. SearchServlet puts results, resultCount, keyword,
    type, startDate and endDate in request attributes; this page only shows
    them. Every value goes through <c:out> so user input is HTML-escaped.
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Search"/>
<c:set var="activeNav" value="search"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div><h1>Search records</h1><p>Find medicines by name or category, or sales by date.</p></div>
</div>

<form action="${pageContext.request.contextPath}/search" method="get" class="search-bar" id="searchForm">
    <select name="type" id="searchType" style="max-width:160px" aria-label="What to search">
        <option value="medicine" ${type ne 'sale' ? 'selected' : ''}>Medicines</option>
        <option value="sale"     ${type eq 'sale' ? 'selected' : ''}>Sales</option>
    </select>

    <input type="text" name="keyword" id="keyword"
           placeholder="${type eq 'sale' ? 'Receipt # or cashier name (optional)' : 'Medicine name or category...'}"
           value="<c:out value='${keyword}'/>" aria-label="Keyword">

    <span class="date-range" id="dateRange" style="${type eq 'sale' ? '' : 'display:none'}">
        <label for="startDate" class="sr-only">From</label>
        <input type="date" name="startDate" id="startDate" value="<c:out value='${startDate}'/>"
               title="From date">
        <span class="date-sep">to</span>
        <label for="endDate" class="sr-only">To</label>
        <input type="date" name="endDate" id="endDate" value="<c:out value='${endDate}'/>"
               title="To date">
    </span>

    <button type="submit" class="btn">Search</button>
    <c:if test="${searched}">
        <a href="${pageContext.request.contextPath}/search" class="btn btn-secondary">Clear</a>
    </c:if>
</form>

<%-- Show the date fields only in sales mode. Pure progressive enhancement:
     without JavaScript the fields simply stay as the server rendered them. --%>
<script>
    (function () {
        var type = document.getElementById('searchType');
        var range = document.getElementById('dateRange');
        var keyword = document.getElementById('keyword');
        type.addEventListener('change', function () {
            var sale = type.value === 'sale';
            range.style.display = sale ? '' : 'none';
            keyword.placeholder = sale ? 'Receipt # or cashier name (optional)'
                                       : 'Medicine name or category...';
        });
    })();
</script>

<c:choose>

    <%-- First visit: nothing searched yet. --%>
    <c:when test="${not searched}">
        <div class="card empty">
            <h3>What are you looking for?</h3>
            <p>Type part of a medicine name or category, such as <em>para</em> or
               <em>antibiotic</em>. Switch to <strong>Sales</strong> to find receipts
               between two dates. Leave the box empty to list everything.</p>
        </div>
    </c:when>

    <%-- Validation or database error: header.jspf already shows the message. --%>
    <c:when test="${not empty errorMessage}"></c:when>

    <%-- No results found for "X". --%>
    <c:when test="${empty results}">
        <div class="card empty">
            <c:choose>
                <c:when test="${type eq 'sale'}">
                    <h3>No sales found
                        <c:if test="${not empty startDate}">
                            between <c:out value="${startDate}"/> and <c:out value="${endDate}"/>
                        </c:if>
                        <c:if test="${not empty keyword}">
                            for "<c:out value='${keyword}'/>"
                        </c:if>
                    </h3>
                    <p>Try a wider date range, or clear the keyword.</p>
                </c:when>
                <c:otherwise>
                    <h3>No results found for "<c:out value='${keyword}'/>"</h3>
                    <p>Check the spelling, or try a shorter keyword such as the first few letters.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>

    <%-- ------------------------- MEDICINE RESULTS ------------------------ --%>
    <c:when test="${type ne 'sale'}">
        <p class="result-count">
            <strong>${resultCount}</strong> result${resultCount == 1 ? '' : 's'}
            <c:choose>
                <c:when test="${not empty keyword}">for "<c:out value='${keyword}'/>"</c:when>
                <c:otherwise>(all medicines)</c:otherwise>
            </c:choose>
        </p>

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
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="m" items="${results}">
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
                            <span class="badge ${m.stockStatusCssClass}">
                                <c:out value="${m.stockStatus}"/>
                            </span>
                        </td>
                        <td>
                            <c:if test="${not empty m.expiryDate}">
                                <fmt:formatDate value="${m.expiryDate}" pattern="dd MMM yyyy"/>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:when>

    <%-- --------------------------- SALE RESULTS -------------------------- --%>
    <c:otherwise>
        <p class="result-count">
            <strong>${resultCount}</strong> sale${resultCount == 1 ? '' : 's'}
            <c:choose>
                <c:when test="${not empty startDate}">
                    between <c:out value="${startDate}"/> and <c:out value="${endDate}"/>
                </c:when>
                <c:otherwise>(all dates)</c:otherwise>
            </c:choose>
            <c:if test="${not empty keyword}">matching "<c:out value='${keyword}'/>"</c:if>
            &middot; total RM
            <fmt:formatNumber value="${salesTotal}" type="number"
                              minFractionDigits="2" maxFractionDigits="2"/>
            &middot; average RM
            <fmt:formatNumber value="${salesAverage}" type="number"
                              minFractionDigits="2" maxFractionDigits="2"/>
        </p>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>Receipt #</th>
                        <th>Date &amp; time</th>
                        <th>Cashier</th>
                        <th class="num">Total (RM)</th>
                        <th style="width:110px"></th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="s" items="${results}">
                    <tr>
                        <td><strong>#${s.saleId}</strong></td>
                        <td><fmt:formatDate value="${s.saleDate}" pattern="dd MMM yyyy, hh:mm a"/></td>
                        <td><c:out value="${s.cashierName}"/></td>
                        <td class="num">
                            <fmt:formatNumber value="${s.totalAmount}" type="number"
                                              minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/sale?action=receipt&id=${s.saleId}"
                               class="btn btn-sm btn-secondary">View receipt</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/includes/footer.jspf" %>
