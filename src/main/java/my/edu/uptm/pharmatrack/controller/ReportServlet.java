package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.SaleDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller for the sales report page.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Business Logic, Reports &amp; Documentation<br>
 * STATUS: <b>STUB — Yasierul to implement</b><br>
 * =====================================================================</p>
 *
 * <p>This is the visible face of <b>rubric item 6, the calculation feature</b>.
 * A report page that shows real aggregated numbers is the clearest possible
 * evidence of business logic, so it is worth polishing.</p>
 *
 * <p>Order of work:</p>
 * <ol>
 *   <li>TODO 1 — {@code getDailySummary()} from SaleDAO, rendered in sales.jsp</li>
 *   <li>TODO 2 — grand total revenue and overall average sale</li>
 *   <li>TODO 3 — expiring-soon report (SQL ready in 03_sample_queries.sql, E2)</li>
 * </ol>
 *
 * @author Yasierul
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/report"})
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient SaleDAO saleDAO;

    @Override
    public void init() throws ServletException {
        this.saleDAO     = new SaleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // TODO 1 (YASIERUL): request.setAttribute("dailySummary",
            //                    saleDAO.getDailySummary());
            request.setAttribute("infoMessage",
                "Sales summary report is not implemented yet (assigned to Yasierul). "
              + "See ReportServlet TODO 1-2 and SaleDAO TODO 5.");

            request.getRequestDispatcher("/report/sales.jsp").forward(request, response);

        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Could not build report: " + ex.getMessage());
            request.getRequestDispatcher("/report/sales.jsp").forward(request, response);
        }
    }
}
