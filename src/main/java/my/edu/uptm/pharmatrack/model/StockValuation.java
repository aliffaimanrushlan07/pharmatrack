package my.edu.uptm.pharmatrack.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * One row of the <b>inventory valuation report</b>: the money currently tied
 * up in stock, grouped by medicine category.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>RAMZI</b> — Database Design &amp; Data Access Layer<br>
 * STATUS: <b>Done — worked example of a report DTO</b><br>
 * =====================================================================</p>
 *
 * <p>This class is not a table. Nothing in the database looks like this — it
 * is the shape of one row of a {@code GROUP BY} result, produced by
 * {@link my.edu.uptm.pharmatrack.dao.MedicineDAO#getStockValuation()}. A class
 * that exists to carry query results to a JSP is called a <b>DTO</b> (Data
 * Transfer Object), and using one instead of passing raw {@code Map}s around
 * is worth saying out loud in the report:</p>
 *
 * <ul>
 *   <li>The compiler checks {@code v.getStockValue()}. It cannot check
 *       {@code map.get("stock_valu")} — that typo returns {@code null} and the
 *       page silently renders a blank cell.</li>
 *   <li>The money fields are {@link BigDecimal}, so they cannot accidentally
 *       be added up as {@code double}s.</li>
 *   <li>{@link #grandTotal(List)} gives the calculation a home in Java rather
 *       than scattering arithmetic through the JSP.</li>
 * </ul>
 *
 * @author Ramzi
 */
public class StockValuation implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Money is always rounded to 2 decimal places, half-up, as in Sale. */
    private static final int SCALE = 2;

    private String     category;
    private int        itemCount;      // how many distinct medicines
    private int        totalUnits;     // how many physical packets/boxes
    private BigDecimal stockValue;     // SUM(price * quantity_in_stock)
    private BigDecimal averagePrice;   // AVG(price)
    private BigDecimal shareOfTotal;   // this category's % of the whole

    public StockValuation() {
        this.stockValue   = BigDecimal.ZERO;
        this.averagePrice = BigDecimal.ZERO;
        this.shareOfTotal = BigDecimal.ZERO;
    }

    // ------------------------------------------------------------------
    //  The calculation
    // ------------------------------------------------------------------

    /**
     * Builds the grand-total row shown at the bottom of the report.
     *
     * <p><b>This method is the calculation feature</b> for the inventory
     * report, so it is worth being able to explain it line by line.</p>
     *
     * <p>The counts and the value simply add up. The average price does
     * <em>not</em>: averaging a list of averages is only correct when every
     * group is the same size, and these are not — "Analgesic" holds two
     * medicines while "Hygiene" holds two others. Taking the plain mean of the
     * category averages would quietly over-weight the small categories.</p>
     *
     * <p>So each category average is <b>weighted by its item count</b> before
     * adding, which recovers the true overall mean:</p>
     *
     * <pre>
     *     &Sigma;(avg&#8342; &times; n&#8342;)     &Sigma;(all prices)
     *     ------------  =  ---------------  =  AVG(price) over every medicine
     *        &Sigma;n&#8342;          count(medicines)
     * </pre>
     *
     * @param rows the per-category rows, never null
     * @return a row whose category reads "ALL CATEGORIES"
     */
    public static StockValuation grandTotal(List<StockValuation> rows) {
        StockValuation total = new StockValuation();
        total.setCategory("ALL CATEGORIES");

        BigDecimal weightedPriceSum = BigDecimal.ZERO;

        for (StockValuation row : rows) {
            total.itemCount  += row.getItemCount();
            total.totalUnits += row.getTotalUnits();

            // BigDecimal is immutable: total = total.add(x), never total.add(x).
            total.stockValue = total.stockValue.add(row.getStockValue());

            weightedPriceSum = weightedPriceSum.add(
                    row.getAveragePrice().multiply(BigDecimal.valueOf(row.getItemCount())));
        }

        // Guard against divide-by-zero on an empty medicines table. Without
        // this the report crashes on a freshly created, unseeded database.
        if (total.itemCount > 0) {
            total.averagePrice = weightedPriceSum.divide(
                    BigDecimal.valueOf(total.itemCount), SCALE, RoundingMode.HALF_UP);
        }

        return total;
    }

    /**
     * Fills in each row's {@code shareOfTotal} — what percentage of the
     * pharmacy's money is sitting in that category.
     *
     * <p>Two things here are deliberate. The multiplication by 100 happens
     * <b>before</b> the division, because dividing first and then scaling
     * throws away the digits you were about to need. And the divide call
     * names a scale and a rounding mode: {@code BigDecimal.divide} with
     * neither argument throws {@code ArithmeticException} the moment a
     * division does not terminate — 1/3, for instance — which is an easy way
     * to crash a report on real data.</p>
     *
     * @param rows       the per-category rows, modified in place
     * @param grandTotal the row returned by {@link #grandTotal(List)}
     */
    public static void applyShares(List<StockValuation> rows, StockValuation grandTotal) {
        BigDecimal whole = grandTotal.getStockValue();

        // An empty or worthless inventory has no meaningful percentages.
        if (whole.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        for (StockValuation row : rows) {
            row.setShareOfTotal(row.getStockValue()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(whole, 1, RoundingMode.HALF_UP));
        }
    }

    // ------------------------------------------------------------------
    //  Getters and setters
    // ------------------------------------------------------------------

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }

    public BigDecimal getStockValue() {
        return stockValue;
    }

    public void setStockValue(BigDecimal stockValue) {
        this.stockValue = (stockValue == null)
                ? BigDecimal.ZERO
                : stockValue.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = (averagePrice == null)
                ? BigDecimal.ZERO
                : averagePrice.setScale(SCALE, RoundingMode.HALF_UP);
    }

    /** @return this category's percentage share, set by {@link #applyShares}. */
    public BigDecimal getShareOfTotal() {
        return shareOfTotal;
    }

    public void setShareOfTotal(BigDecimal shareOfTotal) {
        this.shareOfTotal = (shareOfTotal == null) ? BigDecimal.ZERO : shareOfTotal;
    }

    @Override
    public String toString() {
        return "StockValuation{" + category + ", items=" + itemCount
             + ", units=" + totalUnits + ", value=" + stockValue + '}';
    }
}
