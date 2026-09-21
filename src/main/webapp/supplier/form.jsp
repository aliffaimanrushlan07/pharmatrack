<%--
    Supplier add/edit form.
    OWNER: AMIR (CRUD & Interface Module)
    STATUS: STUB - Amir to build.

    Copy medicine/form.jsp. Same hidden-id trick: supplierId 0 = insert,
    anything else = update.
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="${empty supplier ? 'Add supplier' : 'Edit supplier'}"/>
<c:set var="activeNav" value="supplier"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div><h1>${empty supplier ? 'Add supplier' : 'Edit supplier'}</h1></div>
    <a href="${pageContext.request.contextPath}/supplier" class="btn btn-secondary">Back to list</a>
</div>

<div class="todo-banner">
    <strong>Assigned to Amir &mdash; not built yet</strong>
    Fields needed: name (required), contact person, phone, email, address.
    Validate email and phone with <code>ValidationUtil</code> TODO 1-2.
</div>

<%-- TODO (AMIR): build the form, posting to /supplier?action=save --%>

<%@ include file="/includes/footer.jspf" %>
