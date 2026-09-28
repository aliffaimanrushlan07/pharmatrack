<%--
    403 page - shown when a logged-in user reaches a page their role does not
    permit (in practice: a CASHIER trying to reach the medicine module or the
    inventory valuation report).
    OWNER: Aliff (Authentication, Security & Architecture)

    Mapped in WEB-INF/web.xml. Lives under WEB-INF so it cannot be requested
    directly - the container forwards to it after RoleFilter calls sendError.

    Note this is deliberately NOT the login page. The user IS authenticated;
    they are simply not authorised. Sending them to a login prompt would
    suggest their password was wrong, which it was not.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><title>Access denied | PharmaTrack</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="card empty">
        <h3>Access denied</h3>
        <p>Your account does not have permission to open this page.</p>
        <p>The medicine module and the inventory valuation report are
           restricted to administrator accounts. If you need a change made to
           the medicine records, please ask an administrator.</p>
        <p style="margin-top:16px">
            <a href="${pageContext.request.contextPath}/dashboard" class="btn">Back to dashboard</a>
        </p>
    </div>
</div>
</body>
</html>
