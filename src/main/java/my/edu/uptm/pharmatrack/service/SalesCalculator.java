package my.edu.uptm.pharmatrack.service;

import my.edu.uptm.pharmatrack.model.Sale;
import my.edu.uptm.pharmatrack.model.SaleItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Business logic for sales arithmetic — totals, tax, discounts, change.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Search, Business Logic &amp; Documentation<br>
 * STATUS: <b>PARTIAL</b> — the core total is done as a worked example; the
 * rest are TODOs.<br>
 * =====================================================================</p>
 *
 * <p><b>Why this class exists at all.</b> The arithmetic could live inside
 * {@code SaleServlet}, but then it would be tangled up with HTTP handling and
 * impossible to unit test. A separate service class is the "separation of
 * presentation, business logic and data access layer" the rubric asks for —
 * this is the business logic layer, with nothing else in it. Point at this
 * class in the report when you discuss architecture.</p>
 *
 * <p><b>Money rule:</b> {@link BigDecimal} everywhere, {@code RoundingMode.HALF_UP},
 * scale 2. Never {@code double}. A till that is one sen out per transaction
 * is a bug, and it is the kind of bug an examiner specifically looks for.</p>
 *
 * @author Yasierul
 */
public final class SalesCalculator {

    /** Malaysian SST rate on applicable goods. Most medicines are exempt. */
    public static final BigDecimal SST_RATE = new BigDecimal("0.06");

    private SalesCalculator() {
        throw new AssertionError("SalesCalculator is a utility class.");
    }

    /**
     * CALCULATION FEATURE (rubric item 6) — sum of every line subtotal.
     *
     * <p><b>WORKED EXAMPLE — already done.</b> Every other method in this class
     * follows the same shape: start from {@code BigDecimal.ZERO}, accumulate
     * with {@code .add()}, finish with {@code .setScale(2, HALF_UP)}.</p>
     *
     * <p>Note {@code total = total.add(...)}. BigDecimal is immutable, so
     * {@code total.add(x)} on its own does nothing — a classic silent bug.</p>
     *
     * @param items the basket lines; null or empty gives zero.
     * @return the subtotal before any tax or discount, to 2 decimal places.
     */
    public static BigDecimal calculateSubtotal(List<SaleItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * TODO 1 (YASIERUL) — percentage discount.
     *
     * <p>Watch the order of operations: multiply first, round last. Rounding
     * the percentage before applying it introduces error.</p>
     *
     * @param amount          the amount to discount.
     * @param discountPercent 0-100; values outside that range should be
     *                        clamped, not trusted.
     * @return the discounted amount, to 2 decimal places.
     */
    public static BigDecimal applyDiscount(BigDecimal amount, BigDecimal discountPercent) {
        // TODO 1: implement.
        //   BigDecimal factor = BigDecimal.ONE.subtract(
        //       discountPercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        //   return amount.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        return amount;
    }

    /**
     * TODO 2 (YASIERUL) — SST on a taxable amount.
     *
     * @param amount the taxable amount.
     * @return the tax due, to 2 decimal places.
     */
    public static BigDecimal calculateTax(BigDecimal amount) {
        // TODO 2: implement using SST_RATE.
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * TODO 3 (YASIERUL) — change due to the customer.
     *
     * <p>If {@code amountPaid} is less than {@code totalDue}, return a negative
     * number rather than zero — the JSP needs to be able to tell the cashier
     * "short by RM 3.50".</p>
     *
     * @param totalDue   what the sale comes to.
     * @param amountPaid what the customer handed over.
     * @return change due; negative when the payment is short.
     */
    public static BigDecimal calculateChange(BigDecimal totalDue, BigDecimal amountPaid) {
        // TODO 3: implement.
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * TODO 4 (YASIERUL) — average transaction value, for the sales report.
     *
     * <p>Guard against dividing by zero when there are no sales yet — that
     * ArithmeticException on an empty database is an easy way to crash the
     * demo on the examiner's machine.</p>
     *
     * @param sales the transactions to average.
     * @return the mean sale value, or zero when the list is empty.
     */
    public static BigDecimal calculateAverageSale(List<Sale> sales) {
        // TODO 4: implement.
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
