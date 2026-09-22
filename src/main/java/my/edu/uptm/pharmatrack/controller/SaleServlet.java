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
 * Controller for the point-of-sale screen — building a basket and completing
 * a sale.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>AMIR</b> — Sale &amp; Reporting Module<br>
 * STATUS: <b>STUB — Amir to implement</b><br>
 * DEPENDS ON: {@link SaleDAO} and {@code SalesCalculator} (both yours)<br>
 * =====================================================================</p>
 *
 * <p><b>The one design decision to make first: where does the basket live
 * while the cashier is still adding to it?</b></p>
 *
 * <p>Recommended: <b>in the HTTP session</b>, as a {@code Sale} object, until
 * the cashier presses Complete Sale — then it is written to the database in a
 * single transaction. Nothing touches the database until the sale is final, so
 * an abandoned basket leaves no orphan rows and no wrongly-deducted stock.</p>
 *
 * <pre>
 *   ?action=add       &rarr; pull Sale from session (create if absent), add a
 *                       SaleItem, recalculate total, put it back
 *   ?action=remove    &rarr; remove a line, recalculate
 *   ?action=clear     &rarr; drop the basket from the session
 *   ?action=complete  &rarr; saleDAO.insertSale(sale)  [TRANSACTION]
 *                       then clear the session basket and redirect to the
 *                       receipt page
 * </pre>
 *
 * <p><b>Validate stock at add time AND at complete time.</b> Checking only at
 * add time leaves a gap: another cashier could sell the last packet while this
 * basket is still open. The {@code deductStock} query in {@link MedicineDAO}
 * is the final guard — it refuses to take stock below zero.</p>
 *
 * <p>Order of work — each item is a commit:</p>
 * <ol>
 *   <li>TODO 1 — {@code action=add}, basket held in session</li>
 *   <li>TODO 2 — {@code action=remove} and {@code action=clear}</li>
 *   <li>TODO 3 — live total via {@code SalesCalculator}</li>
 *   <li>TODO 4 — {@code action=complete}, calling {@code SaleDAO.insertSale}</li>
 *   <li>TODO 5 — receipt page after a successful sale</li>
 * </ol>
 *
 * @author Amir
 */
@WebServlet(name = "SaleServlet", urlPatterns = {"/sale"})
public class SaleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Session attribute holding the in-progress basket. */
    public static final String SESSION_CART = "currentSale";

    private transient SaleDAO saleDAO;
    private transient MedicineDAO medicineDAO;

    @Override
    public void init() throws ServletException {
        this.saleDAO     = new SaleDAO();
        this.medicineDAO = new MedicineDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // TODO 1-5 (AMIR): route on ?action= as described in the class
        // comment above. Pattern to copy: MedicineServlet.doGet().

        request.setAttribute("errorMessage",
            "Point-of-sale module is not implemented yet (assigned to Amir). "
          + "See SaleServlet TODO 1-5 and SaleDAO TODO 1-5.");

        request.getRequestDispatcher("/sale/pos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        doGet(request, response);
    }
}
