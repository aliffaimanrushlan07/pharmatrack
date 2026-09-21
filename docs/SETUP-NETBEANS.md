# Setup — NetBeans, Tomcat, MySQL

Everyone does this once. Budget 45 minutes the first time.

---

## 1. Install

| Software | Version | Where |
|----------|---------|-------|
| **JDK** | 8 or 11 | [adoptium.net](https://adoptium.net) |
| **Apache NetBeans** | 12+ | [netbeans.apache.org](https://netbeans.apache.org/download/) |
| **Apache Tomcat** | **9.x** ⚠️ not 10 | [tomcat.apache.org](https://tomcat.apache.org/download-90.cgi) |
| **MySQL Server** | 8.0 | [dev.mysql.com](https://dev.mysql.com/downloads/mysql/) |
| **MySQL Workbench** | 8.0 | [dev.mysql.com](https://dev.mysql.com/downloads/workbench/) |
| **Git** | any | [git-scm.com](https://git-scm.com) |

> **⚠️ Tomcat 9, not Tomcat 10.** Tomcat 10 renamed every `javax.servlet`
> package to `jakarta.servlet`. This project uses `javax`, so on Tomcat 10 it
> will not compile. See the bottom of this page if you are stuck with 10.

The MySQL installer bundles both Server and Workbench — choose the **Developer
Default** setup type and you get everything. **Write down the root password you
set during installation.** You need it in step 4.

---

## 2. Clone the repository

```bash
git clone <repository-url>
cd PharmaTrack
```

---

## 3. Create the database

1. Open **MySQL Workbench**, click your local connection, enter your root password.
2. **File → Open SQL Script…** → `database/01_schema.sql`
3. Click the **⚡ lightning bolt** (Execute All).
   Output should read `PharmaTrack schema created successfully.`
4. Repeat for **`database/02_seed_data.sql`**.
5. Refresh the **SCHEMAS** panel — `pharmatrack` appears with 5 tables and 1 view.

Verify:

```sql
USE pharmatrack;
SELECT COUNT(*) FROM medicines;   -- expect 13
SELECT * FROM v_low_stock;        -- expect 3 rows
```

---

## 4. Set your password ⚠️ everyone must do this

Open `src/main/resources/db.properties` and change one line:

```properties
db.password=CHANGE_ME     →     db.password=your_actual_mysql_password
```

Do **not** commit your real password:

```bash
git update-index --skip-worktree src/main/resources/db.properties
```

(Undo later with `--no-skip-worktree` if the file legitimately needs to change.)

---

## 5. Add Tomcat to NetBeans

1. **Tools → Servers → Add Server…**
2. Choose **Apache Tomcat or TomEE** → Next
3. **Server Location:** browse to your Tomcat folder, e.g.
   - Windows `C:\Program Files\Apache Software Foundation\Tomcat 9.0`
   - macOS `/usr/local/apache-tomcat-9.0.xx` or `/opt/homebrew/opt/tomcat@9/libexec`
4. **Username** `admin`, **Password** `admin`, tick *Create user if it does not exist*
5. Finish.

---

## 6. Open and run the project

1. **File → Open Project…** → select the **PharmaTrack** folder (the one with
   `pom.xml` in it). NetBeans recognises it as a Maven project automatically.
2. Wait for the dependency download — the progress bar is bottom right. First
   time takes 1–3 minutes and **needs internet**.
3. Right-click the project → **Properties → Run** → set **Server** to your Tomcat 9.
4. Press **F6** (Run).
5. Browser opens at `http://localhost:8080/pharmatrack`
6. Log in: **`admin`** / **`admin123`**

You should land on the dashboard showing **13 medicines** and **3 low stock**.
If you do, your setup is correct and you can start work.

---

## Troubleshooting

### `Access denied for user 'root'@'localhost'`
Wrong password in `db.properties`. It must match the one you set when installing
MySQL.

### `Communications link failure` / `Connection refused`
MySQL **Server** is not running (Workbench is only the client).

- **Windows:** Win+R → `services.msc` → find `MySQL80` → Start
- **macOS:** System Settings → MySQL → Start, or `brew services start mysql`

### `Unknown database 'pharmatrack'`
You skipped step 3. Run `01_schema.sql`.

### `ClassNotFoundException: com.mysql.cj.jdbc.Driver`
Dependencies did not download. Right-click the project → **Clean and Build**.
If it still fails you were offline during the first build — connect and rebuild.

### Workbench: "Incompatible/nonstandard server version detected (26.7.0)"
**Harmless — click "Continue Anyway"** and tick "Don't show this message again".

MySQL moved to calendar versioning in July 2026, so a freshly installed server
reports a version like `26.7.0`. MySQL Workbench's compatibility check only
knows about 5.6 / 5.7 / 8.0, so it flags anything newer. The connection works;
only a few Workbench-specific features (migration wizard, some modelling tools)
are affected. Nothing this project needs.

The project's JDBC driver is `com.mysql:mysql-connector-j:9.7.0`, which does
support these servers. If you see an authentication failure from Java rather
than from Workbench, check you are not on an older driver.

> **Agree one MySQL version across the group.** Four laptops on four different
> server versions is a needless source of "works on mine". Whatever the first
> person installs, everyone matches.

### `java.lang.ClassNotFoundException: javax.servlet.http.HttpServlet`
You are on **Tomcat 10+**. See the note at the bottom.

### Port 8080 already in use
Something else is on 8080 (often another Tomcat, or Skype on older Windows).
Either stop it, or change Tomcat's port: **Tools → Servers → Tomcat → Server Port**.

### `The server time zone value '+08' is unrecognized`
Already handled — the JDBC URL includes `serverTimezone=Asia/Kuala_Lumpur`. If
you edited `db.url`, put it back.

### `Public Key Retrieval is not allowed`
Also already handled via `allowPublicKeyRetrieval=true` in the URL.

### `404 Not Found` at `http://localhost:8080/pharmatrack`
The context path is set in `src/main/webapp/META-INF/context.xml`. Check that
the deployment succeeded in the NetBeans **Output** window.

### Page loads but has no styling
The CSS path is wrong, or the browser cached an old copy. Hard-refresh with
**Ctrl+Shift+R** (Cmd+Shift+R on macOS).

### Changes to a `.jsp` do not show
Tomcat caches compiled JSPs. Right-click project → **Clean and Build**, then
run again.

---

## If your machine only has Tomcat 10 / GlassFish 7

Those use the `jakarta.servlet` namespace. Switching the whole project is
mechanical but touches every Java file, so **agree it with the group first** —
a half-migrated project builds on nobody's machine.

1. In `pom.xml`, replace the `javax.servlet` dependencies with:
   ```xml
   <dependency>
       <groupId>jakarta.servlet</groupId>
       <artifactId>jakarta.servlet-api</artifactId>
       <version>5.0.0</version>
       <scope>provided</scope>
   </dependency>
   ```
2. Replace JSTL 1.2 with `jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:2.0.0`
   plus the Glassfish implementation.
3. Find-and-replace `javax.servlet` → `jakarta.servlet` across all `.java` files
   (NetBeans: **Ctrl+Shift+H**).
4. In every JSP, change the taglib URIs from `http://java.sun.com/jsp/jstl/core`
   to `jakarta.tags.core`.

Easier route: install Tomcat 9 alongside whatever else you have. It is a zip
file, it does not conflict, and it takes two minutes.
