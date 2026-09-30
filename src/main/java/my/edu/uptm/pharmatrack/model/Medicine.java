package my.edu.uptm.pharmatrack.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Model (POJO) representing one row of the <code>medicines</code> table.
 *
 * <p>MODULE OWNER: <b>RAMZI</b> — Database Design &amp; Data Access Layer.</p>
 *
 * <p>Every medicine uses the same stock-status ranges: zero is out of stock,
 * 1-20 is low, 21-80 is optimal, and 81 or more is excess stock.</p>
 *
 * <p>{@code price} is a {@link BigDecimal}, not a {@code double}. Money must
 * never be stored in a floating-point type — {@code 0.1 + 0.2} is not
 * {@code 0.3} in binary floating point, and a pharmacy till that is one sen
 * out on every transaction is a bug the examiner will find.</p>
 *
 * @author Ramzi
 */
public class Medicine implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int LOW_STOCK_MAX = 20;
    private static final int OPTIMAL_STOCK_MAX = 80;

    private int        medicineId;
    private String     name;
    private String     category;
    private BigDecimal price;
    private int        quantityInStock;
    private Date       expiryDate;

    public Medicine() {
        this.price = BigDecimal.ZERO;
    }

    /** @return true when there are no units available. */
    public boolean isOutOfStock() {
        return quantityInStock <= 0;
    }

    /** @return true when stock is between 1 and 20 units. */
    public boolean isLowStock() {
        return !isOutOfStock() && quantityInStock <= LOW_STOCK_MAX;
    }

    /** @return true when stock is 81 units or more. */
    public boolean isExcessStock() {
        return quantityInStock > OPTIMAL_STOCK_MAX;
    }

    /**
     * Human-readable stock status used by JSP Expression Language as
     * {@code ${m.stockStatus}}.
     *
     * @return Out of Stock, Low, Optimal, or Excess (High).
     */
    public String getStockStatus() {
        if (isOutOfStock()) {
            return "Out of Stock";
        }
        if (isLowStock()) {
            return "Low";
        }
        if (isExcessStock()) {
            return "Excess (High)";
        }
        return "Optimal";
    }

    /** @return the CSS badge class matching the current stock status. */
    public String getStockStatusCssClass() {
        if (isOutOfStock()) {
            return "badge-out";
        }
        if (isLowStock()) {
            return "badge-low";
        }
        if (isExcessStock()) {
            return "badge-excess";
        }
        return "badge-ok";
    }

    public int getMedicineId()                      { return medicineId; }
    public void setMedicineId(int medicineId)       { this.medicineId = medicineId; }

    public String getName()                         { return name; }
    public void setName(String name)                { this.name = name; }

    public String getCategory()                     { return category; }
    public void setCategory(String category)        { this.category = category; }

    public BigDecimal getPrice()                    { return price; }
    public void setPrice(BigDecimal price)          { this.price = price; }

    public int getQuantityInStock()                 { return quantityInStock; }
    public void setQuantityInStock(int q)           { this.quantityInStock = q; }

    public Date getExpiryDate()                     { return expiryDate; }
    public void setExpiryDate(Date expiryDate)      { this.expiryDate = expiryDate; }


    @Override
    public String toString() {
        return "Medicine{id=" + medicineId + ", name=" + name
             + ", qty=" + quantityInStock + ", price=" + price + "}";
    }
}
