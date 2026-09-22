package my.edu.uptm.pharmatrack.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Model (POJO) representing one row of the <code>medicines</code> table.
 *
 * <p>MODULE OWNER: <b>RAMZI</b> — Database Design &amp; Data Access Layer.</p>
 *
 * <p>{@code reorderLevel} is kept purely to drive the Low/OK badge on the
 * medicine list via {@link #isLowStock()}. The automatic low-stock report and
 * reorder-quantity suggestion were removed from the system scope.</p>
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

    private int        medicineId;
    private String     name;
    private String     category;
    private BigDecimal price;
    private int        quantityInStock;
    private int        reorderLevel;
    private Date       expiryDate;

    public Medicine() {
        this.price = BigDecimal.ZERO;
    }

    /**
     * Business rule behind the Low/OK badge on the medicine list.
     *
     * @return true when stock has fallen to or below the reorder threshold.
     */
    public boolean isLowStock() {
        return quantityInStock <= reorderLevel;
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

    public int getReorderLevel()                    { return reorderLevel; }
    public void setReorderLevel(int reorderLevel)   { this.reorderLevel = reorderLevel; }

    public Date getExpiryDate()                     { return expiryDate; }
    public void setExpiryDate(Date expiryDate)      { this.expiryDate = expiryDate; }


    @Override
    public String toString() {
        return "Medicine{id=" + medicineId + ", name=" + name
             + ", qty=" + quantityInStock + ", price=" + price + "}";
    }
}
