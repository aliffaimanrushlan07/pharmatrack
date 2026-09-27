package my.edu.uptm.pharmatrack.service;

import my.edu.uptm.pharmatrack.model.Sale;
import my.edu.uptm.pharmatrack.model.SaleItem;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class SalesCalculatorTest {

    @Test
    public void emptyBasketHasZeroSubtotal() {
        assertEquals(new BigDecimal("0.00"), SalesCalculator.calculateSubtotal(null));
    }

    @Test
    public void basketSubtotalUsesQuantityAndUnitPrice() {
        Sale sale = new Sale();
        sale.addItem(new SaleItem(1, 2, new BigDecimal("12.50")));
        sale.addItem(new SaleItem(2, 1, new BigDecimal("18.90")));

        assertEquals(new BigDecimal("43.90"),
                SalesCalculator.calculateSubtotal(sale.getItems()));
        assertEquals(3, sale.getTotalUnits());
    }
}
