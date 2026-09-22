<%--
    Receipt shown after a completed sale.
    OWNER: AMIR
    STATUS: STUB - Amir to build.

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

<div class="page-header"><div><h1>Receipt</h1></div></div>

<div class="todo-banner">
    <strong>Assigned to Amir &mdash; not built yet</strong>
    Show ${sale} with its line items, each subtotal, and the grand total.
</div>

<%@ include file="/includes/footer.jspf" %>
