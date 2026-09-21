package my.edu.uptm.pharmatrack.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Model (POJO) representing one line of a receipt — a row of
 * <code>sale_items</code>.
 *
 * <p>MODULE OWNER: <b>RAMZI</b> — Database Design &amp; Data Access Layer.
 * The arithmetic on this class belongs to Yasierul (business logic).</p>
 *
 * <p>{@code unitPrice} is copied from the medicine <b>at the moment of sale</b>
 * and then frozen. If the shop raises the price next month, last month's
 * receipt must still print last month's price.</p>
 *
 * @author Ramzi
 */
public class SaleItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private int        saleItemId;
    private int        saleId;
    private int        medicineId;
    private int        quantity;
    private BigDecimal unitPrice;

    /** Joined in for display on the receipt. */
    private String     medicineName;

    public SaleItem() {
        this.unitPrice = BigDecimal.ZERO;
    }

    public SaleItem(int medicineId, int quantity, BigDecimal unitPrice) {
        this.medicineId = medicineId;
        this.quantity   = quantity;
        this.unitPrice  = unitPrice;
    }

    /**
     * CALCULATION FEATURE (rubric item 6): line subtotal = quantity x unitPrice.
     *
     * <p>The database also stores this as a generated column, so the two must
     * always agree — a handy consistency check to mention in the report.</p>
     *
     * @return quantity multiplied by unit price, rounded to 2 decimal places.
     */
    public BigDecimal getSubtotal() {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity))
                        .setScale(2, RoundingMode.HALF_UP);
    }

    public int getSaleItemId()                      { return saleItemId; }
    public void setSaleItemId(int saleItemId)       { this.saleItemId = saleItemId; }

    public int getSaleId()                          { return saleId; }
    public void setSaleId(int saleId)               { this.saleId = saleId; }

    public int getMedicineId()                      { return medicineId; }
    public void setMedicineId(int medicineId)       { this.medicineId = medicineId; }

    public int getQuantity()                        { return quantity; }
    public void setQuantity(int quantity)           { this.quantity = quantity; }

    public BigDecimal getUnitPrice()                { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice)  { this.unitPrice = unitPrice; }

    public String getMedicineName()                 { return medicineName; }
    public void setMedicineName(String m)           { this.medicineName = m; }

    @Override
    public String toString() {
        return "SaleItem{medicineId=" + medicineId + ", qty=" + quantity
             + ", subtotal=" + getSubtotal() + "}";
    }
}
