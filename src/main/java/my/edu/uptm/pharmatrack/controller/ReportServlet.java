package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.MedicineDAO;
import my.edu.uptm.pharmatrack.dao.SaleDAO;
import my.edu.uptm.pharmatrack.model.StockValuation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for the Reports page.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Business Logic, Reports &amp; Documentation<br>
 * STATUS: <b>PARTIAL — stock valuation done, sales summary is Yasierul's</b><br>
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
 *   <li>{@code /report?type=sales} — daily sales summary (Yasierul, TODO 1–2)</li>
 * </ul>
 *
 * <p>Stock valuation is the default because it works today. A Reports link
 * that lands on a finished page makes a better first impression during the
 * demo than one that lands on a "not implemented" banner.</p>
 *
 * <p>Order of remaining work:</p>
 * <ol>
 *   <li>TODO 1 — {@code getDailySummary()} from SaleDAO, rendered in sales.jsp</li>
 *   <li>TODO 2 — grand total revenue and overall average sale</li>
 *   <li>TODO 3 — expiring-soon report (SQL ready in 03_sample_queries.sql, E1)</li>
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

    @Override
    public void init() throws ServletException {
        this.saleDAO     = new SaleDAO();
        this.medicineDAO = new MedicineDAO();
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
            } else {
                showStockValuation(request, response);
            }

        } catch (Exception ex) {
            // The examiner must never see a stack trace in the browser.
            request.setAttribute("errorMessage",
                    "Could not build the report: " + ex.getMessage());
            forward(request, response,
                    "sales".equals(type) ? "/report/sales.jsp" : "/report/stock.jsp");
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

    private void showSalesSummary(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // TODO 1 (YASIERUL): request.setAttribute("dailySummary",
        //                    saleDAO.getDailySummary());
        // TODO 2 (YASIERUL): grand total revenue and overall average sale.
        //        StockValuation.grandTotal() next door is the pattern to copy —
        //        the totals belong in Java, not in the JSP.
        request.setAttribute("infoMessage",
            "Sales summary report is not implemented yet (assigned to Yasierul). "
          + "See ReportServlet TODO 1-2 and SaleDAO TODO 5.");

        forward(request, response, "/report/sales.jsp");
    }

    /** One place that knows how to hand over to a JSP. */
    private void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher(view).forward(request, response);
    }
}
