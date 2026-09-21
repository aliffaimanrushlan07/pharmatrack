package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.MedicineDAO;
import my.edu.uptm.pharmatrack.dao.SupplierDAO;
import my.edu.uptm.pharmatrack.model.Medicine;
import my.edu.uptm.pharmatrack.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for all medicine CRUD operations.
 *
 * <p>MODULE OWNER: <b>AMIR</b> — CRUD &amp; Core Application Module.
 * STATUS: <b>COMPLETE</b>.</p>
 *
 * <p><b>&gt;&gt;&gt; THIS FILE IS THE WORKED EXAMPLE FOR EVERY OTHER SERVLET. &lt;&lt;&lt;</b></p>
 *
 * <p>{@code SupplierServlet}, {@code SaleServlet}, {@code SearchServlet} and
 * {@code ReportServlet} are all stubs that follow this exact shape. Keep this
 * open beside yours.</p>
 *
 * <p><b>The "action" routing pattern.</b> One servlet handles a whole entity by
 * switching on an {@code ?action=} parameter, instead of five separate
 * servlets. Fewer files, and all the medicine logic in one readable place:</p>
 *
 * <table border="1">
 *   <caption>URL routing</caption>
 *   <tr><th>URL</th><th>Does</th></tr>
 *   <tr><td>{@code GET  /medicine}</td>              <td>list all</td></tr>
 *   <tr><td>{@code GET  /medicine?action=new}</td>   <td>show blank form</td></tr>
 *   <tr><td>{@code GET  /medicine?action=edit&id=3}</td><td>show filled form</td></tr>
 *   <tr><td>{@code GET  /medicine?action=delete&id=3}</td><td>delete, then list</td></tr>
 *   <tr><td>{@code POST /medicine?action=save}</td>  <td>insert or update</td></tr>
 * </table>
 *
 * <p><b>The POST-redirect-GET rule.</b> After a successful save this servlet
 * <i>redirects</i> rather than forwards. Without that, pressing F5 on the
 * result page resubmits the form and creates a duplicate record — a bug an
 * examiner will find in about four seconds.</p>
 *
 * @author Amir
 */
@WebServlet(name = "MedicineServlet", urlPatterns = {"/medicine"})
public class MedicineServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(MedicineServlet.class.getName());

    private transient MedicineDAO medicineDAO;
    private transient SupplierDAO supplierDAO;

    @Override
    public void init() throws ServletException {
        this.medicineDAO = new MedicineDAO();
        this.supplierDAO = new SupplierDAO();
    }

    // ==================================================================
    //  GET — list, new form, edit form, delete
    // ==================================================================

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    showForm(request, response, null);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteMedicine(request, response);
                    break;
                case "list":
                default:
                    listMedicines(request, response);
                    break;
            }
        } catch (SQLException ex) {
            handleDatabaseError(request, response, ex);
        }
    }

    // ==================================================================
    //  POST — save (insert or update)
    // ==================================================================

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            saveMedicine(request, response);
        } catch (SQLException ex) {
            handleDatabaseError(request, response, ex);
        }
    }

    // ==================================================================
    //  ACTIONS
    // ==================================================================

    /** RETRIEVE — show the full list, optionally filtered by a search keyword. */
    private void listMedicines(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String keyword = request.getParameter("keyword");

        // One list variable, two possible sources — the JSP does not care which.
        request.setAttribute("medicines",
            (keyword != null && !keyword.trim().isEmpty())
                ? medicineDAO.search(keyword)
                : medicineDAO.findAll());

        request.setAttribute("keyword", keyword);
        forward(request, response, "/medicine/list.jsp");
    }

    /** Shows the add form (medicine == null) or the edit form. */
    private void showForm(HttpServletRequest request, HttpServletResponse response,
                          Medicine medicine) throws SQLException, ServletException, IOException {

        request.setAttribute("medicine", medicine);

        // The form has a supplier dropdown, so the view needs the supplier list.
        // SupplierDAO is still a stub (Amir, TODO 1) — until it is written this
        // throws UnsupportedOperationException, so we degrade gracefully rather
        // than showing a 500 page to the rest of the team.
        try {
            request.setAttribute("suppliers", supplierDAO.findAll());
        } catch (UnsupportedOperationException ex) {
            LOGGER.warning("SupplierDAO.findAll() not implemented yet — "
                         + "supplier dropdown will be empty. See SupplierDAO TODO 1.");
            request.setAttribute("suppliers", java.util.Collections.emptyList());
        }

        forward(request, response, "/medicine/form.jsp");
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int id = ValidationUtil.parseInt(request.getParameter("id"), -1);
        Medicine medicine = (id > 0) ? medicineDAO.findById(id) : null;

        if (medicine == null) {
            redirectWithMessage(request, response, "error", "Medicine not found.");
            return;
        }
        showForm(request, response, medicine);
    }

    /** CREATE or UPDATE, depending on whether an id came through with the form. */
    private void saveMedicine(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int id = ValidationUtil.parseInt(request.getParameter("medicineId"), 0);

        Medicine medicine = new Medicine();
        medicine.setMedicineId(id);
        medicine.setName(ValidationUtil.trimToEmpty(request.getParameter("name")));
        medicine.setCategory(ValidationUtil.trimToEmpty(request.getParameter("category")));
        medicine.setPrice(parsePrice(request.getParameter("price")));
        medicine.setQuantityInStock(ValidationUtil.parseInt(request.getParameter("quantityInStock"), 0));
        medicine.setReorderLevel(ValidationUtil.parseInt(request.getParameter("reorderLevel"), 10));
        medicine.setExpiryDate(parseDate(request.getParameter("expiryDate")));
        medicine.setSupplierId(ValidationUtil.parseInt(request.getParameter("supplierId"), 0));

        // --- Server-side validation ------------------------------------------
        // The JSP also validates with HTML5 'required' attributes, but that is
        // convenience only — anyone can bypass it with a crafted POST. Never
        // trust the client.
        String validationError = validate(medicine);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            showForm(request, response, medicine);   // re-show with their input kept
            return;
        }

        if (id > 0) {
            medicineDAO.update(medicine);
            redirectWithMessage(request, response, "success",
                "Medicine \"" + medicine.getName() + "\" updated.");
        } else {
            medicineDAO.insert(medicine);
            redirectWithMessage(request, response, "success",
                "Medicine \"" + medicine.getName() + "\" added.");
        }
    }

    /** DELETE, with a friendly message when a foreign key blocks it. */
    private void deleteMedicine(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int id = ValidationUtil.parseInt(request.getParameter("id"), -1);

        if (id <= 0) {
            redirectWithMessage(request, response, "error", "Invalid medicine id.");
            return;
        }

        try {
            boolean deleted = medicineDAO.delete(id);
            redirectWithMessage(request, response,
                deleted ? "success" : "error",
                deleted ? "Medicine deleted." : "Medicine not found.");

        } catch (SQLException ex) {
            // SQLState 23000 = integrity constraint violation. Here it means the
            // medicine appears on an existing receipt and cannot be removed.
            if ("23000".equals(ex.getSQLState())) {
                redirectWithMessage(request, response, "error",
                    "This medicine appears in past sales and cannot be deleted. "
                  + "Set its stock to 0 instead.");
            } else {
                throw ex;
            }
        }
    }

    // ==================================================================
    //  HELPERS
    // ==================================================================

    /** @return an error message, or null when the medicine is valid. */
    private String validate(Medicine m) {
        if (ValidationUtil.isBlank(m.getName())) {
            return "Medicine name is required.";
        }
        if (m.getName().length() > 100) {
            return "Medicine name must be 100 characters or fewer.";
        }
        if (m.getPrice() == null || m.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return "Price must be zero or more.";
        }
        if (m.getQuantityInStock() < 0) {
            return "Quantity in stock cannot be negative.";
        }
        if (m.getReorderLevel() < 0) {
            return "Reorder level cannot be negative.";
        }
        return null;
    }

    private BigDecimal parsePrice(String raw) {
        try {
            return (raw == null || raw.trim().isEmpty())
                ? BigDecimal.ZERO
                : new BigDecimal(raw.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.valueOf(-1);   // fails validation above
        }
    }

    private Date parseDate(String raw) {
        try {
            return (raw == null || raw.trim().isEmpty()) ? null : Date.valueOf(raw.trim());
        } catch (IllegalArgumentException ex) {
            return null;   // HTML date input always sends yyyy-MM-dd, so this is rare
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String jsp)
            throws ServletException, IOException {

        request.getRequestDispatcher(jsp).forward(request, response);
    }

    /**
     * POST-redirect-GET. The message rides along in the query string so it
     * survives the redirect without needing a session attribute.
     */
    private void redirectWithMessage(HttpServletRequest request, HttpServletResponse response,
                                     String type, String message) throws IOException {

        String encoded = java.net.URLEncoder.encode(message, "UTF-8");
        response.sendRedirect(request.getContextPath() + "/medicine?" + type + "=" + encoded);
    }

    private void handleDatabaseError(HttpServletRequest request, HttpServletResponse response,
                                     SQLException ex) throws ServletException, IOException {

        LOGGER.log(Level.SEVERE, "Database error in MedicineServlet", ex);
        request.setAttribute("errorMessage",
            "Database error: " + ex.getMessage()
          + " — check that MySQL is running and db.properties is correct.");
        forward(request, response, "/medicine/list.jsp");
    }
}
