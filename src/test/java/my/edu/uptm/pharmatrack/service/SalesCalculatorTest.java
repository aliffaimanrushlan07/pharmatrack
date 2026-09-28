package my.edu.uptm.pharmatrack.service;

import my.edu.uptm.pharmatrack.model.Sale;
import my.edu.uptm.pharmatrack.model.SaleItem;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SalesCalculatorTest {

    // ---------------------------------------------------------------- subtotal

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

    // ---------------------------------------------------------------- discount

    @Test
    public void tenPercentDiscount() {
        assertEquals(new BigDecimal("90.00"),
                SalesCalculator.applyDiscount(new BigDecimal("100.00"), new BigDecimal("10")));
    }

    @Test
    public void discountRoundsHalfUpOnlyAtTheEnd() {
        // 43.90 x 0.875 = 38.4125 -> 38.41
        assertEquals(new BigDecimal("38.41"),
                SalesCalculator.applyDiscount(new BigDecimal("43.90"), new BigDecimal("12.5")));
    }

    @Test
    public void discountAboveHundredIsClampedToFree() {
        assertEquals(new BigDecimal("0.00"),
                SalesCalculator.applyDiscount(new BigDecimal("50.00"), new BigDecimal("150")));
    }

    @Test
    public void negativeDiscountIsClampedToNone() {
        assertEquals(new BigDecimal("50.00"),
                SalesCalculator.applyDiscount(new BigDecimal("50.00"), new BigDecimal("-20")));
    }

    @Test
    public void nullDiscountLeavesAmountUnchanged() {
        assertEquals(new BigDecimal("12.50"),
                SalesCalculator.applyDiscount(new BigDecimal("12.5"), null));
    }

    // ---------------------------------------------------------------- tax

    @Test
    public void sstIsSixPercent() {
        assertEquals(new BigDecimal("6.00"),
                SalesCalculator.calculateTax(new BigDecimal("100.00")));
    }

    @Test
    public void sstRoundsHalfUp() {
        // 18.90 x 0.06 = 1.134 -> 1.13 ; 12.75 x 0.06 = 0.765 -> 0.77
        assertEquals(new BigDecimal("1.13"), SalesCalculator.calculateTax(new BigDecimal("18.90")));
        assertEquals(new BigDecimal("0.77"), SalesCalculator.calculateTax(new BigDecimal("12.75")));
    }

    @Test
    public void sstOnNullOrNegativeIsZero() {
        assertEquals(new BigDecimal("0.00"), SalesCalculator.calculateTax(null));
        assertEquals(new BigDecimal("0.00"), SalesCalculator.calculateTax(new BigDecimal("-5")));
    }

    // ---------------------------------------------------------------- change

    @Test
    public void changeDue() {
        assertEquals(new BigDecimal("6.10"),
                SalesCalculator.calculateChange(new BigDecimal("43.90"), new BigDecimal("50")));
    }

    @Test
    public void shortPaymentIsNegative() {
        assertEquals(new BigDecimal("-3.50"),
                SalesCalculator.calculateChange(new BigDecimal("23.50"), new BigDecimal("20.00")));
    }

    @Test
    public void exactPaymentGivesZeroChange() {
        assertEquals(new BigDecimal("0.00"),
                SalesCalculator.calculateChange(new BigDecimal("10.00"), new BigDecimal("10")));
    }

    // ---------------------------------------------------------------- average

    @Test
    public void averageOfNoSalesIsZeroNotAnException() {
        assertEquals(new BigDecimal("0.00"), SalesCalculator.calculateAverageSale(null));
        assertEquals(new BigDecimal("0.00"),
                SalesCalculator.calculateAverageSale(Collections.<Sale>emptyList()));
    }

    @Test
    public void averageSale() {
        List<Sale> sales = Arrays.asList(sale("58.10"), sale("104.30"), sale("10.00"));
        // 172.40 / 3 = 57.4666 -> 57.47
        assertEquals(new BigDecimal("57.47"), SalesCalculator.calculateAverageSale(sales));
    }

    @Test
    public void averageWithZeroTransactionsIsZero() {
        assertEquals(new BigDecimal("0.00"),
                SalesCalculator.calculateAverage(new BigDecimal("100"), 0));
    }

    // ---------------------------------------------------------------- report totals

    @Test
    public void overallAverageIsWeightedNotMeanOfDailyAverages() {
        List<Map<String, Object>> summary = new ArrayList<>();
        summary.add(day(1, "10.00"));   // quiet day, avg 10.00
        summary.add(day(9, "450.00"));  // busy day, avg 50.00

        BigDecimal revenue = SalesCalculator.totalRevenue(summary);
        int transactions   = SalesCalculator.totalTransactions(summary);

        assertEquals(new BigDecimal("460.00"), revenue);
        assertEquals(10, transactions);
        // Correct: 460 / 10 = 46.00. The wrong "mean of averages" would be 30.00.
        assertEquals(new BigDecimal("46.00"), SalesCalculator.calculateAverage(revenue, transactions));
    }

    @Test
    public void emptySummaryTotalsAreZero() {
        assertEquals(new BigDecimal("0.00"), SalesCalculator.totalRevenue(null));
        assertEquals(0, SalesCalculator.totalTransactions(null));
    }

    // ---------------------------------------------------------------- helpers

    private static Sale sale(String total) {
        Sale s = new Sale();
        s.setTotalAmount(new BigDecimal(total));
        return s;
    }

    private static Map<String, Object> day(int transactions, String revenue) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("transactions", transactions);
        row.put("revenue", new BigDecimal(revenue));
        return row;
    }
}
