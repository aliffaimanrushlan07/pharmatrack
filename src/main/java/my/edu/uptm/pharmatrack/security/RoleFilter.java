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
import java.net.URLEncoder;

/**
 * Role-based access control — keeps CASHIER accounts out of ADMIN-only pages.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>ALIFF</b> — Authentication, Security &amp; Architecture<br>
 * STATUS: <b>COMPLETE</b><br>
 * =====================================================================</p>
 *
 * <p>{@link AuthFilter} answers "is this person logged in?". This filter
 * answers the harder question: "is this person <i>allowed</i> to be here?"
 * Together they are authentication and authorisation, and naming them
 * correctly in the report is worth doing — they are commonly confused.</p>
 *
 * <p><b>Why this matters for the demo.</b> Hiding an admin-only link from
 * cashiers in the JSP is <i>not</i> security — a cashier who types the URL
 * straight into the address bar still gets in. This filter is what
 * actually stops them. Demonstrating exactly that (log in as cashier, type
 * the admin URL, get refused) is a strong 30 seconds of your presentation.</p>
 *
 * <p><b>How it works:</b> the logged-in {@link User} is read from the
 * session (put there by {@link AuthFilter}, which runs first). Admins pass
 * through; anyone else is redirected to the dashboard with an error message.
 * They are <i>not</i> sent to the login page — they are logged in, just not
 * permitted, and a login prompt would be confusing.</p>
 *
 * <p>The redirect goes to {@code /dashboard} (the servlet), not
 * {@code dashboard.jsp}, so the dashboard tiles are filled in as usual.</p>
 *
 * @author Aliff
 */
@WebFilter(filterName = "RoleFilter", urlPatterns = {
    // Admin-only areas. /medicine covers list, add, edit and delete.
    "/medicine",
    "/user/*",
    "/admin/*"
})
public class RoleFilter implements Filter {

    /** Shown to a cashier who tries to open an admin-only page. */
    static final String FORBIDDEN_MESSAGE =
            "Access denied: only an Admin can manage medicines.";

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

        if (user != null && user.isAdmin()) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/dashboard?error="
                    + URLEncoder.encode(FORBIDDEN_MESSAGE, "UTF-8"));
        }
    }

    @Override
    public void destroy() {
        // Nothing to release.
    }
}
