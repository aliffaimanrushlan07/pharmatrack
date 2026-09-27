package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.MedicineDAO;
import my.edu.uptm.pharmatrack.dao.SaleDAO;
import my.edu.uptm.pharmatrack.model.Medicine;
import my.edu.uptm.pharmatrack.model.Sale;
import my.edu.uptm.pharmatrack.model.SaleItem;
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
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the point-of-sale screen — building a basket and completing
 * a sale.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>AMIR</b> — Sale &amp; Reporting Module<br>
 * STATUS: <b>COMPLETE</b><br>
 * DEPENDS ON: {@link SaleDAO} and {@link SalesCalculator}<br>
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
 * <p>Implemented operations:</p>
 * <ol>
 *   <li>{@code action=add}, with the basket held in session</li>
 *   <li>{@code action=remove} and {@code action=clear}</li>
 *   <li>live total via {@code SalesCalculator}</li>
 *   <li>{@code action=complete}, calling {@code SaleDAO.insertSale}</li>
 *   <li>receipt page after a successful sale</li>
 * </ol>
 *
 * @author Amir
 */
@WebServlet(name = "SaleServlet", urlPatterns = {"/sale"})
public class SaleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SaleServlet.class.getName());

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

        String action = request.getParameter("action");

        try {
            if ("receipt".equals(action)) {
                showReceipt(request, response);
            } else {
                showPointOfSale(request, response);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error in SaleServlet", ex);
            request.setAttribute("errorMessage", "Could not load sales data. Please try again.");
            request.getRequestDispatcher("/sale/pos.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "add":
                    addItem(request, response);
                    break;
                case "remove":
                    removeItem(request, response);
                    break;
                case "clear":
                    clearCart(request, response);
                    break;
                case "complete":
                    completeSale(request, response);
                    break;
                default:
                    redirectWithMessage(request, response, "error", "Unknown sale action.");
                    break;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error while processing a sale", ex);
            redirectWithMessage(request, response, "error",
                    "The sale could not be completed: " + ex.getMessage());
        }
    }

    private void showPointOfSale(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String keyword = ValidationUtil.trimToEmpty(request.getParameter("keyword"));
        request.setAttribute("medicines", keyword.isEmpty()
                ? medicineDAO.findAll()
                : medicineDAO.search(keyword));
        request.setAttribute("keyword", keyword);

        Sale cart = getOrCreateCart(request.getSession());
        recalculate(cart);
        request.getRequestDispatcher("/sale/pos.jsp").forward(request, response);
    }

    private void addItem(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int medicineId = ValidationUtil.parseInt(request.getParameter("medicineId"), -1);
        int quantity = ValidationUtil.parseInt(request.getParameter("quantity"), 0);

        if (medicineId <= 0 || quantity <= 0) {
            redirectWithMessage(request, response, "error",
                    "Choose a medicine and enter a quantity greater than zero.");
            return;
        }

        Medicine medicine = medicineDAO.findById(medicineId);
        if (medicine == null) {
            redirectWithMessage(request, response, "error", "Medicine not found.");
            return;
        }

        Sale cart = getOrCreateCart(request.getSession());
        SaleItem existing = findItem(cart, medicineId);
        int quantityAlreadyInCart = existing == null ? 0 : existing.getQuantity();

        if (quantity > medicine.getQuantityInStock() - quantityAlreadyInCart) {
            redirectWithMessage(request, response, "error",
                    "Only " + medicine.getQuantityInStock() + " unit(s) of "
                    + medicine.getName() + " are currently in stock.");
            return;
        }

        if (existing == null) {
            SaleItem item = new SaleItem(medicineId, quantity, medicine.getPrice());
            item.setMedicineName(medicine.getName());
            cart.addItem(item);
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setUnitPrice(medicine.getPrice());
            existing.setMedicineName(medicine.getName());
        }

        recalculate(cart);
        redirectWithMessage(request, response, "success",
                medicine.getName() + " added to the basket.");
    }

    private void removeItem(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int medicineId = ValidationUtil.parseInt(request.getParameter("medicineId"), -1);
        Sale cart = getOrCreateCart(request.getSession());
        boolean removed = false;

        Iterator<SaleItem> iterator = cart.getItems().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getMedicineId() == medicineId) {
                iterator.remove();
                removed = true;
                break;
            }
        }

        recalculate(cart);
        redirectWithMessage(request, response, removed ? "success" : "error",
                removed ? "Item removed from the basket." : "Basket item not found.");
    }

    private void clearCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.getSession().removeAttribute(SESSION_CART);
        redirectWithMessage(request, response, "success", "Basket cleared.");
    }

    private void completeSale(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        HttpSession session = request.getSession(false);
        Sale cart = session == null ? null : (Sale) session.getAttribute(SESSION_CART);
        User user = session == null ? null
                : (User) session.getAttribute(AuthFilter.SESSION_USER);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            redirectWithMessage(request, response, "error", "The basket is empty.");
            return;
        }
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Friendly pre-check. SaleDAO repeats the stock check atomically while
        // deducting it, which closes the race with another cashier.
        for (SaleItem item : cart.getItems()) {
            Medicine current = medicineDAO.findById(item.getMedicineId());
            if (current == null || current.getQuantityInStock() < item.getQuantity()) {
                redirectWithMessage(request, response, "error",
                        "Stock changed while the basket was open. Review the quantities.");
                return;
            }
        }

        cart.setUserId(user.getUserId());
        recalculate(cart);
        int saleId = saleDAO.insertSale(cart);

        session.removeAttribute(SESSION_CART);
        response.sendRedirect(request.getContextPath()
                + "/sale?action=receipt&id=" + saleId);
    }

    private void showReceipt(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int saleId = ValidationUtil.parseInt(request.getParameter("id"), -1);
        Sale sale = saleId > 0 ? saleDAO.findById(saleId) : null;

        if (sale == null) {
            redirectWithMessage(request, response, "error", "Receipt not found.");
            return;
        }

        request.setAttribute("sale", sale);
        request.getRequestDispatcher("/sale/receipt.jsp").forward(request, response);
    }

    private Sale getOrCreateCart(HttpSession session) {
        Sale cart = (Sale) session.getAttribute(SESSION_CART);
        if (cart == null) {
            cart = new Sale();
            session.setAttribute(SESSION_CART, cart);
        }
        return cart;
    }

    private SaleItem findItem(Sale cart, int medicineId) {
        for (SaleItem item : cart.getItems()) {
            if (item.getMedicineId() == medicineId) {
                return item;
            }
        }
        return null;
    }

    private void recalculate(Sale cart) {
        cart.setTotalAmount(SalesCalculator.calculateSubtotal(cart.getItems()));
    }

    private void redirectWithMessage(HttpServletRequest request, HttpServletResponse response,
                                     String type, String message) throws IOException {
        response.sendRedirect(request.getContextPath() + "/sale?" + type + "="
                + URLEncoder.encode(message, "UTF-8"));
    }
}
