package my.edu.uptm.pharmatrack.dao;

import my.edu.uptm.pharmatrack.model.Sale;
import my.edu.uptm.pharmatrack.model.SaleItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for the <code>sales</code> and <code>sale_items</code>
 * tables.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Business Logic, Reports &amp; Documentation<br>
 * STATUS: <b>COMPLETE</b><br>
 * =====================================================================</p>
 *
 * <p><b>This is the hardest DAO in the project, and the most valuable.</b>
 * It is the one place where a single user action has to write to three tables
 * at once ({@code sales}, {@code sale_items}, {@code medicines}) and where all
 * three writes must succeed or none of them may. That is a <b>transaction</b>,
 * and getting it right is worth real marks under both "Database Integration"
 * and "Search and Calculation Features".</p>
 *
 * <p><b>The transaction pattern you need in {@link #insertSale(Sale)}:</b></p>
 * <pre>
 * Connection conn = null;
 * try {
 *     conn = DBConnection.getConnection();
 *     conn.setAutoCommit(false);            // &lt;-- start the transaction
 *
 *     // 1. INSERT into sales, get the generated sale_id
 *     // 2. for each SaleItem: INSERT into sale_items
 *     // 3. for each SaleItem: medicineDAO.deductStock(conn, id, qty)
 *     //    -> if deductStock returns false, stock ran out: throw SQLException
 *     // 4. UPDATE sales SET total_amount = sale.calculateTotal()
 *
 *     conn.commit();                        // &lt;-- all four steps succeeded
 *     return saleId;
 *
 * } catch (SQLException ex) {
 *     if (conn != null) conn.rollback();    // &lt;-- undo everything
 *     throw ex;
 * } finally {
 *     if (conn != null) {
 *         conn.setAutoCommit(true);
 *         conn.close();
 *     }
 * }
 * </pre>
 *
 * <p>Note that all four steps share <b>one</b> {@code Connection}. That is why
 * {@link MedicineDAO#deductStock(Connection, int, int)} takes a connection as
 * a parameter instead of opening its own — a second connection would be a
 * second transaction and rollback would not reach it.</p>
 *
 * <p><b>Demo this in the presentation.</b> Try to sell 100 units of something
 * with 5 in stock: the sale is rejected, and afterwards the sales table has no
 * orphan row and the stock is untouched. Examiners like seeing that.</p>
 *
 * <p>Implemented operations:</p>
 * <ol>
 *   <li>{@link #findAll()}</li>
 *   <li>{@link #findById(int)} with its line items</li>
 *   <li>{@link #insertSale(Sale)} — the transaction</li>
 *   <li>{@link #findByDateRange(String, String)} — search</li>
 *   <li>{@link #getDailySummary()} — calculation / report</li>
 * </ol>
 *
 * @author Yasierul
 */
public class SaleDAO {

    // ------------------------------------------------------------------
    //  SQL already tested in Workbench — see database/03_sample_queries.sql
    //  Sections D2 and E.
    // ------------------------------------------------------------------

    /** Receipt headers, newest first, with the cashier's name joined in. */
    protected static final String SQL_FIND_ALL =
        "SELECT sa.sale_id, sa.sale_date, sa.user_id, sa.total_amount, "
      + "       u.full_name AS cashier_name "
      + "FROM sales sa "
      + "JOIN users u ON u.user_id = sa.user_id "
      + "ORDER BY sa.sale_date DESC";

    /** One receipt header. */
    protected static final String SQL_FIND_BY_ID =
        "SELECT sa.sale_id, sa.sale_date, sa.user_id, sa.total_amount, "
      + "       u.full_name AS cashier_name "
      + "FROM sales sa "
      + "JOIN users u ON u.user_id = sa.user_id "
      + "WHERE sa.sale_id = ?";

    /** The lines belonging to one receipt. */
    protected static final String SQL_FIND_ITEMS =
        "SELECT si.sale_item_id, si.sale_id, si.medicine_id, si.quantity, "
      + "       si.unit_price, si.subtotal, m.name AS medicine_name "
      + "FROM sale_items si "
      + "JOIN medicines m ON m.medicine_id = si.medicine_id "
      + "WHERE si.sale_id = ? "
      + "ORDER BY si.sale_item_id";

    protected static final String SQL_INSERT_SALE =
        "INSERT INTO sales (user_id, total_amount) VALUES (?, ?)";

    protected static final String SQL_INSERT_ITEM =
        "INSERT INTO sale_items (sale_id, medicine_id, quantity, unit_price) "
      + "VALUES (?, ?, ?, ?)";

    protected static final String SQL_UPDATE_TOTAL =
        "UPDATE sales SET total_amount = ? WHERE sale_id = ?";

    protected static final String SQL_FIND_BY_DATE_RANGE =
        "SELECT sa.sale_id, sa.sale_date, sa.user_id, sa.total_amount, "
      + "       u.full_name AS cashier_name "
      + "FROM sales sa "
      + "JOIN users u ON u.user_id = sa.user_id "
      + "WHERE sa.sale_date BETWEEN ? AND ? "
      + "ORDER BY sa.sale_date DESC";

    protected static final String SQL_DAILY_SUMMARY =
        "SELECT DATE(sale_date) AS sale_day, "
      + "       COUNT(*) AS transactions, "
      + "       SUM(total_amount) AS revenue, "
      + "       ROUND(AVG(total_amount), 2) AS average_sale "
      + "FROM sales "
      + "GROUP BY DATE(sale_date) "
      + "ORDER BY sale_day DESC";


    /**
     * Every sale, newest first. Header rows only, no line
     * items (that would be a query per row — slow and unnecessary for a list).
     *
     * @return all sales, never null.
     * @throws SQLException if the query fails.
     */
    public List<Sale> findAll() throws SQLException {
        List<Sale> sales = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sales.add(mapSale(rs));
            }
        }
        return sales;
    }

    /**
     * One sale WITH its line items, for the receipt page.
     *
     * <p>Two queries: {@code SQL_FIND_BY_ID} for the header, then
     * {@code SQL_FIND_ITEMS} for the lines, added with
     * {@code sale.addItem(...)}.</p>
     *
     * @param saleId which receipt to load.
     * @return the sale with its items populated, or null if not found.
     * @throws SQLException if a query fails.
     */
    public Sale findById(int saleId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            Sale sale;

            try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
                ps.setInt(1, saleId);
                try (ResultSet rs = ps.executeQuery()) {
                    sale = rs.next() ? mapSale(rs) : null;
                }
            }

            if (sale == null) {
                return null;
            }

            try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ITEMS)) {
                ps.setInt(1, saleId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        sale.addItem(mapSaleItem(rs));
                    }
                }
            }
            return sale;
        }
    }

    /**
     * Records a complete sale inside one transaction.
     *
     * <p>This is the centrepiece of your module. Follow the pattern in the
     * class comment above. Do not skip the rollback — a sale that half-happens
     * leaves the inventory permanently wrong.</p>
     *
     * @param sale a sale with {@code userId} set and at least one item.
     * @return the generated {@code sale_id}.
     * @throws SQLException if anything fails; the transaction is rolled back
     *         first, so the database is left exactly as it was.
     */
    public int insertSale(Sale sale) throws SQLException {
        validateSale(sale);
        MedicineDAO medicineDAO = new MedicineDAO();

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int saleId;

                try (PreparedStatement ps = conn.prepareStatement(
                        SQL_INSERT_SALE, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, sale.getUserId());
                    ps.setBigDecimal(2, java.math.BigDecimal.ZERO);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Could not create the sale header.");
                    }

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("The database did not return a sale id.");
                        }
                        saleId = keys.getInt(1);
                    }
                }

                try (PreparedStatement itemStatement = conn.prepareStatement(SQL_INSERT_ITEM)) {
                    for (SaleItem item : sale.getItems()) {
                        itemStatement.setInt(1, saleId);
                        itemStatement.setInt(2, item.getMedicineId());
                        itemStatement.setInt(3, item.getQuantity());
                        itemStatement.setBigDecimal(4, item.getUnitPrice());

                        if (itemStatement.executeUpdate() != 1) {
                            throw new SQLException("Could not save a sale line.");
                        }

                        if (!medicineDAO.deductStock(
                                conn, item.getMedicineId(), item.getQuantity())) {
                            throw new SQLException(
                                "Insufficient stock for medicine " + item.getMedicineId() + ".");
                        }
                    }
                }

                java.math.BigDecimal total = sale.calculateTotal();
                try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_TOTAL)) {
                    ps.setBigDecimal(1, total);
                    ps.setInt(2, saleId);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Could not update the sale total.");
                    }
                }

                conn.commit();
                sale.setSaleId(saleId);
                sale.setTotalAmount(total);
                return saleId;

            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackFailure) {
                    ex.addSuppressed(rollbackFailure);
                }
                throw ex;
            }
        }
    }

    /**
     * SEARCH FEATURE: sales within a date range.
     *
     * <p>Append {@code " 23:59:59"} to the end date, otherwise MySQL reads it
     * as midnight and silently excludes everything sold on the last day — a
     * classic off-by-one-day bug.</p>
     *
     * @param startDate inclusive, format {@code yyyy-MM-dd}.
     * @param endDate   inclusive, format {@code yyyy-MM-dd}.
     * @return matching sales, never null.
     * @throws SQLException if the query fails.
     */
    public List<Sale> findByDateRange(String startDate, String endDate) throws SQLException {
        if (isBlank(startDate) && isBlank(endDate)) {
            return findAll();
        }
        if (isBlank(startDate) || isBlank(endDate)) {
            throw new SQLException("Both start and end dates are required.");
        }

        Timestamp start;
        Timestamp end;
        try {
            start = Timestamp.valueOf(startDate.trim() + " 00:00:00");
            end = Timestamp.valueOf(endDate.trim() + " 23:59:59");
        } catch (IllegalArgumentException ex) {
            throw new SQLException("Dates must use yyyy-MM-dd format.", ex);
        }

        List<Sale> sales = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_DATE_RANGE)) {
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sales.add(mapSale(rs));
                }
            }
        }
        return sales;
    }

    /**
     * CALCULATION FEATURE: per-day totals and averages.
     *
     * <p>Feeds the sales report page. Each map holds the keys
     * {@code sale_day}, {@code transactions}, {@code revenue} and
     * {@code average_sale} so the JSP can loop over it with
     * {@code <c:forEach>} without needing another model class.</p>
     *
     * @return one map per day with sales, newest day first.
     * @throws SQLException if the query fails.
     */
    public List<Map<String, Object>> getDailySummary() throws SQLException {
        List<Map<String, Object>> summary = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DAILY_SUMMARY);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("sale_day", rs.getDate("sale_day"));
                row.put("transactions", rs.getInt("transactions"));
                row.put("revenue", rs.getBigDecimal("revenue"));
                row.put("average_sale", rs.getBigDecimal("average_sale"));
                summary.add(row);
            }
        }
        return summary;
    }

    private Sale mapSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setSaleId(rs.getInt("sale_id"));
        sale.setSaleDate(rs.getTimestamp("sale_date"));
        sale.setUserId(rs.getInt("user_id"));
        sale.setTotalAmount(rs.getBigDecimal("total_amount"));
        sale.setCashierName(rs.getString("cashier_name"));
        return sale;
    }

    private SaleItem mapSaleItem(ResultSet rs) throws SQLException {
        SaleItem item = new SaleItem();
        item.setSaleItemId(rs.getInt("sale_item_id"));
        item.setSaleId(rs.getInt("sale_id"));
        item.setMedicineId(rs.getInt("medicine_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setMedicineName(rs.getString("medicine_name"));
        return item;
    }

    private void validateSale(Sale sale) throws SQLException {
        if (sale == null) {
            throw new SQLException("Sale is required.");
        }
        if (sale.getUserId() <= 0) {
            throw new SQLException("A logged-in cashier is required.");
        }
        if (sale.getItems() == null || sale.getItems().isEmpty()) {
            throw new SQLException("The basket is empty.");
        }
        for (SaleItem item : sale.getItems()) {
            if (item == null || item.getMedicineId() <= 0
                    || item.getQuantity() <= 0 || item.getUnitPrice() == null) {
                throw new SQLException("The basket contains an invalid sale line.");
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
