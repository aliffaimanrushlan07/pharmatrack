package my.edu.uptm.pharmatrack.dao;

import my.edu.uptm.pharmatrack.model.Medicine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the <code>medicines</code> table.
 *
 * <p>MODULE OWNER: <b>RAMZI</b> — written jointly with <b>AMIR</b>, whose
 * CRUD screens drive it.</p>
 *
 * <p>Note there is no join: suppliers were removed from the system scope, so a
 * medicine row is self-contained.</p>
 *
 * <p><b>&gt;&gt;&gt; THIS FILE IS THE WORKED EXAMPLE FOR THE WHOLE TEAM. &lt;&lt;&lt;</b></p>
 *
 * <p>It is complete and working. Every other DAO in this project
 * ({@code SaleDAO}) is a stub that follows exactly this
 * shape. When you write yours, keep this file open beside it and copy the
 * structure — same try-with-resources, same {@code mapRow} helper, same use
 * of {@code PreparedStatement}.</p>
 *
 * <p><b>Three rules this class demonstrates, all of them marked in the rubric:</b></p>
 * <ol>
 *   <li><b>Always {@code PreparedStatement}, never string concatenation.</b>
 *       {@code "... WHERE name = '" + name + "'"} is an SQL injection hole.
 *       A {@code ?} placeholder is not.</li>
 *   <li><b>Always try-with-resources.</b> The {@code (Connection c = …)} form
 *       closes the connection even when the query throws. Leaked connections
 *       are why an app "works for a while then stops".</li>
 *   <li><b>One {@code mapRow} method.</b> ResultSet-to-object conversion is
 *       written once, not copy-pasted into five methods.</li>
 * </ol>
 *
 * @author Ramzi
 * @author Amir
 */
public class MedicineDAO implements GenericDAO<Medicine> {

    // ------------------------------------------------------------------
    //  SQL statements kept as constants at the top — easy to read, easy to
    //  paste into MySQL Workbench when a query misbehaves.
    // ------------------------------------------------------------------

    private static final String SQL_FIND_ALL =
        "SELECT m.* FROM medicines m ORDER BY m.medicine_id DESC";

    private static final String SQL_FIND_BY_ID =
        "SELECT m.* FROM medicines m WHERE m.medicine_id = ?";

    private static final String SQL_INSERT =
        "INSERT INTO medicines "
      + "(name, category, price, quantity_in_stock, reorder_level, expiry_date) "
      + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
        "UPDATE medicines SET "
      + "name = ?, category = ?, price = ?, quantity_in_stock = ?, "
      + "reorder_level = ?, expiry_date = ? "
      + "WHERE medicine_id = ?";

    private static final String SQL_DELETE =
        "DELETE FROM medicines WHERE medicine_id = ?";

    private static final String SQL_SEARCH =
        "SELECT m.* FROM medicines m "
      + "WHERE m.name LIKE ? OR m.category LIKE ? "
      + "ORDER BY m.name";

    private static final String SQL_DEDUCT_STOCK =
        "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? "
      + "WHERE medicine_id = ? AND quantity_in_stock >= ?";


    // ==================================================================
    //  CREATE
    // ==================================================================

    /**
     * {@inheritDoc}
     *
     * <p>Uses {@code RETURN_GENERATED_KEYS} so the caller gets the new
     * {@code medicine_id} back without a second query.</p>
     */
    @Override
    public int insert(Medicine m) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            bindMedicineFields(ps, m);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                return -1;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        }
    }


    // ==================================================================
    //  RETRIEVE
    // ==================================================================

    /** {@inheritDoc} */
    @Override
    public List<Medicine> findAll() throws SQLException {
        List<Medicine> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public Medicine findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * SEARCH FEATURE (rubric item 4) — partial match on name or category.
     *
     * <p>The {@code %} wildcards are added to the <i>value</i>, not to the SQL
     * string. This is the safe way to build a LIKE query.</p>
     *
     * @param keyword what the user typed; blank returns everything.
     * @return matching medicines, never null.
     * @throws SQLException if the query fails.
     */
    public List<Medicine> search(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        List<Medicine> list = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SEARCH)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }


    // ==================================================================
    //  UPDATE
    // ==================================================================

    /** {@inheritDoc} */
    @Override
    public boolean update(Medicine m) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            bindMedicineFields(ps, m);
            ps.setInt(7, m.getMedicineId());   // the WHERE clause parameter

            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Reduces stock after a sale. Used by Yasierul's sales module.
     *
     * <p>The {@code AND quantity_in_stock >= ?} in the WHERE clause is doing
     * real work: it makes the update fail rather than let stock go negative
     * if two cashiers sell the last packet at the same moment.</p>
     *
     * <p>Takes an existing {@link Connection} rather than opening its own,
     * so the caller can run it inside a transaction alongside the sale insert.</p>
     *
     * @param conn       an open connection, normally with auto-commit off.
     * @param medicineId which medicine to deduct from.
     * @param quantity   how many units to remove.
     * @return true when stock was successfully deducted.
     * @throws SQLException if the update fails.
     */
    public boolean deductStock(Connection conn, int medicineId, int quantity) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DEDUCT_STOCK)) {
            ps.setInt(1, quantity);
            ps.setInt(2, medicineId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() == 1;
        }
    }


    // ==================================================================
    //  DELETE
    // ==================================================================

    /**
     * {@inheritDoc}
     *
     * <p>A medicine that already appears on a receipt cannot be deleted —
     * the {@code fk_sale_items_medicine} foreign key uses {@code ON DELETE
     * RESTRICT}, so MySQL throws. The servlet catches that and shows a
     * friendly message rather than a stack trace.</p>
     */
    @Override
    public boolean delete(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }


    // ==================================================================
    //  PRIVATE HELPERS
    // ==================================================================

    /**
     * Converts the current {@link ResultSet} row into a {@link Medicine}.
     * Written once and reused by every retrieve method above.
     */
    private Medicine mapRow(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setMedicineId(rs.getInt("medicine_id"));
        m.setName(rs.getString("name"));
        m.setCategory(rs.getString("category"));
        m.setPrice(rs.getBigDecimal("price"));
        m.setQuantityInStock(rs.getInt("quantity_in_stock"));
        m.setReorderLevel(rs.getInt("reorder_level"));
        m.setExpiryDate(rs.getDate("expiry_date"));
        return m;
    }

    /**
     * Binds parameters 1–6, which are identical in the INSERT and UPDATE
     * statements. Keeping them together means a new column only has to be
     * added in one place.
     */
    private void bindMedicineFields(PreparedStatement ps, Medicine m) throws SQLException {
        ps.setString(1, m.getName());
        ps.setString(2, m.getCategory());
        ps.setBigDecimal(3, m.getPrice());
        ps.setInt(4, m.getQuantityInStock());
        ps.setInt(5, m.getReorderLevel());

        // setDate(null) is not allowed — a null DATE must be set explicitly.
        if (m.getExpiryDate() != null) {
            ps.setDate(6, m.getExpiryDate());
        } else {
            ps.setNull(6, Types.DATE);
        }

    }
}
