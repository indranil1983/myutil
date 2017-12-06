<style>
	.chosen-container {
		font-size: 13px;
	}
	
	.chosen-container-single .chosen-single{
		height: 35px;
		line-height: 1.4;
		padding-top:6px;
	}
	
	.chosen-container-single .chosen-single div{
		top:3px;
	}
	
	
</style>

<div id="popup-dialog-mlstone" style="display:none;">
			<div class="clear" style="padding-top: 10px;"></div>
			<div class="">
		        <textarea required class="form-control" id="currMlStoneMessageId" placeholder="Please provide a message before marking the current milestone." maxlength="150" style="width:100%;" rows="5"></textarea>
		        <h6 class="pull-right" id="count_message"></h6>
			</div>
</div>

		<div id="currentFeatureSelectBoxDiv">
			<span id='currentFeatureSelectBoxDiv'/>	
		</div>`

		<div id="dummyFeatureSelectBoxDiv">
			<select class="chosen-select dummyMilestoneSelectBox" id="milestoneSelectBox"  onchange="releaseMlStoneTracker.fetchArtifacts();" style="width:500px;">						  
				<option disabled selected value> -- Select a Milestone -- </option>
			</select>
			<button onclick="releaseMlStoneTracker.updateCurrentMlStoneButtonClk();" type="button" class="ui-corner-all  ui-button ui-widget ui-state-default ui-button-text-icon-primary" role="button">
				<span class="ui-button-icon-primary ui-icon ui-icon-circle-triangle-e"></span>
				<span class="ui-button-text">Update Current Milestone</span>
			</button>
			
		</div>`
		
		<br style="clear:both;"/>
		<table cellspacing='0' id="artifactTable" class="simpleTable" style="display:none"> <!-- cellspacing='0' is important, must stay -->
		
			<!-- Table Header -->
			<thead>
				<tr>
					<th>#</th>
					<th>Artifact Id</th>
					<th>Description</th>
				</tr>
			</thead>
			<!-- Table Header -->
			<!-- Table Body -->
			<tbody id="simpleTableBody"/>
			<!-- Table Body -->
		</table>
<style>
.simpleTable a:link {
	color: #666;
	font-weight: bold;
	text-decoration:none;
}
.simpleTable a:visited {
	color: #999999;
	font-weight:bold;
	text-decoration:none;
}
.simpleTable a:active,
.simpleTable a:hover {
	color: #bd5a35;
	text-decoration:underline;
}
.simpleTable {
	font-family:Arial, Helvetica, sans-serif;
	color:#666;
	font-size:12px;
	text-shadow: 1px 1px 0px #fff;
	background:#eaebec;
	border:#ccc 1px solid;
	-moz-border-radius:3px;
	-webkit-border-radius:3px;
	border-radius:3px;
	-moz-box-shadow: 0 1px 2px #d1d1d1;
	-webkit-box-shadow: 0 1px 2px #d1d1d1;
	box-shadow: 0 1px 2px #d1d1d1;
}
.simpleTable th {
	padding:21px 25px 22px 25px;
	border-top:1px solid #fafafa;
	border-bottom:1px solid #e0e0e0;

	background: #ededed;
	background: -webkit-gradient(linear, left top, left bottom, from(#ededed), to(#ebebeb));
	background: -moz-linear-gradient(top,  #ededed,  #ebebeb);
}
.simpleTable th:first-child {
	text-align: left;
	padding-left:20px;
}
.simpleTable tr:first-child th:first-child {
	-moz-border-radius-topleft:3px;
	-webkit-border-top-left-radius:3px;
	border-top-left-radius:3px;
}
.simpleTable tr:first-child th:last-child {
	-moz-border-radius-topright:3px;
	-webkit-border-top-right-radius:3px;
	border-top-right-radius:3px;
}
.simpleTable tr {
	text-align: center;
	padding-left:20px;
}
.simpleTable td:first-child {
	text-align: left;
	padding-left:20px;
	border-left: 0;
}
.simpleTable td {
	padding:18px;
	border-top: 1px solid #ffffff;
	border-bottom:1px solid #e0e0e0;
	border-left: 1px solid #e0e0e0;

	background: #fafafa;
	background: -webkit-gradient(linear, left top, left bottom, from(#fbfbfb), to(#fafafa));
	background: -moz-linear-gradient(top,  #fbfbfb,  #fafafa);
}
.simpleTable tr.even td {
	background: #f6f6f6;
	background: -webkit-gradient(linear, left top, left bottom, from(#f8f8f8), to(#f6f6f6));
	background: -moz-linear-gradient(top,  #f8f8f8,  #f6f6f6);
}
.simpleTable tr:last-child td {
	border-bottom:0;
}
.simpleTable tr:last-child td:first-child {
	-moz-border-radius-bottomleft:3px;
	-webkit-border-bottom-left-radius:3px;
	border-bottom-left-radius:3px;
}
.simpleTable tr:last-child td:last-child {
	-moz-border-radius-bottomright:3px;
	-webkit-border-bottom-right-radius:3px;
	border-bottom-right-radius:3px;
}
.simpleTable tr:hover td {
	background: #f2f2f2;
	background: -webkit-gradient(linear, left top, left bottom, from(#f2f2f2), to(#f0f0f0));
	background: -moz-linear-gradient(top,  #f2f2f2,  #f0f0f0);	
}
	
</style>

<script>

	
</script>