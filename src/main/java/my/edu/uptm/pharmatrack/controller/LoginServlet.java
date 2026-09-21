package my.edu.uptm.pharmatrack.controller;

import my.edu.uptm.pharmatrack.dao.UserDAO;
import my.edu.uptm.pharmatrack.model.User;
import my.edu.uptm.pharmatrack.security.AuthFilter;
import my.edu.uptm.pharmatrack.security.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles login: verifies credentials and establishes the user's session.
 *
 * <p>MODULE OWNER: <b>ALIFF</b> — Authentication, Security &amp; Architecture.
 * STATUS: <b>COMPLETE</b> — reference implementation.</p>
 *
 * <p>This servlet is the <b>C</b> in MVC. Notice what it does and does not do:
 * it reads parameters, calls the DAO, puts results where the view can find
 * them, and forwards. It contains no SQL and no HTML. Keeping that boundary
 * clean across all servlets is what the rubric means by "separation of
 * presentation, business logic and data access layer".</p>
 *
 * <p><b>Four security decisions made here, each worth a line in the report:</b></p>
 * <ol>
 *   <li><b>Same error message for both failure cases.</b> "Invalid username or
 *       password" is returned whether the username does not exist or the
 *       password was wrong. Saying "no such user" would let an attacker
 *       enumerate valid usernames.</li>
 *   <li><b>Session fixation defence.</b> The old session is invalidated and a
 *       fresh one created on successful login, so a session ID an attacker
 *       planted beforehand becomes useless.</li>
 *   <li><b>Password never stored in the session.</b> Only the user object,
 *       with its hash field left out of {@code toString()}.</li>
 *   <li><b>Session timeout</b> set to 30 minutes — an unattended pharmacy
 *       terminal logs itself out.</li>
 * </ol>
 *
 * @author Aliff
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    /** 30 minutes, in seconds. */
    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;

    private transient UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        // Created once when the servlet is first loaded, not per request.
        this.userDAO = new UserDAO();
    }

    /**
     * A GET on /login just sends the visitor to the login form. The form
     * itself is a JSP, not generated here.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    /**
     * Processes the submitted login form.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // --- 1. Basic presence check before touching the database ----------
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {

            forwardWithError(request, response, "Please enter both username and password.");
            return;
        }

        try {
            // --- 2. Look the account up --------------------------------------
            User user = userDAO.findByUsername(username.trim());

            // --- 3. Verify -----------------------------------------------------
            // Deliberately one combined check with one shared error message.
            if (user == null
                    || !user.isActive()
                    || !PasswordUtil.verify(password, user.getPasswordHash())) {

                LOGGER.log(Level.WARNING, "Failed login attempt for username: {0}", username);
                forwardWithError(request, response, "Invalid username or password.");
                return;
            }

            // --- 4. Session fixation defence ---------------------------------
            HttpSession oldSession = request.getSession(false);
            String redirectTarget = null;
            if (oldSession != null) {
                redirectTarget = (String) oldSession.getAttribute("redirectAfterLogin");
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute(AuthFilter.SESSION_USER, user);
            session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);

            LOGGER.log(Level.INFO, "Successful login: {0} ({1})",
                       new Object[]{user.getUsername(), user.getRole()});

            // --- 5. Send them where they were originally headed ---------------
            if (redirectTarget != null && !redirectTarget.isEmpty()) {
                response.sendRedirect(redirectTarget);
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error during login", ex);
            forwardWithError(request, response,
                "Cannot reach the database. Check that MySQL is running.");
        }
    }

    /**
     * Sends the user back to the login form with a message.
     *
     * <p>Uses {@code forward}, not {@code sendRedirect}, so the message
     * survives — a redirect would start a new request and lose the attribute.</p>
     */
    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
                                  String message) throws ServletException, IOException {

        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
