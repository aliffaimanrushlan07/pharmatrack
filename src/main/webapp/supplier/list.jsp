<%--
    Supplier list.
    OWNER: AMIR (CRUD & Interface Module)
    STATUS: STUB - Amir to build.

    Copy medicine/list.jsp and swap the entity. Suppliers have fewer columns
    and no badges, so this is the simpler of the two.
--%>
<c:set var="pageTitle" value="Suppliers"/>
<c:set var="activeNav" value="supplier"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div><h1>Suppliers</h1><p>Manage the companies that supply the pharmacy.</p></div>
    <a href="${pageContext.request.contextPath}/supplier?action=new" class="btn">+ Add supplier</a>
</div>

<div class="todo-banner">
    <strong>Assigned to Amir &mdash; not built yet</strong>
    Build this page by copying <code>medicine/list.jsp</code>.
    Needs <code>SupplierDAO</code> TODO 1-6 and <code>SupplierServlet</code> TODO 1-6 first.
    Full brief: <code>docs/tasks/amir-crud-ui.md</code>
</div>

<%-- TODO (AMIR): delete the banner above and the placeholder below, then
     render ${suppliers} in a table the way medicine/list.jsp does.
     Columns: # / Name / Contact person / Phone / Email / Medicines / Actions --%>

<div class="card empty">
    <h3>Supplier table goes here</h3>
    <p>Loop over <code>${suppliers}</code> with <code>&lt;c:forEach&gt;</code>.</p>
</div>

<%@ include file="/includes/footer.jspf" %>
