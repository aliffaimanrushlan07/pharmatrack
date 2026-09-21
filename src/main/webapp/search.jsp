<%--
    Unified search page.
    OWNER: YASIERUL (Search, Business Logic & Documentation)
    STATUS: STUB - Yasierul to build.

    Rubric item 4 - a whole 10-mark criterion for this one page, and the DAO
    method it needs (MedicineDAO.search) is already written and tested.
    Do this one first.
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

<form action="${pageContext.request.contextPath}/search" method="get" class="search-bar">
    <input type="text" name="keyword" placeholder="Enter a keyword..."
           value="<c:out value='${keyword}'/>">
    <select name="type" style="max-width:180px">
        <option value="medicine" ${type eq 'medicine' ? 'selected' : ''}>Medicines</option>
        <option value="sale"     ${type eq 'sale'     ? 'selected' : ''}>Sales</option>
    </select>
    <button type="submit" class="btn">Search</button>
</form>

<div class="todo-banner">
    <strong>Assigned to Yasierul &mdash; not built yet</strong>
    The search box above already posts correctly. What is missing is
    <code>SearchServlet</code> TODO 1-4 and the results table below.
    <code>MedicineDAO.search(keyword)</code> is done &mdash; just call it.
</div>

<%-- TODO (YASIERUL): render ${results} in a table, and show
     "N result(s) for X" above it. Reuse the markup from medicine/list.jsp. --%>

<%@ include file="/includes/footer.jspf" %>
