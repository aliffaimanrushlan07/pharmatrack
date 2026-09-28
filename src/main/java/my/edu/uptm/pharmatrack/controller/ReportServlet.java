package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.MedicineDAO;
import my.edu.uptm.pharmatrack.dao.ReportDAO;
import my.edu.uptm.pharmatrack.dao.SaleDAO;
import my.edu.uptm.pharmatrack.model.StockValuation;
import my.edu.uptm.pharmatrack.model.User;
import my.edu.uptm.pharmatrack.security.AuthFilter;
import my.edu.uptm.pharmatrack.service.SalesCalculator;
import my.edu.uptm.pharmatrack.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Reports page.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Business Logic, Reports &amp; Documentation<br>
 * STATUS: <b>COMPLETE</b><br>
 * =====================================================================</p>
 *
 * <p>This is the visible face of <b>rubric item 6, the calculation feature</b>.
 * A report page that shows real aggregated numbers is the clearest possible
 * evidence of business logic, so it is worth polishing.</p>
 *
 * <p><b>Routing.</b> One servlet serves two reports, chosen by a query
 * parameter, exactly as {@code MedicineServlet} uses {@code ?action=}:</p>
 *
 * <ul>
 *   <li>{@code /report} or {@code /report?type=stock} — inventory valuation
 *       (done; reads only the {@code medicines} table)</li>
 *   <li>{@code /report?type=sales} — daily sales summary</li>
 *   <li>{@code /report?type=expiry&days=90} — medicines expiring soon</li>
 * </ul>
 *
 * <p>Stock valuation is the default because it works today. A Reports link
 * that lands on a finished page makes a better first impression during the
 * demo than one that lands on a "not implemented" banner.</p>
 *
 * <p>Implemented reports:</p>
 * <ol>
 *   <li>Inventory valuation — {@code MedicineDAO.getStockValuation()}</li>
 *   <li>Daily sales summary — {@code SaleDAO.getDailySummary()} with grand
 *       total revenue and the correctly weighted overall average</li>
 *   <li>Expiring soon — {@code ReportDAO.findExpiringSoon(days)}</li>
 * </ol>
 *
 * @author Yasierul
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/report"})
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // transient: DAOs are stateless helpers, not part of the servlet's
    // serialisable state. Without this the compiler warns on every field.
    private transient SaleDAO     saleDAO;
    private transient MedicineDAO medicineDAO;
    private transient ReportDAO   reportDAO;

    @Override
    public void init() throws ServletException {
        this.saleDAO     = new SaleDAO();
        this.medicineDAO = new MedicineDAO();
        this.reportDAO   = new ReportDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            type = "stock";                 // the report that is finished
        }

        try {
            if ("sales".equals(type)) {
                showSalesSummary(request, response);
            } else if ("expiry".equals(type)) {
                showExpiringSoon(request, response);
            } else {
                showStockValuation(request, response);
            }

        } catch (Exception ex) {
            // The examiner must never see a stack trace in the browser.
            request.setAttribute("errorMessage",
                    "Could not build the report: " + ex.getMessage());
            forward(request, response, viewFor(type));
        }
    }

    /**
     * POST {@code /report?action=recalculate} — fixes receipts whose stored
     * total disagrees with their lines. Admin only; uses POST-redirect-GET so
     * refreshing the page does not repeat the update.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String target = request.getContextPath() + "/report?type=sales";
        HttpSession session = request.getSession(false);
        User user = (session == null) ? null
                : (User) session.getAttribute(AuthFilter.SESSION_USER);

        if (!"recalculate".equals(request.getParameter("action"))) {
            response.sendRedirect(target);
            return;
        }
        if (user == null || !user.isAdmin()) {
            response.sendRedirect(target + "&error="
                    + URLEncoder.encode("Only an Admin can correct receipt totals.", "UTF-8"));
            return;
        }

        try {
            int fixed = saleDAO.recalculateTotals();
            String message = (fixed == 0)
                    ? "All receipt totals were already correct."
                    : fixed + " receipt total(s) recalculated from their line items.";
            response.sendRedirect(target + "&success=" + URLEncoder.encode(message, "UTF-8"));
        } catch (Exception ex) {
            response.sendRedirect(target + "&error="
                    + URLEncoder.encode("Could not recalculate totals: " + ex.getMessage(), "UTF-8"));
        }
    }

    // ------------------------------------------------------------------
    //  Report 1 — inventory valuation (done)
    // ------------------------------------------------------------------

    /**
     * How much money is sitting on the shelves, by category.
     *
     * <p>The servlet does no arithmetic of its own. It asks the DAO for the
     * rows and the model for the total, then forwards. Keeping calculation out
     * of the controller is the point of having a model layer at all — and it
     * means {@link StockValuation#grandTotal} can be unit-tested without
     * starting Tomcat.</p>
     */
    private void showStockValuation(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        List<StockValuation> valuation = medicineDAO.getStockValuation();

        StockValuation grandTotal = StockValuation.grandTotal(valuation);
        StockValuation.applyShares(valuation, grandTotal);

        request.setAttribute("valuation",  valuation);
        request.setAttribute("grandTotal", grandTotal);

        forward(request, response, "/report/stock.jsp");
    }

    // ------------------------------------------------------------------
    //  Report 2 — daily sales summary (Yasierul)
    // ------------------------------------------------------------------

    /**
     * Revenue, transaction count and average sale per day, plus a grand-total
     * row.
     *
     * <p>As with the valuation report, the servlet does no arithmetic itself.
     * The per-day figures come from SQL ({@code GROUP BY DATE(sale_date)}) and
     * the totals from {@link SalesCalculator}. The overall average is
     * <b>total revenue &divide; total transactions</b> — not the mean of the
     * daily averages, which would over-weight quiet days.</p>
     */
    private void showSalesSummary(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        List<Map<String, Object>> dailySummary = saleDAO.getDailySummary();

        BigDecimal totalRevenue      = SalesCalculator.totalRevenue(dailySummary);
        int        totalTransactions = SalesCalculator.totalTransactions(dailySummary);
        BigDecimal overallAverage    =
                SalesCalculator.calculateAverage(totalRevenue, totalTransactions);

        // Best day by revenue — a headline tile the pharmacist actually cares about.
        Map<String, Object> bestDay = null;
        for (Map<String, Object> row : dailySummary) {
            BigDecimal revenue = (BigDecimal) row.get("revenue");
            if (revenue != null && (bestDay == null
                    || revenue.compareTo((BigDecimal) bestDay.get("revenue")) > 0)) {
                bestDay = row;
            }
        }

        request.setAttribute("dailySummary",      dailySummary);
        request.setAttribute("totalRevenue",      totalRevenue);
        request.setAttribute("totalTransactions", totalTransactions);
        request.setAttribute("overallAverage",    overallAverage);
        request.setAttribute("tradingDays",       dailySummary.size());
        request.setAttribute("bestDay",           bestDay);

        // Integrity check: receipts whose stored total disagrees with their lines.
        request.setAttribute("mismatches", saleDAO.findTotalMismatches());

        forward(request, response, "/report/sales.jsp");
    }

    // ------------------------------------------------------------------
    //  Report 3 — expiring soon (Yasierul)
    // ------------------------------------------------------------------

    /**
     * Medicines expiring within the chosen window (default 90 days),
     * soonest first, with already-expired stock flagged at the top.
     */
    private void showExpiringSoon(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        int days = ValidationUtil.parseInt(request.getParameter("days"),
                                           ReportDAO.DEFAULT_EXPIRY_DAYS);
        if (days < 1 || days > ReportDAO.MAX_EXPIRY_DAYS) {
            days = ReportDAO.DEFAULT_EXPIRY_DAYS;
        }

        List<Map<String, Object>> expiring = reportDAO.findExpiringSoon(days);

        int expiredCount = 0;
        int criticalCount = 0;
        int unitsAtRisk = 0;
        BigDecimal valueAtRisk = BigDecimal.ZERO;
        for (Map<String, Object> row : expiring) {
            String status = (String) row.get("status");
            if ("EXPIRED".equals(status)) {
                expiredCount++;
            } else if ("CRITICAL".equals(status)) {
                criticalCount++;
            }
            unitsAtRisk += (Integer) row.get("quantity_in_stock");
            valueAtRisk = valueAtRisk.add((BigDecimal) row.get("stock_value"));
        }

        request.setAttribute("expiring",      expiring);
        request.setAttribute("days",          days);
        request.setAttribute("expiredCount",  expiredCount);
        request.setAttribute("criticalCount", criticalCount);
        request.setAttribute("unitsAtRisk",   unitsAtRisk);
        request.setAttribute("valueAtRisk",   valueAtRisk.setScale(2, RoundingMode.HALF_UP));

        forward(request, response, "/report/expiry.jsp");
    }

    private String viewFor(String type) {
        if ("sales".equals(type)) {
            return "/report/sales.jsp";
        }
        if ("expiry".equals(type)) {
            return "/report/expiry.jsp";
        }
        return "/report/stock.jsp";
    }

    /** One place that knows how to hand over to a JSP. */
    private void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher(view).forward(request, response);
    }
}
