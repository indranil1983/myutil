<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html lang="en"><head>

	<meta charset="utf-8">

	<title>App Resource Tool</title>

	<link rel="stylesheet" href="assets/custom/login.css" media="screen">
	<link rel="stylesheet"  href="assets/custom/app.css" />
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<style>body{margin: 0 auto;width: 960px;padding-top: 20px}.footer{margin-top:50px;text-align:center;color:#666;font:bold 14px Arial}.footer a{color:#999;text-decoration:none}.login-form{margin: 50px auto;}</style>
	<meta name="robots" content="noindex,follow">
	<link rel="shortcut icon" href="assets/images/favicon/favicon.ico" type="image/x-icon">
	<link rel="icon" href="assets/images/favicon/favicon.ico" type="image/x-icon">
	<meta name="theme-color" content="#ffffff">
	
	
</head>

<body class="js">

<div align="center">
	<div style="display: inline;">
		<img src="assets\images\genie.gif" alt="title" height="125px">
		<div style="font-weight: bold;">EBI UTILITY TOOL</div>
		
	</div>
	
</div>


<div class="login-form">

	<h1>Login Form </h1>
	<c:if test="${param.denied}">
		<font color="red">Access denied or you have to change your first-time password.</font>
	</c:if>
	<c:if test="${param.logout}">
		<font color="red">Successfully logged out</font>
	</c:if>
	<c:if test="${param.invalid}">
		<font color="red">Session got invalidated due to inactivity</font>
	</c:if>
	<form name='loginForm'
		  action="<c:url value='/j_spring_security_check' />" method='POST'>

		<input type="text" name="username"  placeholder="username" value="" autocomplete="off" autofocus="autofocus">
		
		<input type="password" name="password" placeholder="password" value="">
		
		<input type="submit" class="loginButton" value="Login" name="submit">

	</form>

</div>

<div class="footer"><p>Developed by CAC TEAM</p></div>



<script type="text/javascript">


</script>


</body></html>