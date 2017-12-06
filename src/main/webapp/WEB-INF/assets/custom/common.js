function cleanDate(d) {
	d = String(d);
	var date = new Date(+d.replace(/\/Date\((\d+)\)\//, '$1'));
	date = date.toString().slice(3, 25);
	return date;
}

var fileDownloadObj={
	downloadProperties:function(data,action)
	{
		$.fileDownload(action, {
	        preparingMessageHtml: "We are preparing your report, please wait...",
	        failMessageHtml: "There was a problem generating your report, please try again.",
	        httpMethod: "POST",
	        data: data,
	        headers: { 
	            'Accept': 'application/json',
	            'Content-Type': 'application/json' 
	        },
	        dataType: "json"
	    });
	    
	}
		
}

var commonUtil={
	showAudit:function(hrefElem)
	{
		var key = $(hrefElem).html();
		var obj={};
		obj.key=key;
		$.ajax({
			dataType: "json",
			type: "POST",
			async: true,
			beforeSend: function (jqXHR, settings) {
				try{prodPageEditor.grid.showLoading();}
				catch(e){}
			},
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json' 
			},
			url: "rest/rsrc/audit",                                           
			data: JSON.stringify(obj),
			success: function (ebiResponse) {
				var data = ebiResponse.alListOfMaps;
				$("#auditCaption").html('History for '+key);
				$('#records_table_body').empty();
				if(data.length==0)
				{
					$( "#dialog_note" ).html("No history found");
					$('#records_table').hide();
				}
				else
				{
					$( "#dialog_note" ).empty();
					$('#auditHeading').show();
					for(i=0;i<data.length;i++)
					{
						var dataObj = data[i];
						var dataPrevObj = data[i+1];
						var englishVal = null;
						if(dataPrevObj!=null || dataPrevObj!=undefined)
						{
							englishVal=highlightDiff(dataPrevObj.ENGLISH_VAL,dataObj.ENGLISH_VAL);
						}
						else
						{
							englishVal = "<xmp>"+dataObj.ENGLISH_VAL+"</xmp>";
						}
						
						var spanishVal = null;
						if(dataPrevObj!=null || dataPrevObj!=undefined)
						{
							spanishVal=highlightDiff(dataPrevObj.SPANISH_VAL,dataObj.SPANISH_VAL);
						}
						else
						{
							spanishVal = dataObj.SPANISH_VAL;
						}
						
						var date = cleanDate(dataObj.UPDATED_TIME);
						var rowString = "<tr><td>"+englishVal +
											"</td><td>"+spanishVal+
											"</td><td> "+date+
											"</td><td>"+dataObj.CHANGED_BY+
											"</td><td>"+dataObj.SOURCE+
											"</td><td>"+dataObj.ACTION+
										"</td></tr>"
						$('#records_table_body').append(rowString);
						$('#records_table').show();
					}
				}
				$( "#dialog" ).dialog({
					height: "auto",
					width: "auto",
					modal: true,
					show: {
						effect: "blind",
						duration: 200
					},
					create: function( event, ui ) {
					    // Set maxWidth
					    $(this).css("maxWidth", "900px");
					    $(this).css("maxHeight", "400px");
					 },
					hide: {
						effect: "blind",
						duration: 200
					}
				});


			},
			complete: function () {
				prodPageEditor.grid.hideLoading();
			}
		});
	},
	showConfirmDialog:function(string,funcHandler)
	  {
		  var originalContent;
		  $('#dialogConfirmBody').empty();
			$('#dialogConfirmBody').append(string);
			$( "#dialogConfirm" ).dialog({
				height: "auto",
				width: "auto",
				modal: true,
				show: {
					effect: "blind",
					duration: 200
				},
				hide: {
					effect: "blind",
					duration: 200
				},
				buttons: {
	                Yes: function(){
	                	funcHandler();
	                	$(this).dialog("close");
	                },
	                Cancel: function () {
	                    $(this).dialog("close");
	                }
				}
			});	  
	  },
	  showProdFileList:function (evt, ui) 
	  {
    	  $.ajax({
    		  dataType: "json",
    		  type: "GET",
    		  async: true,
    		  beforeSend: function (jqXHR, settings) {
    			  attLaoderAnimation.showLoaderAnimation();
    		  },
    		  headers: { 
    			  'Accept': 'application/json',
    			  'Content-Type': 'application/json' 
    		  },
    		  url: "rest/rsrc/publishReport",
    		  success: function (ebiResponse) {
    			  //debugger;			      
    			  
    			  var data = ebiResponse.alListOfMaps;
    			  $('#publishTable').empty();
    			  if(data.length==0) 
    			  {
    				  $( "#publish_note" ).html('No Published Data found');
    			  }
    			  else{
    				  $( "#publish_note" ).empty();
    				  for(i=0;i<data.length;i++)
    				  {
    					  var dataObj = data[i];
    					  var date = cleanDate(dataObj.TIME);
    					  var englishUrl ="<a href='rest/rsrc/downloadPublishedReport?publishKey="+dataObj.PUBLISH_KEY+"&languageFile=ENGLISH_FILE' onclick='' title='Click to downlaod English file'>Download English</a>";
    					  var spanishUrl ="<a href='rest/rsrc/downloadPublishedReport?publishKey="+dataObj.PUBLISH_KEY+"&languageFile=SPANISH_FILE' title='Click to downlaod Spanish file'>Download Spanish</a>";
    					  var rowString = "<tr><td>"+(data.length-i)+"<input type='checkbox' class='publishDummyClass' data-val="+dataObj.PUBLISH_KEY+" onclick='validatePublishCheckBox()'>"+
    					  "</td><td>"+date+
    					  "</td><td>"+englishUrl+
    					  "</td><td>"+spanishUrl+
    					  "</td><td>"+dataObj.PUBLISHED_BY+
    					  "</td><td style='word-wrap: break-word;'>"+dataObj.PUBLISH_COMMENT+"</td></tr>"
    					  $('#publishTable').append(rowString);

    				  }
    			  }
    			  $( "#popup-dialog-publish" ).dialog({
    				  height: "auto",
    				  width: "auto",
    				  modal: true,
    				  show: {
    					  effect: "blind",
    					  duration: 200
    				  },
    				  hide: {
    					  effect: "blind",
    					  duration: 200
    				  }
    			  });


    		  },
    		  complete: function () {
    			  attLaoderAnimation.stopAnimation();
    		  },
    		  error:processResponseData.processError
    	  });
    	  },
    	  compareRallyArtifactByName(a,b){ //first feature will come then defect in reverse order.
    		  
    		  var nameA = a.FormattedID.toUpperCase(); // ignore upper and lowercase
    		  var nameB = b.FormattedID.toUpperCase(); // ignore upper and lowercase
    		  return nameB.localeCompare(nameA);
    	  }
}

var processResponseData=
{
	processSuccess:function(data)
	{
		if(data.successList.length>0)
		{
			var html="<div id='accordionAlert'>"+data.successList+"</div>";
			alert(html); 
			$( "#accordionAlert" ).accordion({
			      collapsible: true,
			      active :false
			 });
		}
	},	
	processError:function(xmlhttp)
	{
		var json = JSON.parse(xmlhttp.responseText);
		if(json.errorList.length>0)
		{
			alert(json.errorList);
		}
	}

}

window.alert = function(string) 
{
	$('#dialogResultBody').empty();
	$('#dialogResultBody').append(string);
	$( "#dialogResult" ).dialog({
		height: "auto",
		width: "auto",
		modal: true,
		show: {
			effect: "blind",
			duration: 200
		},
		hide: {
			effect: "blind",
			duration: 200
		},
		my: "center",
		   at: "center",
		   of: window
	});
  };
  

  
var rallyService={
	populateRallyFeatureDefectsInDropDown:function($dropDownId)
	{
		$.ajax({
			dataType: "json",
			type: "GET",
			async: true,
			beforeSend: function (jqXHR, settings) {
				
			},
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json' 
			},
			url: "rest/rsrc/rallyFeatures",
			success: function (ebiResponse) {
				var rallyResponse = ebiResponse.rallyResponse;
				rallyResponseJson = JSON.parse(rallyResponse);
				var result = rallyResponseJson.QueryResult.Results;
				for (var i = 0; i < result.length; i++) {
                	var featureId = result[i].FormattedID;
                	var featureName = result[i]._refObjectName;    
                	var div_data="<option value='"+featureId+"'>"+featureId+" - "+featureName+"</option>";
	             	$(div_data).appendTo($dropDownId);
	             	
            	}
				//$dropDownId.chosen();
				//$dropDownId.chosen('destroy'); 
				//$dropDownId.chosen();  
			},
			complete: function () {
				//prodPageEditor.grid.hideLoading();
			}
		});
		
		$.ajax({
			dataType: "json",
			type: "GET",
			async: true,
			beforeSend: function (jqXHR, settings) {
				
			},
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json' 
			},
			url: "rest/rsrc/rally/defects",
			success: function (ebiResponse) {
				var rallyResponse = ebiResponse.rallyResponse;
				rallyResponseJson = JSON.parse(rallyResponse);
				var result = rallyResponseJson.QueryResult.Results;
				for (var i = 0; i < result.length; i++) {
                	var defectId = result[i].FormattedID;
                	var defectName = result[i]._refObjectName;    
                	var div_data="<option value='"+defectId+"'>"+defectId+" - "+defectName+"</option>";
	             	$(div_data).appendTo($dropDownId);
	             	
            	}
				//$dropDownId.chosen();
				$dropDownId.chosen('destroy'); 
				$dropDownId.chosen();  
			},
			complete: function () {
				//prodPageEditor.grid.hideLoading();
			}
		});
		
	},
	populateRallyMileStoneInDropDown:function($dropDownId)
	{
		$.ajax({
			dataType: "json",
			type: "GET",
			async: true,
			beforeSend: function (jqXHR, settings) {
				
			},
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json' 
			},
			url: "rest/rsrc/rallyMilestones",
			success: function (ebiResponse) {
				var rallyResponse = ebiResponse.rallyResponse;
				rallyResponseJson = JSON.parse(rallyResponse);
				var result = rallyResponseJson.QueryResult.Results;
				for (var i = 0; i < result.length; i++) {
                	var mlStoneId = result[i].FormattedID;
                	var mlStoneName = result[i]._refObjectName;    
                	var refUrl = result[i]._ref;
                	var div_data="<option value='"+mlStoneName+" ("+mlStoneId+")||"+refUrl+"'>"+mlStoneName+" ("+mlStoneId+")</option>";
	             	$(div_data).appendTo($dropDownId);
	             	
            	}
				//$dropDownId.chosen();
				$dropDownId.chosen('destroy'); 
				$dropDownId.chosen({allow_single_deselect:true});  
			},
			complete: function () {
				//prodPageEditor.grid.hideLoading();
			}
		});
	}
		
		
}

var attLaoderAnimation={
		spinner:'',
		loadAnimation:function(id)
		{
			var opts = attLaoderAnimation.opts;
			var target = document.getElementById(id);
			attLaoderAnimation.spinner = new Spinner(opts).spin();				
			$('#'+id).append(attLaoderAnimation.spinner.el);
		},
		opts:{
		  lines: 15, // The number of lines to draw
		  length: 15, // The length of each line
		  width: 3, // The line thickness
		  radius: 8, // The radius of the inner circle
		  corners: 1, // Corner roundness (0..1)
		  rotate: 0, // The rotation offset
		  direction: 1, // 1: clockwise, -1: counterclockwise
		  color: '#000000', // #rgb or #rrggbb or array of colors
		  speed: 1.3, // Rounds per second
		  trail: 60, // Afterglow percentage
		  shadow: false, // Whether to render a shadow
		  hwaccel: false, // Whether to use hardware acceleration
		  className: 'spinner', // The CSS class to assign to the spinner
		  zIndex: 99999, // The z-index (defaults to 2000000000)
		  top: '0px', // Top position relative to parent in px
		  left: '0px' // Left position relative to parent in px
		},
		showLoaderAnimation:function(){
			$('#glasspane').show();
			attLaoderAnimation.loadAnimation('spinnerpane');
		},
		stopAnimation:function(){
			$('#glasspane').hide();
			attLaoderAnimation.spinner.stop();
		}
	};

function highlightDiff(oldText,newText){ 
        
	var wikEdDiff = new WikEdDiff();
	var diffHtml = wikEdDiff.diff( oldText, newText );
	return diffHtml;
}

function validatePublishCheckBox(){
	var checkedCounter=0;
	$('.publishDummyClass').each(function(){
		if($(this).attr('checked')) {
			checkedCounter++;
			if(checkedCounter>2)
			{
				alert('Please click any 2 checkboxes');
				$('#compareEngButton').prop("disabled",true);
				$('#compareSpaButton').prop("disabled",true);
			}
			else if(checkedCounter==2)
			{
				$('#compareEngButton').prop("disabled",false);
				$('#compareSpaButton').prop("disabled",false);
			}
			else
			{
				$('#compareEngButton').prop("disabled",true);
				$('#compareSpaButton').prop("disabled",true);
			}
		}
	});
}

function viewFileDiff(languageFile)
{	
	var publishedFiles=[];
	$('.publishDummyClass').each(function(){
		if($(this).attr('checked')) {
			publishedFiles.push($(this).attr('data-val'));			
		}
	});
	
	$.ajax({
		  dataType: "json",
		  type: "POST",
		  async: true,
		  beforeSend: function (jqXHR, settings) {
			  attLaoderAnimation.showLoaderAnimation();
		  },
		  complete: function(){
			  attLaoderAnimation.stopAnimation();
		  },
		  headers: { 
			  'Accept': 'application/json',
			  'Content-Type': 'application/json' 
		  },
		  url: "rest/rsrc/fileDiff",
		  data:JSON.stringify({"filePublishId":publishedFiles,"language":languageFile}),
		  success: function (ebiResponse){
			  var file= ebiResponse.fileStringDiff;
			  var newFile = file[0];
			  var oldFile = file[1];
			 // var diff = highlightDiff(oldFile,newFile);
			  
			  $('#compare').mergely({
					cmsettings: { readOnly: true, lineNumbers: true,lineWrapping:true},
					editor_height:'700px',
					editor_width:'400px',
					ignorews:true,
					viewport:false,
					
				});
			  $('#compare').mergely('lhs', oldFile);
			  $('#compare').mergely('rhs', newFile);
			  //alert($('#compare').html());
			  
			  $("#popup-dialog-diff").dialog({
				  height: "auto",
				  width: screen.width-50,
				  modal: true,
				  show: {
					  effect: "blind",
					  duration: 200
				  },
				  hide: {
					  effect: "blind",
					  duration: 200
				  }
			  });
			  $("#popup-dialog-diff").dialog("open");
		  },
		  error:function(){
			  
		  }		  
	});
	

}


function scrollToNextDiff()
{
	$('#compare').mergely('scrollToDiff', 'next');
}

function scrollToPrevDiff()
{
	$('#compare').mergely('scrollToDiff', 'prev');
}


$( function() {
    $( "#tabs" ).tabs();
  } );


$( function() {
    $( document ).tooltip({
      position: {
        my: "center bottom-20",
        at: "center top",
        using: function( position, feedback ) {
          $( this ).css( position );
          $( "<div>" )
            .addClass( "arrow" )
            .addClass( feedback.vertical )
            .addClass( feedback.horizontal )
            .appendTo( this );
        }
      }
    });
    
    $('.ui-button').hover(
    		function () {
    			$(this).addClass('ui-state-hover');
    		},
    		function () {
    			$(this).removeClass('ui-state-hover');
    		}
    );
    
  } );

