<%--
    Generic error page.
    OWNER: Aliff (Architecture)

    Deliberately shows NO stack trace. A stack trace on a public page leaks
    class names, file paths and library versions, and looks unfinished. The
    real exception still goes to the Tomcat log where the developer can read
    it - which is exactly where it belongs.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><title>Something went wrong | PharmaTrack</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="card empty">
        <h3>Something went wrong</h3>
        <p>The system could not complete that request. The most common cause is
           that MySQL is not running, or <code>db.properties</code> has the wrong password.</p>
        <p style="margin-top:16px">
            <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn">Back to dashboard</a>
        </p>
        <p class="hint" style="margin-top:20px">
            Developers: the full stack trace is in the Tomcat output window in NetBeans.
        </p>
    </div>
</div>
</body>
</html>
