<!DOCTYPE html>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<head>

<meta charset="ISO-8859-1">
<title>AppResource Lab/Dev</title>


<style type="text/css">
	.customLabButton{
		background:#f6a828 ;
	}
</style>

</head>
<body>
<script type="text/javascript">

  </script>
  		
		<div align="center"><b> Lab/Dev</b> </div>
		<div style="height:20px;"></div>
		<div id="tabs">
		  <ul>
		    <li><a href="#tabs-1">Appresource Lab/Dev</a></li>
		    <li><a href="#tabs-2" onclick="propertyDownLoadObject.loadFeaturesChangedFromDB();">Generate Properties</a></li>
		     <sec:authorize access="hasAuthority('EBI_SA')">
		     	<li><a href="#tabs-3" onclick="releaseMlStoneTracker.displayReleaseMlStoneTracker();">Release Milestone</a></li>
		     </sec:authorize>
		  </ul>
		  <div id="tabs-1">
				<div id="grid_array"></div>
				<div style="height:20px;"></div>
				
				<div>
					<div style="float:left">
						<select class="chosen-select dummyEditorFeature" multiple style="width:500px;">						  
						</select>
					</div>
					<div style="float:left;margin-left:20px;">
						<button style="width:100px" class="ui-corner-all  ui-button ui-widget ui-state-default ui-button-text-icon-primary"  onclick="loadDevData()">Submit</button>
					</div>
					<input type="hidden" id="workEditorFeaturesId" value="false"/>
				</div>
				<div class="clear" style="height: 50px;"></div>
				<div id="grid_staging"></div>
				
				<div class="clear" style="height: 100px;"></div>
			</div>
			<div id="tabs-2" style="min-height: 300px;">
				<%@include file="dev-propertygenerate-tab.jsp"%>	
			</div>
			<div id="tabs-3" style="min-height: 300px;">
				<%@include file="dev-milestone-tab.jsp"%>	
			</div>
		
		  </div>
</body>

<script>

$( function() {
    $( "#tabs" ).tabs();
  } );

/* $(document).on("submit", "form.fileDownloadForm", function (e) {
	var selectedVal =$(".dummyfeatureSelectBox").chosen().val();	
	if(selectedVal!=null)
	{
		selectedVal = selectedVal.toString();
	}	
	fileDownloadObj.downloadProperties(selectedVal, $(this).prop('action'));
	 //otherwise a normal form submit would occur
	e.preventDefault();
}); */

var $prodGrid = null;
var prodGrid =null;
var isProdEditableFlag = false;
var prodSelectionModel = { type: 'none', subtype:'incr', cbHeader:true, cbAll:true};

var prodColumns = [
				{ title: "", dataIndx: "state", width: "2%", minWidth:30, align: "center", type:'checkBoxSelection', cls: 'ui-state-default', resizable: false, sortable:false },
		        { title: "Key", dataType: "string", dataIndx: "KEY", width: '20', editable: false,
		            filter: { type: 'textbox', condition: 'contain', listeners: ['keyup'],cls :'filterClass',attr: 'placeholder="Enter your keyword"' },
		            editor: { type: "textarea", attr: "rows=2" },
	                editModel: { keyUpDown: false, saveKey: '' },
	                validations: [{ type: 'minLen', value: 1, msg: "Required"}],
	                render: function (ui) {
             		   var rowData = ui.rowData;
             		   return "<a href='javascript:void(0)' onclick='commonUtil.showAudit(this)' >"+rowData.KEY+"</a>";
             	   }
			     },
		        { title: "ENGLISH_VAL", dataType: "string", dataIndx: "ENGLISH_VAL", width: '35' ,editable:isProdEditableFlag,
			            filter: { type: 'textbox', condition: 'contain', listeners: ['keyup'],cls :'filterClass',attr: 'placeholder="Enter your keyword"' },
			            editor: { type: "textarea", attr: "rows=5" },
		                editModel: { keyUpDown: false, saveKey: '' }
			     },
		        { title: "SPANISH_VAL", dataType: "string", align: "right", dataIndx: "SPANISH_VAL",width: '35',editable:isProdEditableFlag,
			            filter: { type: 'textbox', condition: 'contain', listeners: ['keyup'],cls :'filterClass',attr: 'placeholder="Enter your keyword"' },
			            editor: { type: "textarea", attr: "rows=5" },
		                editModel: { keyUpDown: false, saveKey: '' }
			     }
		    ];






$(function(){
     showStagingGrid(null);  	 
 });


</script>


   <script type="text/javascript" src="assets\custom/devedittable.js" ></script> 
 
</html>