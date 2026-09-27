package my.edu.uptm.pharmatrack.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationUtilTest {

    @Test
    public void emailAllowsBlankOptionalValue() {
        assertTrue(ValidationUtil.isValidEmail(null));
        assertTrue(ValidationUtil.isValidEmail("   "));
    }

    @Test
    public void emailAcceptsPlausibleAddresses() {
        assertTrue(ValidationUtil.isValidEmail("amir@example.com"));
        assertTrue(ValidationUtil.isValidEmail("amir.test+pharmacy@uptm.edu.my"));
    }

    @Test
    public void emailRejectsMalformedAddresses() {
        assertFalse(ValidationUtil.isValidEmail("amir.example.com"));
        assertFalse(ValidationUtil.isValidEmail("amir@localhost"));
        assertFalse(ValidationUtil.isValidEmail("amir @example.com"));
    }

    @Test
    public void phoneAllowsBlankOptionalValue() {
        assertTrue(ValidationUtil.isValidPhone(null));
        assertTrue(ValidationUtil.isValidPhone(""));
    }

    @Test
    public void phoneAcceptsCommonMalaysianFormats() {
        assertTrue(ValidationUtil.isValidPhone("03-3342 2222"));
        assertTrue(ValidationUtil.isValidPhone("0333422222"));
        assertTrue(ValidationUtil.isValidPhone("+603 3342 2222"));
        assertTrue(ValidationUtil.isValidPhone("012-345 6789"));
    }

    @Test
    public void phoneRejectsInvalidValues() {
        assertFalse(ValidationUtil.isValidPhone("12345"));
        assertFalse(ValidationUtil.isValidPhone("+44 20 1234 5678"));
        assertFalse(ValidationUtil.isValidPhone("03-ABC 2222"));
    }

    @Test
    public void escapeHtmlHandlesNullAndAllSpecialCharacters() {
        assertEquals("", ValidationUtil.escapeHtml(null));
        assertEquals("&amp;&lt;&gt;&quot;&#39;",
                ValidationUtil.escapeHtml("&<>\"'"));
    }

    @Test
    public void escapeHtmlLeavesOrdinaryTextUnchanged() {
        assertEquals("Paracetamol 500mg",
                ValidationUtil.escapeHtml("Paracetamol 500mg"));
    }
}
