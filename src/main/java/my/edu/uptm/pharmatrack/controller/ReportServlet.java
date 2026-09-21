package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.MedicineDAO;
import my.edu.uptm.pharmatrack.dao.SaleDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller for the low-stock and sales report pages.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Search, Business Logic &amp; Documentation<br>
 * STATUS: <b>PARTIAL</b> — low-stock report works (its DAO method is done);
 * the sales summary is a TODO.<br>
 * =====================================================================</p>
 *
 * <p>This is the visible face of <b>rubric item 6, the calculation feature</b>.
 * A report page that shows real aggregated numbers is the clearest possible
 * evidence of business logic, so it is worth polishing.</p>
 *
 * <p>Order of work:</p>
 * <ol>
 *   <li><b>DONE</b> — {@code ?type=lowstock}, using
 *       {@link MedicineDAO#findLowStock()}</li>
 *   <li>TODO 1 — {@code ?type=sales}, using {@code SaleDAO.getDailySummary()}</li>
 *   <li>TODO 2 — show grand total revenue and overall average sale</li>
 *   <li>TODO 3 — expiring-soon report (medicines within 90 days of expiry;
 *       the SQL is ready in database/03_sample_queries.sql, F2)</li>
 * </ol>
 *
 * @author Yasierul
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/report"})
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient MedicineDAO medicineDAO;
    private transient SaleDAO saleDAO;

    @Override
    public void init() throws ServletException {
        this.medicineDAO = new MedicineDAO();
        this.saleDAO     = new SaleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        if (type == null) {
            type = "lowstock";
        }

        try {
            if ("sales".equals(type)) {
                // TODO 1 (YASIERUL): request.setAttribute("dailySummary",
                //                        saleDAO.getDailySummary());
                request.setAttribute("infoMessage",
                    "Sales summary report is not implemented yet (assigned to Yasierul). "
                  + "See ReportServlet TODO 1-2 and SaleDAO TODO 5.");

                request.getRequestDispatcher("/report/sales.jsp").forward(request, response);

            } else {
                // WORKING: low-stock report.
                request.setAttribute("lowStockList", medicineDAO.findLowStock());
                request.getRequestDispatcher("/report/low-stock.jsp").forward(request, response);
            }

        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Could not build report: " + ex.getMessage());
            request.getRequestDispatcher("/report/low-stock.jsp").forward(request, response);
        }
    }
}
