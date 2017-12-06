<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page session="true"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"   pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" href="assets\custom\app.css" >
 <script src="assets\growl\jquery.growl.js" type="text/javascript"></script>
 <link href="assets\growl\jquery.growl.css" rel="stylesheet" type="text/css" />  
 <script type="text/javascript" src="assets\custom/diff.js" ></script>
 <script type="text/javascript" src="assets\custom/HashMap.js" ></script>
 <script type="text/javascript" src="assets\custom/spin.js" ></script>
  <link rel="stylesheet" href="assets\font-awesome-4.7.0/css/font-awesome.min.css">
  
   <script type="text/javascript" src="assets\mergely-3.4.4\lib\codemirror.min.js"></script>
  <link type="text/css" rel="stylesheet" href="assets\mergely-3.4.4\lib\codemirror.css" />

<script type="text/javascript" src="assets\mergely-3.4.4\lib\mergely.js"></script>
<link type="text/css" rel="stylesheet" href="assets\mergely-3.4.4\lib\mergely.css" />

</head>
<body>
	<div id="glasspane" style="display:none">
		<div id='glasspaneContainer' class="glasspaneContainer"></div> 
    	<div id="spinnerpane" class="glassPaneCenter"></div>  
	</div>
	<header>
    <div class="nav">
      <ul>
      	<li style="margin-top: 5px"><img style="" alt="Ericsson" height="20px" src="assets\images/elogo.png"><span style="padding-left:5px;position: relative;bottom: 5px;">EBI TOOL</span></li>
       	<li class="about"></li>
        <li class="home"><a href="home">Home</a></li>
        <sec:authorize access="hasAuthority('EBI_OPS') or hasAuthority('EBI_ADMIN')">
        	<li class="tutorials"><a href="production">Production</a></li>
        </sec:authorize>
        <sec:authorize access="hasAuthority('EBI_DEV') or hasAuthority('EBI_ADMIN') or hasAuthority('EBI_LAB')">
        	<li class="about"><a href="lab">Dev/Lab</a></li>         
        </sec:authorize>
        <li class="about" style="float: right"><a href="<c:url value="j_spring_security_logout" />">Log Out</a></li>
      </ul>
    </div>
  </header>

	<div style="height: 10px;"></div>	 
	<div style="height:2px;background: url('https://login2.ericsson.net/login/V2_0/images/ebottomgrad.jpg') repeat-x 0 0;" />
		
	<div style="clear: both;padding-top: 10px;"></div>
	
	<div id="popup-dialog-summary" style="display:none;width:800px;">
		    <div id="summary_note"></div>
		    <table align="center" width="100%" border="1" bordercolor="white">
		    	<caption>Changes Summary</caption>
		    	<thead style="background-color: white;color: black;">
		    		<td width="10%">Action</td><td width="20%">Key</td>
		    		<td width="35%">English Value</td>
		    		<td width="35%" >Spanish Value</td>
		    	</thead>
			    <tbody id="summaryTable"/>
		  	</table>
		</div>
	
	<div id="dialog" title="Audit History" style="display:none">
			<div id="dialog_note"></div>
			<table id="records_table"  class="summarytable">
				<caption id="auditCaption"></caption>
				<thead style="background-color: white;">
					<th>English Val</th>
					<th>Spanish Val</th>
					<th>Time</th>
					<th>Updated By</th>
					<th>Source</th>
					<th>Action</th>
				</thead>
				<tbody id="records_table_body"/>
			</table>
	</div>
	
	<div id="dialogResult" title="Response Status" style="display:none">
			<div id="dialogResultBody"></div>
	</div>
	
	<div id="dialogConfirm" title="Confirm" style="display:none">
			<div id="dialogConfirmBody"></div>
	</div>
	
	<div id="popup-dialog-publish" style="display:none;width:800px;">
		    <div id="publish_note"></div>
		    <table align="center" width="100%" class="summarytable">
		    	<caption>
		    		<button style="float:left" disabled id="compareEngButton" onclick="viewFileDiff('ENGLISH_FILE')">Compare English</button>
		    		<button style="float:left" disabled id="compareSpaButton"  onclick="viewFileDiff('SPANISH_FILE')">Compare Spanish</button>  
		    		Publish Summary</caption>
		    	<thead >
		    		<th width="10%">Version</th>
		    		<th width="20%">Time</th>
		    		<th width="15%">English File</th>
		    		<th width="15%">Spanish File</th>
		    		<th width="15%" >Published By</th>
		    		<th width="25%" >Publish Comment</th>
		    	</thead>
			    <tbody id="publishTable"/>
		  	</table>
		</div>
	
		<div id="popup-dialog-diff" style="display:none;">
			<button class="ui-corner-all  ui-button ui-widget ui-state-default ui-button-text-icon-primary dummyScrollNextDiff" onclick="scrollToNextDiff();">
				<span class="ui-button-icon-primary ui-icon ui-icon-search"></span>
		 		<span class="ui-button-text">Scroll Next</span>					
			</button>
			<button class="ui-corner-all  ui-button ui-widget ui-state-default ui-button-text-icon-primary dummyScrollNextDiff" onclick="scrollToPrevDiff();">
				<span class="ui-button-icon-primary ui-icon ui-icon-search"></span>
		 		<span class="ui-button-text">Scroll Previous</span>					
			</button>
			<div class="clear" style="padding-top: 10px;"></div>
			<div id="compare" ></div>
		</div>
	
</body>

<style>
	.nav ul {
  list-style: none;
  /* Permalink - use to edit and share this gradient: http://colorzilla.com/gradient-editor/#b5bdc8+0,828c95+36,28343b+100;Grey+Black+3D */
	background: #b5bdc8; /* Old browsers */
	background: -moz-linear-gradient(top, #b5bdc8 0%, #828c95 36%, #28343b 100%); /* FF3.6-15 */
	background: -webkit-linear-gradient(top, #b5bdc8 0%,#828c95 36%,#28343b 100%); /* Chrome10-25,Safari5.1-6 */
	background: linear-gradient(to bottom, #b5bdc8 0%,#828c95 36%,#28343b 100%); /* W3C, IE10+, FF16+, Chrome26+, Opera12+, Safari7+ */
	filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#b5bdc8', endColorstr='#28343b',GradientType=0 ); /* IE6-9 */
  text-align: center;
  padding: 0;
  margin: 0;
}
.nav li {
  font-family: 'Oswald', sans-serif;
  font-size: 1.2em;
  line-height: 40px;
  height: 30px;
  border-bottom: 1px solid #888;
}
 
.nav a {
  text-decoration: none;
  color: #fff;
  display: block;
  transition: .3s background-color;
}
 
.nav a:hover {
  /* Permalink - use to edit and share this gradient: http://colorzilla.com/gradient-editor/#7d7e7d+0,0e0e0e+100;Black+3D */
	background: #7d7e7d; /* Old browsers */
	background: -moz-linear-gradient(top, #7d7e7d 0%, #0e0e0e 100%); /* FF3.6-15 */
	background: -webkit-linear-gradient(top, #7d7e7d 0%,#0e0e0e 100%); /* Chrome10-25,Safari5.1-6 */
	background: linear-gradient(to bottom, #7d7e7d 0%,#0e0e0e 100%); /* W3C, IE10+, FF16+, Chrome26+, Opera12+, Safari7+ */
	filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#7d7e7d', endColorstr='#0e0e0e',GradientType=0 ); /* IE6-9 */
}
 
.nav a.active {
  background-color: #fff;
  color: #444;
  cursor: default;
}

.nav span {
  color: white;
  font-size: 1.1em;
  font-weight: bold;
}
 
@media screen and (min-width: 600px) {
  .nav li {
    width: 120px;
    border-bottom: none;
    height: 40px;
    line-height: 40px;
    font-size: .9em;
    font-weight: bold;
  }
  .nav li {
    float: left;
  }
  .nav ul {
    overflow: auto;
    width: 100%;
    margin: 0 auto;
  }
  .nav {
    background-color: #444;
  }
  
}

</style>
</html>