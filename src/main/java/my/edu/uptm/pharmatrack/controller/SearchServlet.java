package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.MedicineDAO;
import my.edu.uptm.pharmatrack.dao.SaleDAO;
import my.edu.uptm.pharmatrack.model.Medicine;
import my.edu.uptm.pharmatrack.model.Sale;
import my.edu.uptm.pharmatrack.service.SalesCalculator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controller for the unified search page.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Search &amp; Documentation<br>
 * STATUS: <b>COMPLETE</b><br>
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
 * <p>What the page does:</p>
 * <ol>
 *   <li>Search medicines by name or category ({@code MedicineDAO.search})</li>
 *   <li>A {@code type} parameter switches between medicines and sales</li>
 *   <li>Search sales by date range ({@code SaleDAO.findByDateRange}), with an
 *       optional keyword matching the receipt number or cashier name</li>
 *   <li>"No results found for X" instead of an empty table, a result count,
 *       and the search inputs kept filled in after submitting</li>
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

        String keyword   = trim(request.getParameter("keyword"));
        String type      = "sale".equals(request.getParameter("type")) ? "sale" : "medicine";
        String startDate = trim(request.getParameter("startDate"));
        String endDate   = trim(request.getParameter("endDate"));

        // Echo every input back so the form keeps what the user typed.
        request.setAttribute("keyword",   keyword);
        request.setAttribute("type",      type);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate",   endDate);

        // First visit (no parameters at all): show the empty form, not a
        // table of every record.
        boolean submitted = request.getParameter("type") != null
                         || request.getParameter("keyword") != null;
        request.setAttribute("searched", submitted);

        if (submitted) {
            try {
                if ("sale".equals(type)) {
                    searchSales(request, keyword, startDate, endDate);
                } else {
                    searchMedicines(request, keyword);
                }
            } catch (SQLException ex) {
                request.setAttribute("errorMessage", "Search failed: " + ex.getMessage());
            } catch (RuntimeException ex) {
                request.setAttribute("errorMessage",
                        "Search failed. Please check your input and try again.");
            }
        }

        request.getRequestDispatcher("/search.jsp").forward(request, response);
    }

    /** Medicines whose name or category contains the keyword. */
    private void searchMedicines(HttpServletRequest request, String keyword) throws SQLException {
        List<Medicine> results = medicineDAO.search(keyword);
        request.setAttribute("results",     results);
        request.setAttribute("resultCount", results.size());
    }

    /**
     * Sales within a date range. Both dates optional together (all sales),
     * but if one is given the other is required. The keyword, if present,
     * narrows the list to a receipt number or cashier name.
     */
    private void searchSales(HttpServletRequest request, String keyword,
                             String startDate, String endDate) throws SQLException {

        boolean hasStart = !startDate.isEmpty();
        boolean hasEnd   = !endDate.isEmpty();

        if (hasStart != hasEnd) {
            request.setAttribute("errorMessage",
                    "Please choose both a start date and an end date.");
            return;
        }
        if (hasStart && startDate.compareTo(endDate) > 0) {
            // yyyy-MM-dd sorts correctly as a string.
            request.setAttribute("errorMessage",
                    "The start date must be on or before the end date.");
            return;
        }

        List<Sale> sales = saleDAO.findByDateRange(startDate, endDate);
        List<Sale> results = filterSales(sales, keyword);

        BigDecimal total = BigDecimal.ZERO;
        for (Sale sale : results) {
            if (sale.getTotalAmount() != null) {
                total = total.add(sale.getTotalAmount());
            }
        }

        request.setAttribute("results",      results);
        request.setAttribute("resultCount",  results.size());
        request.setAttribute("salesTotal",   total.setScale(2, RoundingMode.HALF_UP));
        request.setAttribute("salesAverage", SalesCalculator.calculateAverageSale(results));
    }

    /**
     * Keeps only sales whose receipt number equals the keyword (with or
     * without a leading '#') or whose cashier name contains it.
     * Package-private so it can be unit-tested without a database.
     */
    static List<Sale> filterSales(List<Sale> sales, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return sales;
        }
        String needle = keyword.trim().toLowerCase(Locale.ROOT);
        String idText = needle.startsWith("#") ? needle.substring(1) : needle;

        List<Sale> matches = new ArrayList<>();
        for (Sale sale : sales) {
            boolean idMatch = String.valueOf(sale.getSaleId()).equals(idText);
            boolean cashierMatch = sale.getCashierName() != null
                    && sale.getCashierName().toLowerCase(Locale.ROOT).contains(needle);
            if (idMatch || cashierMatch) {
                matches.add(sale);
            }
        }
        return matches;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
