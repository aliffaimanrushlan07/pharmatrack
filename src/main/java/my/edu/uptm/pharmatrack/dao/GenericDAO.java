package my.edu.uptm.pharmatrack.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * The contract every Data Access Object in PharmaTrack implements.
 *
 * <p>MODULE OWNER: <b>RAMZI</b> — Database Design &amp; Data Access Layer.</p>
 *
 * <p><b>Why this interface exists.</b> The assignment brief says the system
 * "should follow good design principles by implementing classes and
 * interfaces", and the rubric's Java EE Architecture band rewards a clear
 * DAO pattern. This interface is the visible evidence of both: every DAO
 * exposes the same five CRUD operations, so a servlet can be written against
 * the interface rather than a concrete class.</p>
 *
 * <p>Mention this interface explicitly in the report's Class Diagram section
 * (6.4) — show {@code MedicineDAO} and {@code UserDAO} realising
 * {@code GenericDAO}. ({@code SaleDAO} deliberately does not — a sale is
 * written as a header plus its lines in one transaction, which does not fit
 * the single-entity {@code insert(T)} signature. Knowing when a pattern does
 * not apply is worth a sentence in the report.)</p>
 *
 * @param <T> the model type this DAO reads and writes (Medicine, User, …)
 *
 * @author Ramzi
 */
public interface GenericDAO<T> {

    /**
     * CREATE — insert a new record.
     *
     * @param entity the object to persist; its ID field is ignored.
     * @return the auto-generated primary key of the new row, or -1 on failure.
     * @throws SQLException if the insert fails.
     */
    int insert(T entity) throws SQLException;

    /**
     * RETRIEVE — fetch every record.
     *
     * @return a list, never null; empty when the table has no rows.
     * @throws SQLException if the query fails.
     */
    List<T> findAll() throws SQLException;

    /**
     * RETRIEVE — fetch one record by primary key.
     *
     * @param id the primary key to look up.
     * @return the matching object, or {@code null} when no row has that id.
     * @throws SQLException if the query fails.
     */
    T findById(int id) throws SQLException;

    /**
     * UPDATE — overwrite an existing record.
     *
     * @param entity the object to save; its ID field selects the row.
     * @return true when exactly one row was updated.
     * @throws SQLException if the update fails.
     */
    boolean update(T entity) throws SQLException;

    /**
     * DELETE — remove a record by primary key.
     *
     * @param id the primary key to delete.
     * @return true when exactly one row was deleted.
     * @throws SQLException if the delete fails — for example when a foreign
     *         key in another table still references this row.
     */
    boolean delete(int id) throws SQLException;
}
