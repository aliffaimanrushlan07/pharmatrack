package my.edu.uptm.pharmatrack.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model (POJO) representing one row of the <code>users</code> table.
 *
 * <p>MODULE OWNER: <b>RAMZI</b> — Database Design &amp; Data Access Layer.
 * Aliff uses this class in the authentication module; if a field needs to
 * change, agree it with Ramzi first because the DAO maps it column by column.</p>
 *
 * <p>Note that {@link #passwordHash} holds a <b>BCrypt hash</b>, never a plain
 * password. Nothing in the system should ever be able to read the real
 * password back out.</p>
 *
 * @author Ramzi
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Role constant — full access, can manage medicines and users. */
    public static final String ROLE_ADMIN = "ADMIN";
    /** Role constant — can process sales and search, but not manage records. */
    public static final String ROLE_CASHIER = "CASHIER";

    private int       userId;
    private String    username;
    private String    passwordHash;
    private String    fullName;
    private String    role;
    private boolean   active;
    private Timestamp createdAt;

    public User() {
        // Required no-argument constructor — JavaBeans / JSP EL needs it.
    }

    public User(int userId, String username, String fullName, String role) {
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.role     = role;
    }

    /** @return true when this user has the ADMIN role. Used by RoleFilter. */
    public boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }

    public int getUserId()                        { return userId; }
    public void setUserId(int userId)             { this.userId = userId; }

    public String getUsername()                   { return username; }
    public void setUsername(String username)      { this.username = username; }

    public String getPasswordHash()               { return passwordHash; }
    public void setPasswordHash(String hash)      { this.passwordHash = hash; }

    public String getFullName()                   { return fullName; }
    public void setFullName(String fullName)      { this.fullName = fullName; }

    public String getRole()                       { return role; }
    public void setRole(String role)              { this.role = role; }

    public boolean isActive()                     { return active; }
    public void setActive(boolean active)         { this.active = active; }

    public Timestamp getCreatedAt()               { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        // passwordHash deliberately excluded so it never leaks into a log file.
        return "User{id=" + userId + ", username=" + username + ", role=" + role + "}";
    }
}
