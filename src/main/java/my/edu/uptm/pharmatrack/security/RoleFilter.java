package my.edu.uptm.pharmatrack.security;

import my.edu.uptm.pharmatrack.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Role-based access control - keeps CASHIER accounts out of ADMIN-only pages.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>ALIFF</b> - Authentication, Security &amp; Architecture<br>
 * STATUS: <b>COMPLETE</b><br>
 * =====================================================================</p>
 *
 * <p>{@link AuthFilter} answers "is this person logged in?". This filter
 * answers the harder question: "is this person <i>allowed</i> to be here?"
 * Together they are authentication and authorisation, and naming them
 * correctly in the report is worth doing - they are commonly confused.</p>
 *
 * <p><b>Why this matters for the demo.</b> Hiding an admin-only link from
 * cashiers in the JSP is <i>not</i> security - a cashier who types the URL
 * straight into the address bar still gets in. This filter is what actually
 * stops them. The JSP changes in {@code includes/header.jspf} and
 * {@code dashboard.jsp} only remove the temptation; this class removes the
 * access. Demonstrating exactly that (log in as cashier, type
 * {@code /medicine} into the address bar, get the 403 page) is a strong
 * 30 seconds of the presentation.</p>
 *
 * <p><b>What is restricted.</b></p>
 * <ul>
 *   <li>{@code /medicine} - the whole medicine module, admin only.</li>
 *   <li>{@code /report} - admin only <i>except</i> the sales summary
 *       ({@code ?type=sales}), which cashiers are allowed to see. The
 *       inventory valuation report exposes stock levels and cost price for
 *       every medicine, so it follows the medicine module.</li>
 *   <li>{@code /user/*} and {@code /admin/*} - reserved for future admin
 *       screens; locked from the start so they cannot be forgotten later.</li>
 * </ul>
 *
 * <p>Denial is a <b>403 Forbidden</b>, not a redirect to the login page: the
 * user <i>is</i> logged in, they are simply not permitted. Sending them to a
 * login prompt would wrongly suggest their password failed. The status code
 * is set with {@code sendError} so the container renders the shared error
 * page declared in {@code web.xml}, which is the same mechanism 404 and 500
 * already use.</p>
 *
 * @author Aliff
 */
@WebFilter(filterName = "RoleFilter", urlPatterns = {
    "/medicine",
    "/report",
    "/user/*",
    "/admin/*"
})
public class RoleFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(RoleFilter.class.getName());

    /** The one report a non-admin is allowed to open. */
    private static final String REPORT_SALES = "sales";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to configure.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req = (HttpServletRequest)  request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session == null) ? null
                : (User) session.getAttribute(AuthFilter.SESSION_USER);

        // AuthFilter runs first and should already have stopped anonymous
        // requests. This is belt and braces: if the session died between the
        // two filters, treat it as "not logged in" rather than "not allowed".
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (user.isAdmin() || !isAdminOnly(req)) {
            chain.doFilter(request, response);
            return;
        }

        // Logged in, but not permitted. Log it - an audit trail of refused
        // access attempts is exactly what a security review looks for - then
        // hand over to the 403 page.
        LOGGER.warning("Access denied: user '" + user.getUsername()
                + "' (role " + user.getRole() + ") requested " + req.getRequestURI());

        res.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * Is the requested resource restricted to administrators?
     *
     * <p>Most of the URLs this filter is mapped to are admin-only outright.
     * {@code /report} is the exception: one servlet serves two reports and
     * they are told apart by a query parameter, which a URL pattern cannot
     * express. Keeping that judgement here rather than inside
     * {@code ReportServlet} means every authorisation decision in the
     * application is made in one class.</p>
     *
     * @param req the request being filtered
     * @return true when only an ADMIN may proceed
     */
    private boolean isAdminOnly(HttpServletRequest req) {

        if ("/report".equals(req.getServletPath())) {
            // ReportServlet falls back to the stock valuation report when no
            // type is supplied, so anything that is not explicitly the sales
            // report is the inventory report - and that is admin-only.
            return !REPORT_SALES.equals(req.getParameter("type"));
        }

        return true;
    }

    @Override
    public void destroy() {
        // Nothing to release.
    }
}
