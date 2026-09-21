package my.edu.uptm.pharmatrack.dao;

import my.edu.uptm.pharmatrack.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the <code>suppliers</code> table.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>AMIR</b> — CRUD &amp; Core Application Module<br>
 * STATUS: <b>STUB — Amir to implement</b><br>
 * =====================================================================</p>
 *
 * <p><b>How to do this file.</b> Open {@link MedicineDAO} in a split window.
 * It is the same job, already finished. Your five methods map onto its five
 * methods almost line for line — the only differences are the table name, the
 * column names, and the fact that suppliers has no dates or foreign keys to
 * worry about. Expect this to take under an hour once the pattern clicks.</p>
 *
 * <p><b>Do not change:</b> the class name, the {@code implements GenericDAO}
 * clause, or any method signature. {@code SupplierServlet} and
 * {@code MedicineServlet} both call this class and will stop compiling.</p>
 *
 * <p>Tick these off as you go — each one is a commit:</p>
 * <ol>
 *   <li>TODO 1 — {@link #findAll()}</li>
 *   <li>TODO 2 — {@link #findById(int)}</li>
 *   <li>TODO 3 — {@link #insert(Supplier)}</li>
 *   <li>TODO 4 — {@link #update(Supplier)}</li>
 *   <li>TODO 5 — {@link #delete(int)}</li>
 *   <li>TODO 6 — {@link #search(String)}</li>
 * </ol>
 *
 * @author Amir
 */
public class SupplierDAO implements GenericDAO<Supplier> {

    // ------------------------------------------------------------------
    //  The SQL is already written for you — Ramzi tested every one of these
    //  in MySQL Workbench. See database/03_sample_queries.sql, Section C.
    //  Your job is the Java around them.
    // ------------------------------------------------------------------

    private static final String SQL_FIND_ALL =
        "SELECT s.supplier_id, s.name, s.contact_person, s.phone, s.email, s.address, "
      + "       COUNT(m.medicine_id) AS medicine_count "
      + "FROM suppliers s "
      + "LEFT JOIN medicines m ON m.supplier_id = s.supplier_id "
      + "GROUP BY s.supplier_id, s.name, s.contact_person, s.phone, s.email, s.address "
      + "ORDER BY s.name";

    private static final String SQL_FIND_BY_ID =
        "SELECT supplier_id, name, contact_person, phone, email, address, 0 AS medicine_count "
      + "FROM suppliers WHERE supplier_id = ?";

    private static final String SQL_INSERT =
        "INSERT INTO suppliers (name, contact_person, phone, email, address) "
      + "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
        "UPDATE suppliers SET name = ?, contact_person = ?, phone = ?, email = ?, address = ? "
      + "WHERE supplier_id = ?";

    private static final String SQL_DELETE =
        "DELETE FROM suppliers WHERE supplier_id = ?";

    private static final String SQL_SEARCH =
        "SELECT supplier_id, name, contact_person, phone, email, address, 0 AS medicine_count "
      + "FROM suppliers "
      + "WHERE name LIKE ? OR contact_person LIKE ? "
      + "ORDER BY name";


    /**
     * TODO 3 (AMIR) — CREATE a supplier.
     *
     * <p>Copy {@code MedicineDAO.insert()}. You need
     * {@code Statement.RETURN_GENERATED_KEYS} in {@code prepareStatement} so
     * you can read the new {@code supplier_id} back out.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public int insert(Supplier supplier) throws SQLException {
        // TODO 3: implement. Delete this line and the exception below.
        throw new UnsupportedOperationException(
            "SupplierDAO.insert() not implemented yet — assigned to Amir (TODO 3).");
    }

    /**
     * TODO 1 (AMIR) — RETRIEVE all suppliers.
     *
     * <p>Start here, it is the easiest one. The skeleton below is already
     * correct — you only need to fill in {@link #mapRow(ResultSet)} and remove
     * the exception. Compare with {@code MedicineDAO.findAll()}.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public List<Supplier> findAll() throws SQLException {
        List<Supplier> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));    // <-- TODO 1: finish mapRow() below
            }
        }
        return list;
    }

    /**
     * TODO 2 (AMIR) — RETRIEVE one supplier by id. Used to fill the edit form.
     *
     * {@inheritDoc}
     */
    @Override
    public Supplier findById(int id) throws SQLException {
        // TODO 2: implement. Pattern: MedicineDAO.findById()
        throw new UnsupportedOperationException(
            "SupplierDAO.findById() not implemented yet — assigned to Amir (TODO 2).");
    }

    /**
     * TODO 4 (AMIR) — UPDATE a supplier.
     *
     * <p>Careful with parameter numbering: the five SET values are 1–5 and the
     * WHERE value is 6. Off-by-one here is the single most common bug in this
     * kind of code.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public boolean update(Supplier supplier) throws SQLException {
        // TODO 4: implement. Pattern: MedicineDAO.update()
        throw new UnsupportedOperationException(
            "SupplierDAO.update() not implemented yet — assigned to Amir (TODO 4).");
    }

    /**
     * TODO 5 (AMIR) — DELETE a supplier.
     *
     * <p>Note: {@code medicines.supplier_id} is {@code ON DELETE SET NULL}, so
     * deleting a supplier will <b>not</b> fail — it quietly orphans that
     * supplier's medicines. Worth a warning dialog on the JSP, and worth a
     * sentence in your section of the report.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int id) throws SQLException {
        // TODO 5: implement. Pattern: MedicineDAO.delete()
        throw new UnsupportedOperationException(
            "SupplierDAO.delete() not implemented yet — assigned to Amir (TODO 5).");
    }

    /**
     * TODO 6 (AMIR) — SEARCH suppliers by name or contact person.
     *
     * <p>Remember: wrap the keyword in {@code %} signs as a <i>value</i>
     * ({@code "%" + keyword + "%"}) and bind it with {@code setString}. Never
     * build the {@code %} into the SQL string itself.</p>
     *
     * @param keyword what the user typed; blank should return everything.
     * @return matching suppliers, never null.
     * @throws SQLException if the query fails.
     */
    public List<Supplier> search(String keyword) throws SQLException {
        // TODO 6: implement. Pattern: MedicineDAO.search()
        throw new UnsupportedOperationException(
            "SupplierDAO.search() not implemented yet — assigned to Amir (TODO 6).");
    }

    /**
     * TODO 1 (AMIR) — convert the current ResultSet row into a Supplier.
     *
     * <p>Write this first; everything else depends on it. Six lines:</p>
     * <pre>
     * Supplier s = new Supplier();
     * s.setSupplierId(rs.getInt("supplier_id"));
     * s.setName(rs.getString("name"));
     * ... contact_person, phone, email, address, medicine_count ...
     * return s;
     * </pre>
     */
    private Supplier mapRow(ResultSet rs) throws SQLException {
        // TODO 1: implement, then findAll() above will work.
        throw new UnsupportedOperationException(
            "SupplierDAO.mapRow() not implemented yet — assigned to Amir (TODO 1).");
    }
}
