package my.edu.uptm.pharmatrack.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model (POJO) representing one completed transaction — a row of
 * <code>sales</code> plus its {@link SaleItem} lines.
 *
 * <p>MODULE OWNER: <b>RAMZI</b> — Database Design &amp; Data Access Layer.
 * Used heavily by Yasierul's sales / calculation module.</p>
 *
 * @author Ramzi
 */
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    private int            saleId;
    private Timestamp      saleDate;
    private int            userId;
    private BigDecimal     totalAmount;
    private transient List<SaleItem> items;

    /** Joined in for display — which staff member rang up the sale. */
    private String         cashierName;

    public Sale() {
        this.items       = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
    }

    /** Adds a line to this sale's basket. */
    public void addItem(SaleItem item) {
        if (item != null) {
            this.items.add(item);
        }
    }

    /**
     * CALCULATION FEATURE (rubric item 6): recompute the receipt total by
     * summing every line subtotal.
     *
     * <p>This is the authoritative total — {@link #totalAmount} is only the
     * stored copy. Yasierul's {@code SalesCalculator} calls this before the
     * sale is written to the database.</p>
     *
     * @return the sum of all line subtotals, to 2 decimal places.
     */
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /** @return how many individual units were sold across all lines. */
    public int getTotalUnits() {
        int units = 0;
        for (SaleItem item : items) {
            units += item.getQuantity();
        }
        return units;
    }

    public int getSaleId()                             { return saleId; }
    public void setSaleId(int saleId)                  { this.saleId = saleId; }

    public Timestamp getSaleDate()                     { return saleDate; }
    public void setSaleDate(Timestamp saleDate)        { this.saleDate = saleDate; }

    public int getUserId()                             { return userId; }
    public void setUserId(int userId)                  { this.userId = userId; }

    public BigDecimal getTotalAmount()                 { return totalAmount; }
    public void setTotalAmount(BigDecimal total)       { this.totalAmount = total; }

    public List<SaleItem> getItems()                   { return items; }
    public void setItems(List<SaleItem> items)         { this.items = items; }

    public String getCashierName()                     { return cashierName; }
    public void setCashierName(String cashierName)     { this.cashierName = cashierName; }

    @Override
    public String toString() {
        return "Sale{id=" + saleId + ", items=" + items.size()
             + ", total=" + totalAmount + "}";
    }
}
