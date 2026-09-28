package my.edu.uptm.pharmatrack.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only queries that exist only to feed the Reports page.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Business Logic, Reports &amp; Documentation<br>
 * STATUS: <b>COMPLETE</b><br>
 * =====================================================================</p>
 *
 * <p>Kept separate from {@link MedicineDAO} on purpose: {@code MedicineDAO}
 * is the CRUD DAO owned by Ramzi and Amir, while report queries change for
 * different reasons (what the pharmacist wants to see) and belong to the
 * reports module. Same try-with-resources and {@code PreparedStatement}
 * pattern as every other DAO in the project.</p>
 *
 * @author Yasierul
 */
public class ReportDAO {

    /** Default look-ahead window for the expiring-soon report. */
    public static final int DEFAULT_EXPIRY_DAYS = 90;

    /** Largest window the report accepts; stops silly values like 99999. */
    public static final int MAX_EXPIRY_DAYS = 730;

    /**
     * Medicines expiring within the next N days — including ones that have
     * already expired and are still on the shelf (negative days remaining).
     * SQL tested in {@code database/03_sample_queries.sql}, section E1.
     */
    private static final String SQL_EXPIRING_SOON =
        "SELECT medicine_id, name, category, quantity_in_stock, price, expiry_date, "
      + "       DATEDIFF(expiry_date, CURDATE()) AS days_remaining "
      + "FROM   medicines "
      + "WHERE  expiry_date IS NOT NULL "
      + "  AND  expiry_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY) "
      + "ORDER BY expiry_date ASC, name ASC";

    /**
     * EXPIRY REPORT: medicines that expire within {@code days} days of today.
     *
     * <p>Each map holds {@code medicine_id}, {@code name}, {@code category},
     * {@code quantity_in_stock}, {@code price}, {@code expiry_date},
     * {@code days_remaining}, {@code stock_value} and {@code status}
     * ({@code EXPIRED}, {@code CRITICAL} for 30 days or less, otherwise
     * {@code WARNING}). The status is decided here, in Java, so the JSP only
     * has to pick a badge colour.</p>
     *
     * @param days look-ahead window; clamped to 1..{@value #MAX_EXPIRY_DAYS}.
     * @return matching medicines, soonest expiry first; never null.
     * @throws SQLException if the query fails.
     */
    public List<Map<String, Object>> findExpiringSoon(int days) throws SQLException {
        int window = Math.max(1, Math.min(days, MAX_EXPIRY_DAYS));
        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXPIRING_SOON)) {
            ps.setInt(1, window);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int daysRemaining = rs.getInt("days_remaining");
                    int quantity = rs.getInt("quantity_in_stock");
                    BigDecimal price = rs.getBigDecimal("price");
                    if (price == null) {
                        price = BigDecimal.ZERO;
                    }

                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("medicine_id", rs.getInt("medicine_id"));
                    row.put("name", rs.getString("name"));
                    row.put("category", rs.getString("category"));
                    row.put("quantity_in_stock", quantity);
                    row.put("price", price);
                    row.put("expiry_date", rs.getDate("expiry_date"));
                    row.put("days_remaining", daysRemaining);
                    row.put("stock_value", price.multiply(BigDecimal.valueOf(quantity))
                            .setScale(2, RoundingMode.HALF_UP));
                    row.put("status", expiryStatus(daysRemaining));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    /**
     * Business rule for the badge on the expiry report.
     *
     * @param daysRemaining days until expiry; negative means already expired.
     * @return {@code EXPIRED}, {@code CRITICAL} (0-30 days) or {@code WARNING}.
     */
    public static String expiryStatus(int daysRemaining) {
        if (daysRemaining < 0) {
            return "EXPIRED";
        }
        if (daysRemaining <= 30) {
            return "CRITICAL";
        }
        return "WARNING";
    }
}
