package my.edu.uptm.pharmatrack.dao;

import my.edu.uptm.pharmatrack.model.Sale;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for the <code>sales</code> and <code>sale_items</code>
 * tables.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Search, Business Logic &amp; Documentation<br>
 * STATUS: <b>STUB — Yasierul to implement</b><br>
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
 * <p>Tick these off — each one is a commit:</p>
 * <ol>
 *   <li>TODO 1 — {@link #findAll()} (easiest, start here)</li>
 *   <li>TODO 2 — {@link #findById(int)} with its line items</li>
 *   <li>TODO 3 — {@link #insertSale(Sale)} — the transaction</li>
 *   <li>TODO 4 — {@link #findByDateRange(String, String)} — search</li>
 *   <li>TODO 5 — {@link #getDailySummary()} — calculation / report</li>
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
      + "WHERE si.sale_id = ?";

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
     * TODO 1 (YASIERUL) — every sale, newest first. Header rows only, no line
     * items (that would be a query per row — slow and unnecessary for a list).
     *
     * @return all sales, never null.
     * @throws SQLException if the query fails.
     */
    public List<Sale> findAll() throws SQLException {
        // TODO 1: implement. Closest pattern: MedicineDAO.findAll()
        throw new UnsupportedOperationException(
            "SaleDAO.findAll() not implemented yet — assigned to Yasierul (TODO 1).");
    }

    /**
     * TODO 2 (YASIERUL) — one sale WITH its line items, for the receipt page.
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
        // TODO 2: implement.
        throw new UnsupportedOperationException(
            "SaleDAO.findById() not implemented yet — assigned to Yasierul (TODO 2).");
    }

    /**
     * TODO 3 (YASIERUL) — record a complete sale inside one transaction.
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
        // TODO 3: implement the transaction.
        throw new UnsupportedOperationException(
            "SaleDAO.insertSale() not implemented yet — assigned to Yasierul (TODO 3).");
    }

    /**
     * TODO 4 (YASIERUL) — SEARCH FEATURE: sales within a date range.
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
        // TODO 4: implement.
        throw new UnsupportedOperationException(
            "SaleDAO.findByDateRange() not implemented yet — assigned to Yasierul (TODO 4).");
    }

    /**
     * TODO 5 (YASIERUL) — CALCULATION FEATURE: per-day totals and averages.
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
        // TODO 5: implement using SQL_DAILY_SUMMARY.
        throw new UnsupportedOperationException(
            "SaleDAO.getDailySummary() not implemented yet — assigned to Yasierul (TODO 5).");
    }
}
