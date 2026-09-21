<%--
    404 page.
    OWNER: Aliff (Architecture)
    Mapped in WEB-INF/web.xml. Lives under WEB-INF so it cannot be requested
    directly - the container forwards to it.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><title>Page not found | PharmaTrack</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="card empty">
        <h3>Page not found</h3>
        <p>That page does not exist in PharmaTrack.</p>
        <p style="margin-top:16px">
            <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn">Back to dashboard</a>
        </p>
    </div>
</div>
</body>
</html>
