package my.edu.uptm.pharmatrack.model;

import java.io.Serializable;

/**
 * Model (POJO) representing one row of the <code>suppliers</code> table.
 *
 * <p>MODULE OWNER: <b>RAMZI</b> — Database Design &amp; Data Access Layer.
 * Consumed by Amir's supplier CRUD screens.</p>
 *
 * @author Ramzi
 */
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    private int    supplierId;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;

    /**
     * Not a database column — filled in by the "list suppliers" query so the
     * JSP can show how many medicines each supplier provides.
     */
    private int medicineCount;

    public Supplier() {
    }

    public int getSupplierId()                     { return supplierId; }
    public void setSupplierId(int supplierId)      { this.supplierId = supplierId; }

    public String getName()                        { return name; }
    public void setName(String name)               { this.name = name; }

    public String getContactPerson()               { return contactPerson; }
    public void setContactPerson(String c)         { this.contactPerson = c; }

    public String getPhone()                       { return phone; }
    public void setPhone(String phone)             { this.phone = phone; }

    public String getEmail()                       { return email; }
    public void setEmail(String email)             { this.email = email; }

    public String getAddress()                     { return address; }
    public void setAddress(String address)         { this.address = address; }

    public int getMedicineCount()                  { return medicineCount; }
    public void setMedicineCount(int n)            { this.medicineCount = n; }

    @Override
    public String toString() {
        return "Supplier{id=" + supplierId + ", name=" + name + "}";
    }
}
