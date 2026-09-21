package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.SupplierDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller for supplier CRUD operations.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>AMIR</b> — CRUD &amp; Core Application Module<br>
 * STATUS: <b>STUB — Amir to implement</b><br>
 * DEPENDS ON: {@link SupplierDAO} (also yours — do that first)<br>
 * =====================================================================</p>
 *
 * <p><b>This is a direct copy of {@link MedicineServlet} with the entity
 * swapped.</b> Open that file side by side. Same action routing, same
 * POST-redirect-GET, same validation-then-re-show-form flow. Suppliers are
 * simpler than medicines — no dates, no prices, no foreign key to another
 * table — so if you can follow MedicineServlet you can write this.</p>
 *
 * <p>Order of work — each item is a commit:</p>
 * <ol>
 *   <li>TODO 1 — {@code doGet} action routing (list / new / edit / delete)</li>
 *   <li>TODO 2 — {@code listSuppliers()}, forwarding to supplier/list.jsp</li>
 *   <li>TODO 3 — {@code showForm()}, forwarding to supplier/form.jsp</li>
 *   <li>TODO 4 — {@code doPost} → {@code saveSupplier()} (insert vs update)</li>
 *   <li>TODO 5 — {@code deleteSupplier()}</li>
 *   <li>TODO 6 — server-side validation using {@code ValidationUtil}</li>
 * </ol>
 *
 * @author Amir
 */
@WebServlet(name = "SupplierServlet", urlPatterns = {"/supplier"})
public class SupplierServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient SupplierDAO supplierDAO;

    @Override
    public void init() throws ServletException {
        this.supplierDAO = new SupplierDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // TODO 1 (AMIR): route on request.getParameter("action") exactly the
        // way MedicineServlet.doGet() does — switch over
        // "new" / "edit" / "delete" / default "list".
        //
        // For now everything falls through to the list page so the link in the
        // navigation bar does not 404 while you work.

        request.setAttribute("errorMessage",
            "Supplier module is not implemented yet (assigned to Amir). "
          + "See SupplierServlet TODO 1-6 and SupplierDAO TODO 1-6.");

        request.getRequestDispatcher("/supplier/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // TODO 4 (AMIR): build a Supplier from the request parameters,
        // validate it, then insert or update depending on whether
        // supplierId > 0. Redirect afterwards, never forward — see the
        // POST-redirect-GET note in MedicineServlet.

        doGet(request, response);
    }
}
