package my.edu.uptm.pharmatrack.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Role-based access control — keeps CASHIER accounts out of ADMIN-only pages.
 *
 * <p>=====================================================================<br>
 * MODULE OWNER: <b>ALIFF</b> — Authentication, Security &amp; Architecture<br>
 * STATUS: <b>STUB — Aliff to implement</b><br>
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
 * <p><b>TODO 1 (ALIFF) — implement {@code doFilter}:</b></p>
 * <ol>
 *   <li>Cast to {@code HttpServletRequest} / {@code HttpServletResponse}.</li>
 *   <li>Read the {@link my.edu.uptm.pharmatrack.model.User} from the session
 *       under {@link AuthFilter#SESSION_USER}. {@code AuthFilter} has already
 *       guaranteed it is there for these URLs.</li>
 *   <li>If {@code user.isAdmin()} → {@code chain.doFilter(...)}.</li>
 *   <li>Otherwise → forward to {@code /WEB-INF/views/403.jsp} (create it) or
 *       redirect to the dashboard with an {@code error} message. Do <b>not</b>
 *       send them to the login page — they <i>are</i> logged in, they are just
 *       not permitted, and a login prompt would be confusing.</li>
 * </ol>
 *
 * <p><b>TODO 2 (ALIFF)</b> — once it works, widen {@code urlPatterns} below to
 * cover every admin-only URL. Coordinate with Amir first: he owns the medicine
 * screens and needs to know they are about to become admin-only.</p>
 *
 * @author Aliff
 */
@WebFilter(filterName = "RoleFilter", urlPatterns = {
    // TODO 2: add the rest once the filter body works.
    //   "/medicine",
    //   "/user/*",
    "/admin/*"
})
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to configure.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // TODO 1 (ALIFF): replace this pass-through with the real role check.
        //
        // Right now every logged-in user gets through, which means the filter
        // is doing nothing. Leaving it like this on submission day would cost
        // marks in the Security band.
        //
        // HttpServletRequest  req = (HttpServletRequest)  request;
        // HttpServletResponse res = (HttpServletResponse) response;
        // HttpSession session = req.getSession(false);
        // User user = (session == null) ? null
        //           : (User) session.getAttribute(AuthFilter.SESSION_USER);
        //
        // if (user != null && user.isAdmin()) {
        //     chain.doFilter(request, response);
        // } else {
        //     res.sendRedirect(req.getContextPath() + "/dashboard.jsp?error=forbidden");
        // }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nothing to release.
    }
}
