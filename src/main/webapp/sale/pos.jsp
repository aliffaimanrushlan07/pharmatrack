<%--
    Point of sale - the till screen.
    OWNER: YASIERUL (Search, Business Logic & Documentation)
    STATUS: STUB - Yasierul to build.

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
<c:set var="pageTitle" value="New sale"/>
<c:set var="activeNav" value="sale"/>
<%@ include file="/includes/header.jspf" %>

<div class="page-header">
    <div><h1>New sale</h1><p>Add medicines to the basket, then complete the transaction.</p></div>
</div>

<div class="todo-banner">
    <strong>Assigned to Yasierul &mdash; not built yet</strong>
    Needs <code>SaleDAO</code> TODO 1-5 (especially the transaction in
    <code>insertSale</code>) and <code>SaleServlet</code> TODO 1-5.
    Full brief: <code>docs/tasks/yasierul-search-reporting.md</code>
</div>

<%-- TODO (YASIERUL):
     - medicine picker on the left (reuse MedicineDAO.search)
     - basket table on the right, read from the session attribute "currentSale"
     - running total from SalesCalculator.calculateSubtotal()
     - "Complete sale" posts to /sale?action=complete
--%>

<div class="card empty">
    <h3>Point-of-sale screen goes here</h3>
    <p>Basket lives in the session until "Complete sale" writes it in one transaction.</p>
</div>

<%@ include file="/includes/footer.jspf" %>
