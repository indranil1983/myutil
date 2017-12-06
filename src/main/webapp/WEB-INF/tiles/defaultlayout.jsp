<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insertAttribute name="directives" />
 
<html>

<head>
	<meta name="theme-color" content="#ffffff">	
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title><tiles:getAsString name="title" /></title>
	<link rel="shortcut icon" href="assets/images/favicon/favicon.ico" type="image/x-icon">
	<link rel="icon" href="assets/images/favicon/favicon.ico" type="image/x-icon">
	
	<link rel="stylesheet"  href="assets\jquery-ui-css-1.9.2\<tiles:getAsString name="jquery-color" />\jquery-ui-1.9.2.custom.min.css" />
	<tiles:insertAttribute name="header" />
</head>

<body>
        <tiles:insertAttribute name="menu" />   
        <tiles:insertAttribute name="body" />      
        <%-- <footer id="footer">
            <tiles:insertAttribute name="footer" />
        </footer> --%>
</body>
</html>