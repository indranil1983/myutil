<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<div style="position: absolute;">	
		<div class="dropdownSucessText">Please select the features you want to merge with production copy of Appresource properties. If you just want the 
			production copy , do not select anything in the dropdown and click generate properties button.
		</div>	
		<div class="dropdownErrorText">
			You do not have any modification in Appresource properties loaded in development. If you choose to download you will get the production copy
		</div>	
		<div class="clear" style="height: 15px;"></div>		
		
		<div>
            <button onclick="commonUtil.showProdFileList();" style="" class="ui-corner-all  ui-button ui-widget ui-state-default ui-button-text-icon-primary">
				<span class="ui-button-icon-primary ui-icon ui-icon-circle-arrow-s"></span>
				<span class="ui-button-text">Download Production Copies</span>
			</button>
        </div>
        <br>
		
		<div id="dummyFeatureSelectBoxDiv">
			<select class="chosen-select dummyfeatureSelectBox" id="featureSelectBox" multiple style="width:500px;">						  
			</select>
		</div>
			
		<div class="clear" style="height: 15px;"></div>		
		
		<div id="dummyCustomPropDisplay" 
			style="width:700px;border:1px solid black; padding: 5px;height: 150px;">
			<div class="clear" style="height: 15px;"></div>		
			<div style="text-align: center;font-weight: bold">Custom Properties</div>
			<div class="clear" style="height: 15px;"></div>		
			
		</div>		
		<div class="clear" style="height: 15px;"></div>		
		<div>
			<button onclick="downloadDevFile('english');" style="" class="ui-corner-all  ui-button ui-widget ui-state-default ui-button-text-icon-primary">
				<span class="ui-button-icon-primary ui-icon ui-icon-circle-arrow-s"></span>
				<span class="ui-button-text">Download English</span>
			</button>
			<button onclick="downloadDevFile('spanish');" style="" class="ui-corner-all  ui-button ui-widget ui-state-default ui-button-text-icon-primary">
				<span class="ui-button-icon-primary ui-icon ui-icon-circle-arrow-s"></span>
				<span class="ui-button-text">Download Spanish</span>
			</button>
			<button onclick="downloadDevFile('zip');" style="" class="ui-corner-all  ui-button ui-widget ui-state-default ui-button-text-icon-primary">
				<span class="ui-button-icon-primary ui-icon ui-icon-document"></span>
				<span class="ui-button-text">Download zip</span>
			</button>
		</div>			
	</div>
	<div class="clear" style="height: 45px;"></div>	
	
	
	<script>
		

	</script>
	
</html>