package my.edu.uptm.pharmatrack.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Ends the user's session.
 *
 * <p>MODULE OWNER: <b>ALIFF</b> — Authentication, Security &amp; Architecture.
 * STATUS: <b>COMPLETE</b>.</p>
 *
 * <p>Short, but there are two things here people get wrong:</p>
 * <ul>
 *   <li>{@code session.invalidate()} destroys the session <i>server-side</i>.
 *       Just removing the user attribute would leave the session alive and
 *       reusable.</li>
 *   <li>{@code sendRedirect} rather than {@code forward}, so the browser's
 *       address bar ends up on the login page. After a forward the URL would
 *       still read {@code /logout}, and a refresh would be confusing.</li>
 * </ul>
 *
 * @author Aliff
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/login.jsp?loggedOut=1");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }
}
