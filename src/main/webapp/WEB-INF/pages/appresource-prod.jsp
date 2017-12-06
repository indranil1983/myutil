<!DOCTYPE html>
<html>

<body>
		<div class="my-progress-bar"></div>
		
		
		<div id="popup-dialog-crud" style="display:none;">
			<form id="crud-form">
			    <table align="center">
			    <tbody><tr>
			    <td class="label">Key:</td>
			    <td><input type="text" name="key" style="width: 90%;"></td>
			    </tr>
			    <tr>
			    <td class="label">English Value:</td>
			    <td><textarea name="engVal" rows="3" cols="40" style="width: 90%;"></textarea></td>
			    </tr>
			    <tr>
			    <td class="label">Spanish Value:</td>
			    <td><textarea name="spanishVal" rows="3" cols="43" style="width: 90%;"></textarea></td>
			    </tr>
			    <tr>
			    <td class="label">Source:</td>
			    <td><input type="text" name="source" value="PROD" readonly style="width: 90%;"></td>
			    </tr>
			    </tbody></table>
			</form>
		</div>
		
		
		
		<div id="popup-dialog-tagtoFile" style="display:none;">
			<div class="clear" style="padding-top: 10px;"></div>
			<div class="">
		        <textarea required class="form-control" id="tagToFileMessageId" placeholder="Type in your message" maxlength="150" style="width:100%;" rows="5"></textarea>
		        <h6 class="pull-right" id="count_message"></h6>
			</div>
		</div>

		<div align="center"><b> Production/Operation</b> </div>
		<div style="height:20px;"></div>
		<div id="tabs">
		  <ul>
		    <li><a href="#tabs-1">Appresource Production</a></li>
		    <li><a href="#tabs-2" onclick="">Reconcile Files</a></li>
		  </ul>
		  <div id="tabs-1">
				<div id="grid_array"></div>
				
				<div style="height:10px;"></div>	
				<div style="height:1px;background: black" ></div>
				<div style="height:10px;"></div>
				<div align="center"><b> Promote from Lab/Dev to Production</b> </div>
				<div style="height:20px;"></div>
				<div class="clear" style="padding-top: 10px;"></div>
				<div>
					<div style="float:left">
						<select class="chosen-select dummyfeatureSelectBox" id="featureSelectBox" multiple style="width:500px;">
						</select>				
					</div>
					<div style="float:left;margin-left:20px;">
						<button class="ui-corner-all  ui-button ui-widget ui-state-default ui-button-text-icon-primary" onclick="stagingTableObject.showStagingGrid()">
							<span class="ui-button-icon-primary ui-icon ui-icon-search"></span>
					 		<span class="ui-button-text">Search</span>					
						</button>
					</div>
				</div>
				<div class="clear" style="height: 50px;"></div>
				<div id="grid_staging"></div>
				<div class="clear" style="height: 100px;"></div>
			</div>
			<div id="tabs-2" style="min-height: 300px;">
				<%@include file="prod-reconcile.jsp"%>	
			</div>
		</div>
</body>

  <script type="text/javascript" src="assets\custom/prodPage.js"></script>

</html>