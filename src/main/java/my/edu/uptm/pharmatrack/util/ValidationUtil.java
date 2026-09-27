package my.edu.uptm.pharmatrack.util;

/**
 * Small helpers for turning untrusted request parameters into usable values.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>AMIR</b> — CRUD &amp; Core Application Module<br>
 * STATUS: <b>COMPLETE</b>.<br>
 * =====================================================================</p>
 *
 * <p>Everything arriving from a browser is a {@code String} and may be null,
 * blank, or deliberate nonsense. Centralising the conversions here keeps the
 * servlets readable and means a fix applies everywhere at once.</p>
 *
 * @author Amir
 */
public final class ValidationUtil {

    private ValidationUtil() {
        throw new AssertionError("ValidationUtil is a utility class.");
    }

    /**
     * Parses an integer without throwing.
     *
     * @param raw          the request parameter, possibly null or rubbish.
     * @param defaultValue returned when {@code raw} cannot be parsed.
     * @return the parsed value, or {@code defaultValue}.
     */
    public static int parseInt(String raw, int defaultValue) {
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    /**
     * @param value any string, possibly null.
     * @return true when the value is null, empty, or only whitespace.
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * @param value any string, possibly null.
     * @return the trimmed value, or an empty string when null.
     */
    public static String trimToEmpty(String value) {
        return (value == null) ? "" : value.trim();
    }

    /**
     * Basic email format check for form input.
     *
     * <p>A simple regex is enough here. Do not try to write a fully
     * RFC-compliant email regex; it is famously several hundred characters
     * long and still gets edge cases wrong.</p>
     *
     * @param email the address to check; blank counts as valid (optional field).
     * @return true when the address looks plausible.
     */
    public static boolean isValidEmail(String email) {
        return isBlank(email)
            || email.trim().matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");
    }

    /**
     * Malaysian phone number check for form input.
     *
     * <p>Accept the formats actually used: {@code 03-3342 2222},
     * {@code 0333422222}, {@code +603 3342 2222}. Strip spaces and dashes
     * first, then check length and prefix.</p>
     *
     * @param phone the number to check; blank counts as valid (optional field).
     * @return true when the number looks plausible.
     */
    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) {
            return true;
        }

        String normalised = phone.trim().replaceAll("[\\s-]", "");
        return normalised.matches("^(?:\\+60|0)\\d{8,10}$");
    }

    /**
     * Escapes HTML before echoing user input back into a page.
     *
     * <p>Defends against stored XSS: if someone saves a medicine named
     * {@code <script>alert(1)</script>}, the list page must display that text,
     * not run it.</p>
     *
     * <p>Note: the JSPs in this project already use JSTL's {@code <c:out>},
     * which escapes automatically, so this is a belt-and-braces helper for any
     * place that writes output directly. Mention the {@code <c:out>} choice in
     * your section of the report — it is a real security decision.</p>
     *
     * @param input raw text from the database or a form.
     * @return the same text with {@code & < > " '} replaced by entities.
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }

        // Ampersand must be replaced first so the entities introduced by the
        // later replacements are not escaped a second time.
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
