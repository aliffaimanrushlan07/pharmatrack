<%--
    Medicine add/edit form - CRUD "Create" and "Update".
    OWNER: AMIR (CRUD & Interface Module)
    STATUS: COMPLETE

    >>> WORKED EXAMPLE FOR EVERY OTHER FORM PAGE. <<<

    ONE form handles both add and edit. The difference is a hidden medicineId:
    zero means insert, anything else means update. Two near-identical JSPs
    would be two places to fix every future change.

    Note the value="..." on each input. On an edit they pre-fill from the
    model; after a failed validation they keep whatever the user typed, so
    nobody has to retype a long form because of one bad field. Losing user
    input on a validation error is a usability bug the rubric notices.
--%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="${empty medicine ? 'Add medicine' : 'Edit medicine'}"/>
<c:set var="activeNav" value="medicine"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div>
        <h1>${empty medicine ? 'Add medicine' : 'Edit medicine'}</h1>
        <p>Fields marked <span class="req">*</span> are required.</p>
    </div>
    <a href="${pageContext.request.contextPath}/medicine" class="btn btn-secondary">Back to list</a>
</div>

<div class="card">
    <form action="${pageContext.request.contextPath}/medicine?action=save" method="post">

        <%-- 0 = insert, >0 = update. --%>
        <input type="hidden" name="medicineId" value="${empty medicine ? 0 : medicine.medicineId}">

        <div class="form-grid">

            <div class="form-row">
                <label for="name">Medicine name <span class="req">*</span></label>
                <input type="text" id="name" name="name" maxlength="100" required
                       value="<c:out value='${medicine.name}'/>"
                       placeholder="e.g. Paracetamol 500mg (100 tabs)">
            </div>

            <div class="form-row">
                <label for="category">Category</label>
                <input type="text" id="category" name="category" maxlength="50"
                       value="<c:out value='${medicine.category}'/>"
                       placeholder="e.g. Analgesic">
            </div>

            <div class="form-row">
                <label for="price">Unit price (RM) <span class="req">*</span></label>
                <input type="number" id="price" name="price" step="0.01" min="0" required
                       value="${empty medicine ? '0.00' : medicine.price}">
            </div>

            <div class="form-row">
                <label for="quantityInStock">Quantity in stock <span class="req">*</span></label>
                <input type="number" id="quantityInStock" name="quantityInStock" min="0" required
                       value="${empty medicine ? 0 : medicine.quantityInStock}">
            </div>

            <div class="form-row">
                <label for="reorderLevel">Reorder level <span class="req">*</span></label>
                <input type="number" id="reorderLevel" name="reorderLevel" min="0" required
                       value="${empty medicine ? 10 : medicine.reorderLevel}">
                <div class="hint">Flagged as low stock at or below this figure.</div>
            </div>

            <div class="form-row">
                <label for="expiryDate">Expiry date</label>
                <input type="date" id="expiryDate" name="expiryDate"
                       value="${medicine.expiryDate}">
            </div>

            <div class="form-row">
                <label for="supplierId">Supplier</label>
                <select id="supplierId" name="supplierId">
                    <option value="0">-- none --</option>
                    <c:forEach var="s" items="${suppliers}">
                        <option value="${s.supplierId}"
                                ${medicine.supplierId eq s.supplierId ? 'selected' : ''}>
                            <c:out value="${s.name}"/>
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${empty suppliers}">
                    <div class="hint" style="color:#d97706">
                        Supplier list is empty &mdash; <strong>SupplierDAO.findAll()</strong>
                        is not implemented yet (Amir, TODO 1).
                    </div>
                </c:if>
            </div>

        </div>

        <div class="form-actions">
            <button type="submit" class="btn">
                ${empty medicine ? 'Add medicine' : 'Save changes'}
            </button>
            <a href="${pageContext.request.contextPath}/medicine" class="btn btn-secondary">Cancel</a>
        </div>

    </form>
</div>

<%@ include file="/includes/footer.jspf" %>
