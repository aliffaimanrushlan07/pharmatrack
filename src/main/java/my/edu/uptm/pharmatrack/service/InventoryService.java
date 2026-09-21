package my.edu.uptm.pharmatrack.service;

import my.edu.uptm.pharmatrack.model.Medicine;

import java.util.List;

/**
 * Business rules about stock levels and expiry dates.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Search, Business Logic &amp; Documentation<br>
 * STATUS: <b>STUB — Yasierul to implement</b><br>
 * =====================================================================</p>
 *
 * <p>Rules live here rather than in a JSP or a DAO so that "what counts as low
 * stock" is defined in exactly one place. If the pharmacy later decides low
 * stock means 20% above the reorder level, one method changes and every screen
 * follows.</p>
 *
 * <p>Order of work:</p>
 * <ol>
 *   <li>TODO 1 — {@link #needsReorder(Medicine)}</li>
 *   <li>TODO 2 — {@link #suggestReorderQuantity(Medicine)}</li>
 *   <li>TODO 3 — {@link #isExpiringSoon(Medicine, int)}</li>
 *   <li>TODO 4 — {@link #countLowStock(List)}, for the dashboard badge</li>
 * </ol>
 *
 * @author Yasierul
 */
public final class InventoryService {

    /** A medicine within this many days of expiry is flagged as expiring soon. */
    public static final int EXPIRY_WARNING_DAYS = 90;

    /** Reorder suggestions top stock up to this multiple of the reorder level. */
    public static final int REORDER_MULTIPLIER = 3;

    private InventoryService() {
        throw new AssertionError("InventoryService is a utility class.");
    }

    /**
     * TODO 1 (YASIERUL) — has this medicine hit its reorder threshold?
     *
     * <p>{@code Medicine.isLowStock()} already does the comparison. Wrapping it
     * here gives the rule a home in the service layer, where a future change
     * belongs.</p>
     *
     * @param medicine the medicine to check.
     * @return true when stock is at or below the reorder level.
     */
    public static boolean needsReorder(Medicine medicine) {
        // TODO 1: implement.
        return false;
    }

    /**
     * TODO 2 (YASIERUL) — how many units to order.
     *
     * <p>Suggested rule: bring stock up to {@code reorderLevel *
     * REORDER_MULTIPLIER}. Never return a negative number.</p>
     *
     * @param medicine the medicine to restock.
     * @return units to order, or 0 when no reorder is needed.
     */
    public static int suggestReorderQuantity(Medicine medicine) {
        // TODO 2: implement.
        return 0;
    }

    /**
     * TODO 3 (YASIERUL) — is this medicine close to expiry?
     *
     * <p>Careful with the null check: {@code expiry_date} is nullable in the
     * schema, and a null date must return false, not throw.</p>
     *
     * @param medicine   the medicine to check.
     * @param withinDays the warning window, e.g. {@link #EXPIRY_WARNING_DAYS}.
     * @return true when the medicine expires within the window (or already has).
     */
    public static boolean isExpiringSoon(Medicine medicine, int withinDays) {
        // TODO 3: implement.
        return false;
    }

    /**
     * TODO 4 (YASIERUL) — count of medicines needing reorder.
     *
     * <p>Feeds the red badge on the dashboard.</p>
     *
     * @param medicines the full inventory.
     * @return how many are at or below their reorder level.
     */
    public static int countLowStock(List<Medicine> medicines) {
        // TODO 4: implement.
        return 0;
    }
}
