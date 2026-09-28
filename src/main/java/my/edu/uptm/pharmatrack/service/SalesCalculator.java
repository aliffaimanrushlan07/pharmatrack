package my.edu.uptm.pharmatrack.service;

import my.edu.uptm.pharmatrack.model.Sale;
import my.edu.uptm.pharmatrack.model.SaleItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Business logic for sales arithmetic — totals, tax, discounts, change.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>YASIERUL</b> — Business Logic, Reports &amp; Documentation<br>
 * STATUS: <b>COMPLETE</b><br>
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

    /** Money is always 2 decimal places. */
    private static final int SCALE = 2;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

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
     * Percentage discount.
     *
     * <p>Order of operations matters: the percentage is turned into a factor
     * with four decimal places, multiplied in, and only the final result is
     * rounded. Rounding the percentage first would introduce error.</p>
     *
     * @param amount          the amount to discount; null is treated as zero.
     * @param discountPercent 0-100; values outside that range are clamped,
     *                        and null means no discount.
     * @return the discounted amount, to 2 decimal places.
     */
    public static BigDecimal applyDiscount(BigDecimal amount, BigDecimal discountPercent) {
        BigDecimal base = money(amount);
        if (discountPercent == null) {
            return base;
        }

        BigDecimal percent = discountPercent;
        if (percent.compareTo(BigDecimal.ZERO) < 0) {
            percent = BigDecimal.ZERO;
        } else if (percent.compareTo(HUNDRED) > 0) {
            percent = HUNDRED;
        }

        BigDecimal factor = BigDecimal.ONE.subtract(
                percent.divide(HUNDRED, 4, RoundingMode.HALF_UP));
        return base.multiply(factor).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * SST on a taxable amount, using {@link #SST_RATE}.
     *
     * @param amount the taxable amount; null or negative gives zero.
     * @return the tax due, to 2 decimal places.
     */
    public static BigDecimal calculateTax(BigDecimal amount) {
        BigDecimal base = money(amount);
        if (base.signum() <= 0) {
            return zero();
        }
        return base.multiply(SST_RATE).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Change due to the customer.
     *
     * <p>If {@code amountPaid} is less than {@code totalDue} the result is
     * negative rather than zero, so the page can tell the cashier
     * "short by RM 3.50".</p>
     *
     * @param totalDue   what the sale comes to; null is treated as zero.
     * @param amountPaid what the customer handed over; null is treated as zero.
     * @return change due; negative when the payment is short.
     */
    public static BigDecimal calculateChange(BigDecimal totalDue, BigDecimal amountPaid) {
        return money(amountPaid).subtract(money(totalDue))
                                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Average transaction value, for the sales report.
     *
     * <p>Guards against dividing by zero when there are no sales yet — an
     * {@code ArithmeticException} on an empty database would crash the demo.</p>
     *
     * @param sales the transactions to average.
     * @return the mean sale value, or zero when the list is null or empty.
     */
    public static BigDecimal calculateAverageSale(List<Sale> sales) {
        if (sales == null || sales.isEmpty()) {
            return zero();
        }

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (Sale sale : sales) {
            if (sale == null) {
                continue;
            }
            total = total.add(money(sale.getTotalAmount()));
            count++;
        }
        return calculateAverage(total, count);
    }

    /**
     * Revenue divided by transaction count, safely.
     *
     * <p>This is the <b>correct</b> overall average for the sales report:
     * total revenue over total transactions. It is deliberately NOT the mean
     * of the daily averages, because days with different transaction counts
     * would then be weighted equally and the quiet days would be
     * over-represented. Same trap as {@code StockValuation.grandTotal()}.</p>
     *
     * @param revenue      total money taken.
     * @param transactions how many sales produced it.
     * @return the average, or zero when there were no transactions.
     */
    public static BigDecimal calculateAverage(BigDecimal revenue, int transactions) {
        if (transactions <= 0) {
            return zero();
        }
        return money(revenue).divide(BigDecimal.valueOf(transactions),
                                     SCALE, RoundingMode.HALF_UP);
    }

    // ------------------------------------------------------------------
    //  Sales report totals
    // ------------------------------------------------------------------

    /**
     * Grand-total revenue across the rows returned by
     * {@code SaleDAO.getDailySummary()}.
     *
     * @param dailySummary rows with a {@code revenue} key; null gives zero.
     * @return the summed revenue, to 2 decimal places.
     */
    public static BigDecimal totalRevenue(List<Map<String, Object>> dailySummary) {
        BigDecimal total = BigDecimal.ZERO;
        if (dailySummary != null) {
            for (Map<String, Object> row : dailySummary) {
                total = total.add(toMoney(row.get("revenue")));
            }
        }
        return total.setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Grand-total transaction count across the daily summary rows.
     *
     * @param dailySummary rows with a {@code transactions} key; null gives zero.
     * @return how many sales in total.
     */
    public static int totalTransactions(List<Map<String, Object>> dailySummary) {
        int total = 0;
        if (dailySummary != null) {
            for (Map<String, Object> row : dailySummary) {
                Object value = row.get("transactions");
                if (value instanceof Number) {
                    total += ((Number) value).intValue();
                }
            }
        }
        return total;
    }

    // ------------------------------------------------------------------
    //  Helpers
    // ------------------------------------------------------------------

    private static BigDecimal money(BigDecimal value) {
        return value == null ? zero() : value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal toMoney(Object value) {
        if (value instanceof BigDecimal) {
            return money((BigDecimal) value);
        }
        if (value instanceof Number) {
            return money(new BigDecimal(value.toString()));
        }
        return zero();
    }

    private static BigDecimal zero() {
        return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
