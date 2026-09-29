package my.edu.uptm.pharmatrack.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MedicineStockStatusTest {

    private final Medicine medicine = new Medicine();

    @Test
    public void zeroStockIsOutOfStock() {
        medicine.setQuantityInStock(0);

        assertEquals("Out of Stock", medicine.getStockStatus());
        assertEquals("badge-out", medicine.getStockStatusCssClass());
    }

    @Test
    public void twentyUnitsIsLow() {
        medicine.setQuantityInStock(20);

        assertEquals("Low", medicine.getStockStatus());
        assertEquals("badge-low", medicine.getStockStatusCssClass());
    }

    @Test
    public void twentyOneUnitsIsOptimal() {
        medicine.setQuantityInStock(21);

        assertEquals("Optimal", medicine.getStockStatus());
        assertEquals("badge-ok", medicine.getStockStatusCssClass());
    }

    @Test
    public void eightyUnitsIsOptimal() {
        medicine.setQuantityInStock(80);

        assertEquals("Optimal", medicine.getStockStatus());
        assertEquals("badge-ok", medicine.getStockStatusCssClass());
    }

    @Test
    public void eightyOneUnitsIsExcess() {
        medicine.setQuantityInStock(81);

        assertEquals("Excess (High)", medicine.getStockStatus());
        assertEquals("badge-excess", medicine.getStockStatusCssClass());
    }
}
