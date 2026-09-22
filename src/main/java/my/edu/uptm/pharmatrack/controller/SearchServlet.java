package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.MedicineDAO;
import my.edu.uptm.pharmatrack.dao.SaleDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller for the unified search page.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Search &amp; Documentation<br>
 * STATUS: <b>STUB — Yasierul to implement</b><br>
 * =====================================================================</p>
 *
 * <p>This is <b>rubric item 4, "Search Functionality"</b>, worth 10 marks in
 * the development rubric — a whole criterion for one page. It is also the
 * easiest of your three files, so do it first to build momentum.</p>
 *
 * <p>Good news: {@link MedicineDAO#search(String)} is already written and
 * tested. Your first version is roughly fifteen lines — read {@code keyword},
 * call the DAO, forward to the JSP.</p>
 *
 * <p>Order of work — each item is a commit:</p>
 * <ol>
 *   <li>TODO 1 — search medicines by name or category (use the existing DAO method)</li>
 *   <li>TODO 2 — a {@code type} parameter to switch between medicines and sales</li>
 *   <li>TODO 3 — search sales by date range via {@code SaleDAO.findByDateRange}</li>
 *   <li>TODO 4 — show "No results found for X" rather than an empty table</li>
 * </ol>
 *
 * <p><b>Two details that lift this from Satisfactory to Excellent:</b> keep the
 * keyword in the search box after submitting (nothing feels more broken than a
 * box that clears itself), and show a result count. Both are two lines each.</p>
 *
 * @author Yasierul
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {

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

        String keyword = request.getParameter("keyword");
        String type    = request.getParameter("type");   // "medicine" or "sale"

        try {
            // TODO 1 (YASIERUL): when keyword is present, call
            //     medicineDAO.search(keyword)
            // and put the result in request attribute "results".
            // Also set "keyword" and "resultCount" so the JSP can echo them.
            //
            // TODO 2-3: branch on 'type' to search sales by date range instead.

            request.setAttribute("keyword", keyword);
            request.setAttribute("type", type);
            request.setAttribute("infoMessage",
                "Search module is not implemented yet (assigned to Yasierul). "
              + "See SearchServlet TODO 1-4.");

        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Search failed: " + ex.getMessage());
        }

        request.getRequestDispatcher("/search.jsp").forward(request, response);
    }
}
