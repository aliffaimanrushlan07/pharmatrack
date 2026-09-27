package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.MedicineDAO;
import my.edu.uptm.pharmatrack.dao.SaleDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Loads the dashboard figures before handing rendering to the JSP. */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());

    private transient MedicineDAO medicineDAO;
    private transient SaleDAO saleDAO;

    @Override
    public void init() throws ServletException {
        medicineDAO = new MedicineDAO();
        saleDAO = new SaleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int totalMedicines = 0;
        int salesToday = 0;
        BigDecimal revenueToday = BigDecimal.ZERO.setScale(2);

        try {
            totalMedicines = medicineDAO.findAll().size();
            Date today = Date.valueOf(LocalDate.now());
            List<Map<String, Object>> summary = saleDAO.getDailySummary();

            for (Map<String, Object> row : summary) {
                if (today.equals(row.get("sale_day"))) {
                    salesToday = ((Number) row.get("transactions")).intValue();
                    BigDecimal revenue = (BigDecimal) row.get("revenue");
                    revenueToday = revenue == null ? BigDecimal.ZERO.setScale(2) : revenue;
                    break;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Could not load dashboard figures", ex);
            request.setAttribute("errorMessage",
                    "Could not load all dashboard figures. Please check the database connection.");
        }

        request.setAttribute("totalMedicines", totalMedicines);
        request.setAttribute("salesToday", salesToday);
        request.setAttribute("revenueToday", revenueToday);
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
