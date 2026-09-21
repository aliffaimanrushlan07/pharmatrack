<%--
    Login page.
    OWNER: ALIFF (Authentication, Security & Architecture)
    STATUS: COMPLETE

    Does not include header.jspf - the navigation bar would be meaningless to
    someone who is not logged in yet.

    Security note for the report: the form posts to /login and the password
    field is type="password". There is NO "remember me" and no client-side
    password handling at all - the plain password exists only for the duration
    of the POST request and is never written anywhere.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign in | PharmaTrack</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="login-wrap">
    <div class="login-card">

        <h1>PharmaTrack</h1>
        <p class="sub">Pharmacy Inventory &amp; Sales Management</p>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error"><c:out value="${errorMessage}"/></div>
        </c:if>
        <c:if test="${param.timeout eq '1'}">
            <div class="alert alert-warning">Your session expired. Please sign in again.</div>
        </c:if>
        <c:if test="${param.loggedOut eq '1'}">
            <div class="alert alert-success">You have been signed out.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">

            <div class="form-row">
                <label for="username">Username <span class="req">*</span></label>
                <input type="text" id="username" name="username"
                       required autofocus autocomplete="username">
            </div>

            <div class="form-row">
                <label for="password">Password <span class="req">*</span></label>
                <input type="password" id="password" name="password"
                       required autocomplete="current-password">
            </div>

            <button type="submit" class="btn">Sign in</button>
        </form>

        <%-- REMOVE THIS BLOCK BEFORE THE FINAL SUBMISSION.
             It is here so the group can log in during development. Leaving
             credentials printed on a login page in the submitted version
             would be marked down under the Security criterion. --%>
        <div class="demo-box">
            <strong>Test accounts (remove before submission)</strong><br>
            Admin: <code>admin</code> / <code>admin123</code><br>
            Cashier: <code>cashier</code> / <code>cashier123</code>
        </div>

    </div>
</div>

</body>
</html>
