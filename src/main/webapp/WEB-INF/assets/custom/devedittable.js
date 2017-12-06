
if (!String.prototype.format) {
  String.prototype.format = function() {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function(match, number) { 
      return typeof args[number] != 'undefined'
        ? args[number]
        : match
      ;
    });
  };
}

var dev_viewProductionTable={
		$gridStaging:null,
		gridStaging:null,
		storeOnGoingModification:[],	
		modificationPush:function(obj)
		{
			if(obj!=undefined && obj!=null){
				dev_viewProductionTable.storeOnGoingModification.push(obj);				
			}
		},
		prodToolBar :{
			    items: [			            
			            { type: 'button', icon: 'ui-icon-disk', label: 'Edit', style: 'margin:0px 5px;', listeners: [
			                { "click": function (evt, ui) {
			                	selarray = $grid.pqGrid('selection', { type: 'row', method: 'getSelection' });
			                	var editorEnabled = $('#workEditorFeaturesId').val();
			                	if('true'==editorEnabled)
			                	{
			                		for (var i = 0; i < selarray.length; i++) {
					                	var rowData = selarray[i].rowData;
					                	rowData.ACTION='MODIFIED';
					                	rowData.CHANGE='local';
					                	rowData.SOURCE='';
					                	dev_viewProductionTable.modificationPush(rowData);
				                		var rowIndx = dev_viewProductionTable.$gridStaging.pqGrid("addRow", { rowData: selarray[i].rowData });
				                		dev_viewProductionTable.$gridStaging.pqGrid("goToPage", { rowIndx: rowIndx });
				                		dev_viewProductionTable.$gridStaging.pqGrid("editFirstCellInRow", { rowIndx: rowIndx }); 
				                	}
				                	$grid.pqGrid( "selection", 
				                		    { type:'row', method:'removeAll' } 
				                		);
			                	}
			                	else
			                	{
				                	alert("Please enable Work Editor by selecting features you want to work on.");
			                	}
			                	
			                }
			                }
			            ]
			            },
			            { type: 'button', icon: 'ui-icon-cancel', label: 'Delete', listeners: [
			                { "click": function (evt, ui) {
								selarray = $grid.pqGrid('selection', { type: 'row', method: 'getSelection' });	  
								var editorEnabled = $('#workEditorFeaturesId').val();
			                	if('true'==editorEnabled)
			                	{
				                	for (var i = 0; i < selarray.length; i++) {
					                	var rowData = selarray[i].rowData;
					                	rowData.ACTION='DELETED';
					                	rowData.CHANGE='local';
					                	rowData.SOURCE='';
				                		var rowIndx = dev_viewProductionTable.$gridStaging.pqGrid("addRow", { rowData: selarray[i].rowData });
				                		
				                	}
				                	$grid.pqGrid( "selection", 
				                		    { type:'row', method:'removeAll' } 
				                		);
			                	}
			                	else
			                	{
				                	alert("Please enable Work Editor by selecting features you want to work on.");
			                	}
			                }
			                }
			            ]
			            }
			        ]
			    },
		calculateChanges:function()
		{
			 var isDirty = dev_viewProductionTable.gridStaging.isDirty();
			 var addListModified =null;
			    if (isDirty) {
			        //validate the new added rows.     
			    	var changeList = dev_viewProductionTable.gridStaging.getChanges({format: 'raw'});
			        var addList = changeList.addList;
			        var updateList = changeList.updateList;
			        var data = dev_viewProductionTable.$gridStaging.pqGrid( "getData", { dataIndx: ['KEY', 'SOURCE'] } );
			        addListModified =[];
			        for (var i = 0; i < addList.length; i++) {
			            var rowData = addList[i].rowData;
			            var isValid = dev_viewProductionTable.gridStaging.isValid({ "rowData": rowData }).valid;
			            if (!isValid) {
			                return;
			            }
			            if(!validateDuplicateRowInStaging(data, rowData))
			        	{
			        		alert('Duplcate data exists for Key '+rowData.KEY +" and SOURCE=" +rowData.SOURCE);
			        		return;
			        	}
			            var obj = {
							"key":rowData.KEY,
							"englishval":rowData.ENGLISH_VAL,
							"spanishval":rowData.SPANISH_VAL,
							"source":rowData.SOURCE,	
							"action":rowData.ACTION,	
			                }
			            addListModified.push(obj);
			        }
			        
			        for (var i = 0; i < updateList.length; i++) {
			            var rowData = updateList[i].rowData;
			            var oldRow = updateList[i].oldRow;
			            if(rowData.CHANGE!='local')
		            	{
			            	var isValid = dev_viewProductionTable.gridStaging.isValid({ "rowData": rowData }).valid;
				            if (!isValid) {
				                return;
				            }
				            if(!validateDuplicateRowInStaging(data, rowData))
				        	{
				        		alert('Duplcate data exists for Key '+rowData.KEY +" and SOURCE=" +rowData.SOURCE);
				        		return;
				        	}
				            var obj = {
								"key":rowData.KEY,
								"englishval":rowData.ENGLISH_VAL,
								"spanishval":rowData.SPANISH_VAL,
								"source":rowData.SOURCE,	
								"action":"RE_MODFIED",	
								"old_action":rowData.ACTION,
								"old_source":oldRow.SOURCE
				                }
				            addListModified.push(obj);		
		            	}
			            	        	
			        }
			    }
			    return addListModified;
		},
		acceptChanges:function() {
			//attempt to save editing cell.
			//debugger;
			if (dev_viewProductionTable.gridStaging.saveEditCell() === false) {
				return false;
			}
			var modifiedList = dev_viewProductionTable.calculateChanges();
			if(modifiedList!=null && modifiedList.length>0)
			{
				$('#summaryTable').empty();
				$('#summary_note').html("");
				for (var i = 0; i < modifiedList.length; i++) {
					var rowSummary = "<tr><td>"+modifiedList[i].action+"</td><td>"+modifiedList[i].key+"</td><td>"+modifiedList[i].englishval+"</td><td>"+modifiedList[i].spanishval+"</td></tr>";
					$('#summaryTable').append(rowSummary);
				}

				$("#popup-dialog-summary").dialog({ width: '900px', modal: true,autoOpen: false,title: "Accept Changes", buttons: {
					Add: function () {
						//post changes to server 
						$that=$(this);
						$.ajax({
							dataType: "json",
							type: "POST",
							async: true,
							beforeSend: function (jqXHR, settings) {
								dev_viewProductionTable.gridStaging.showLoading();
								$that.dialog("close");
							},
							headers: { 
								'Accept': 'application/json',
								'Content-Type': 'application/json' 
							},
							url: "rest/rsrc/updateDevRsrc",                                           
							data: JSON.stringify(modifiedList),
							success: function (ebiResponse) {
								//debugger;
								processResponseData.processSuccess(ebiResponse);
								dev_viewProductionTable.$gridStaging.pqGrid("destroy");
								loadDevData(true);

							},
							error:processResponseData.processError,
							complete: function () {
								dev_viewProductionTable.gridStaging.hideLoading();
							}
						});
					},
					Cancel: function () {
						$(this).dialog("close");
					}
				}
				});
			}
			else
			{
				$('#summaryTable').empty();
				$('#summary_note').html("No Changes detected");
				$("#popup-dialog-summary").dialog({ width: '900px', modal: true,autoOpen: false,title: "Accept Changes",
					buttons: {Cancel: function () {
						$(this).dialog("close");
					}
				  }
				});
			}
			$("#popup-dialog-summary").dialog("open");
		},
		getAllProdVal:function()
		{
			$.ajax({
				   url: 'rest/rsrc/all',
				   data: {
				      format: 'json'
				   },
				   error: processResponseData.processError,
				   dataType: 'json',
				   success: function(data) {
					   loadGridData(data.alListOfMaps);
					   processResponseData.processSuccess(data);
				   },
				   type: 'GET'
				});
		}
		
}


function downloadDevFile(type)
{
	var selectedVal ='';
	if($(".dummyfeatureSelectBox").chosen().val()!=null 
				&& $(".dummyfeatureSelectBox").chosen().val()!='' && !$("#checkbox-prod").is(':checked'))
	{
		selectedVal=$(".dummyfeatureSelectBox").chosen().val();	
		selectedVal = selectedVal.toString();
	}	
	if("english"==type) type="&type=english";
	else if("spanish"==type) type="&type=spanish";
	else type="&type=zip";
	var keyValPairs ="";
	$('.customKeyDummyClass').each(function( index, element ){
		var key = $(this).html();
		var value = $(".customValueDummyClass").get(index).value;
		keyValPairs=keyValPairs + "||"+key+"||"+value;
	});
	keyValPairs="&keys="+encodeURI(keyValPairs);
	
	location.href="rest/rsrc/generate?filters="+selectedVal+type+keyValPairs;

}


function validateDuplicateRowInStaging(gridData,rowData)
{
	var count = 0;
	for (var int = 0; int < gridData.length; int++) {
		if(rowData.KEY==gridData[int].KEY && rowData.KEY==gridData[int].SOURCE)
		{
			
			count++;
			alert("match count="+count);
			if(count>1) return false;
		}
			
	}
	return true;

}


function loadDevData(ignoreDirty)
{
	 var isDirty = dev_viewProductionTable.gridStaging.isDirty();
	 var ans=null;
	 if(ignoreDirty==undefined || ignoreDirty==null)
	 {
		 ignoreDirty=false;
	 }
	 if(!ignoreDirty && isDirty)
	 {
		 ans= window.confirm("You have uncomiitted changes. This will reload the table and scrap all your changes. Continue ?");
	 }
	 if(ans!=null && !ans) return false;
	var selectedVal =$(".chosen-select").chosen().val();	
	if(selectedVal!=null && ''!=selectedVal)
	{
		$('#workEditorFeaturesId').val('true');
	}	
		
	$.ajax({
        dataType: "json",
        type: "POST",
        async: true,
        beforeSend: function (jqXHR, settings) {
           
        },
        headers: { 
            'Accept': 'application/json',
            'Content-Type': 'application/json' 
        },
        url: "rest/rsrc/filter",                                           
        data: JSON.stringify(selectedVal),
        success: function (ebiResponse) {
            //debugger;
            try{$("#grid_staging").pqGrid("destroy");}
            catch(e){}
        	showStagingGrid(ebiResponse.alListOfMaps);

        },
        error: processResponseData.processError,
        complete: function () {
            dev_viewProductionTable.gridStaging.hideLoading();
        }
    });

}

function generateAppresrource()
{
	
	//e.preventDefault(); //otherwise a normal form submit would occur
    $.fileDownload($(this).prop('action'), {
        preparingMessageHtml: "We are preparing your report, please wait...",
        failMessageHtml: "There was a problem generating your report, please try again.",
        httpMethod: "POST",
        data: JSON.stringify($(".dummyfeatureSelectBox").chosen().val())
    });

}

function showStagingGrid(data)
{
	if(data==null) data = {};
	var textAreaValidation = { type: 'maxLen', value: 5950, msg: "Max chars allowed =5950"};
	var actionList = [
	             "NEW",
	             "MODIFIED",
	             "DELETED"
	           ];

	var sourceList = $(".chosen-select").chosen().val();

	var autoCompleteEditor = function (ui) {
        var $inp = ui.$cell.find("input");

        //initialize the editor
        $inp.autocomplete({
            source: (ui.dataIndx == "ACTION" ? actionList : sourceList),
            selectItem: { on: true }, //custom option
            highlightText: { on: true }, //custom option
            minLength: 0
        }).focus(function () {
            //open the autocomplete upon focus                
            $(this).autocomplete("search", "");
        });
    }
	
	var obj = {
	        width: "99%",
	        height: 400,
	        //columnBorders: false,
	        resizable: true,
	        title: "Work Editor",
	        showBottom: false,
	        scrollModel: { autoFit: true },
	        track: true,
	        filterModel: { on: true, mode: "AND", header: true },
            pageModel: { type: "local", rPP: 10, strRpp: "{0}", strDisplay: "{0} to {1} of {2}" },
            toolbar: 
            {
                items: [
                    { type: 'button', icon: 'ui-icon-plus', label: 'New Key', listeners: [
                        { "click": function (evt, ui) {
                            //append empty row at the end.    
                            var rowData = {ACTION:'NEW',CHANGE:'local'}; //empty row
                            var rowIndx = dev_viewProductionTable.$gridStaging.pqGrid("addRow", { rowData: rowData });
                            dev_viewProductionTable.$gridStaging.pqGrid("goToPage", { rowIndx: rowIndx });
                            dev_viewProductionTable.$gridStaging.pqGrid("editFirstCellInRow", { rowIndx: rowIndx });
                        }
                        }
                    ]
                    },
                    { type: 'button', icon: 'ui-icon-disk', label: 'Accept Changes', style: 'margin:0px 5px;', listeners: [
                        { "click": function (evt, ui) {
                        	dev_viewProductionTable.acceptChanges();
                        }
                        }
                    ]
                    },
                    { type: 'button', icon: 'ui-icon-cancel', label: 'Reject Changes', listeners: [
                        { "click": function (evt, ui) {
                            dev_viewProductionTable.$gridStaging.pqGrid("rollback");
                        }
                        }
                    ]
                    },
                    { type: 'button', icon: 'ui-icon-cancel', label: 'Export to Excel', listeners: [
                       { "click": function (evt, ui) {
                    	   dev_viewProductionTable.$gridStaging.pqGrid("exportExcel", { url: "exportToexcel", sheetName: "Appresource Dev Sheet" });
                       }
                       }
                   ]
                   }
                ]
            },
            quitEditMode: function (evt, ui) {                
                if (evt.keyCode != $.ui.keyCode.ESCAPE) {
                    dev_viewProductionTable.$gridStaging.pqGrid("saveEditCell");
                }
            },
            editModel: {
                saveKey: $.ui.keyCode.ENTER
            }
	    };
	    
	    obj.columnTemplate = { minWidth: '10%', maxWidth: '80%' };
	    obj.colModel = [
	        { title: "Key", dataType: "string", dataIndx: "KEY", width: '20',
	            filter: { type: 'textbox', condition: 'contain',cls :"filterClass", listeners: ['keyup'],attr: 'placeholder="Enter your keyword"' },
	            editor: { type: "textarea", attr: "rows=2" },
                editModel: { keyUpDown: false, saveKey: '' },
                validations: [{ type: 'minLen', value: 1, msg: "Required"},
                              { type: 'maxLen', value: 99, msg: "Max chars allowed =99"}],
                editable: function (ui) {
                	//make filed editable or not according to type of action
                	if(ui.rowData!=null){
                		 var action = ui.rowData['ACTION'];
                		 var change = ui.rowData['CHANGE'];
                		 if("local"!=change || "MODIFIED"==action || "DELETED"==action)
            			 {
                			 return false;
            			 }
                         else
                         {
                             return true;
                         }
                	}
                	else return true;                   
                }
		     },
	        { title: "ENGLISH_VAL", dataType: "string", dataIndx: "ENGLISH_VAL", width: '35' ,
		            filter: { type: 'textbox', condition: 'contain',cls :'filterClass', listeners: ['keyup'] ,attr: 'placeholder="Enter your keyword"'},
		            editor: { type: "textarea", attr: "rows=5" },
	                editModel: { keyUpDown: false, saveKey: '' },
	                editable: function (ui) {
	                	//make filed editable or not according to type of action
	                	if(ui.rowData!=null){
	                		 var action = ui.rowData['ACTION'];
	                		 var change = ui.rowData['CHANGE'];
	                		 if("DELETED"==action)
	            			 {
	                			 return false;
	            			 }
	                         else
	                         {
	                             return true;
	                         }
	                	}
	                	else return true;                   
	                },
             	   validations: [textAreaValidation]
		     },
	        { title: "SPANISH_VAL", dataType: "string", align: "right", dataIndx: "SPANISH_VAL",width: '35',
		            filter: { type: 'textbox', condition: 'contain',cls :"filterClass", listeners: ['keyup'],attr: 'placeholder="Enter your keyword"' },
		            editor: { type: "textarea", attr: "rows=5" },
	                editModel: { keyUpDown: false, saveKey: '' },
	                editable: function (ui) {
	                	//make filed editable or not according to type of action
	                	if(ui.rowData!=null){
	                		 var action = ui.rowData['ACTION'];
	                		 var change = ui.rowData['CHANGE'];
	                		 if("DELETED"==action)
	            			 {
	                			 return false;
	            			 }
	                         else
	                         {
	                             return true;
	                         }
	                	}
	                	else return true;                   
	                },
             	   validations: [textAreaValidation]
		     },
		     { title: "ACTION", dataIndx: "ACTION", width: 10,
		            filter: { type: 'textbox', condition: 'contain',cls :"filterClass", listeners: ['keyup'] ,attr: 'placeholder="Enter your keyword"'},
	                editor: { type: "textbox"},
	                editable: function(ui){
	                	var rowData = ui.rowData;
	                	if(rowData!=null)
	                	{
	                		return false;
	                	}
	                	else return true;
	                }
            },
            { title: "SOURCE", dataIndx: "SOURCE", width: 10,
            	filter: { type: 'textbox', condition: 'contain',cls :"filterClass", listeners: ['keyup'],attr: 'placeholder="Enter your keyword"' },
                editor: {
                    type: "textbox",
                    init: autoCompleteEditor
                },
                validations: [
                    { type: 'minLen', value: 1, msg: "Required" },
                    { type: function (ui) {
                        var value = ui.value;
                        if ($.inArray(ui.value, sourceList) == -1) {
                            ui.msg = value + " not found in list";
                            return false;
                        }
                    }, icon: 'ui-icon-info'
                    }
                ]
            },
            { title: "", editable: false, width: 40, sortable: false, render: function (ui) {
                return "<button type='button' class='edit_btn' onclick='deleteRowFromWorkArea(this);'>Discard Change</button>";
            	}
            }
	    ];
	    obj.dataModel = {
		    	dataType: "JSON",
		        location: "local",
		        sorting: "local",
		        sortIndx: "KEY",
		        sortDir: "down",
		        recIndx: "KEY",
	            data: data
		    };
	    dev_viewProductionTable.$gridStaging = $("#grid_staging").pqGrid(obj);
	    //get instance of the grid.
        dev_viewProductionTable.gridStaging = dev_viewProductionTable.$gridStaging.data("paramqueryPqGrid");
}


function deleteRowFromWorkArea(elem)
{
	$elem = $(elem);
	 var $tr = $elem.closest("tr"),
     rowIndx = dev_viewProductionTable.$gridStaging.pqGrid("getRowIndx", { $tr: $tr }).rowIndx;
	 
	 var rowData = dev_viewProductionTable.$gridStaging.pqGrid("getRowData", {rowIndxPage: rowIndx});
	 var ans =null;
	 if(rowData.CHANGE=='local')
	 {
		 ans= window.confirm("This change has not been committed yet. It will delete locally ");
	 }
	 else
	 {
		 ans= window.confirm("This will delete the record from DB ");
	 }
	 
	 if(ans)
	 {
		 if('local' == rowData.CHANGE) 
		 {
			 dev_viewProductionTable.$gridStaging.pqGrid("deleteRow", { rowIndx: rowIndx, effect: true });
		 }
		 else 
		 {
			 var obj ={"key":rowData.KEY,"source": rowData.SOURCE};
			 $.ajax({
		            dataType: "json",
		            type: "POST",
		            async: true,
		            beforeSend: function (jqXHR, settings) {
		                dev_viewProductionTable.gridStaging.showLoading();
		            },
		            headers: { 
		                'Accept': 'application/json',
		                'Content-Type': 'application/json' 
		            },
		            url: "rest/rsrc/deleteRsrcFromDev",                                           
		            data: JSON.stringify(obj),
		            success: function (data) {
		                //debugger;
		            	dev_viewProductionTable.$gridStaging.pqGrid("deleteRow", { rowIndx: rowIndx, effect: true });

		            },
		            error: function (data) {
		                //debugger;
		            	alert('Error in deleting');

		            },
		            complete: function () {
		                dev_viewProductionTable.gridStaging.hideLoading();
		            }
		        });
		 }
	 }
	 
}




function loadGridData(data)
{
	
	var obj = {
	        width: "100%",
	        height: 400,
	        //columnBorders: false,
	        resizable: true,
	        title: "Production Values",
	        showBottom: false,
	        scrollModel: { autoFit: true },
	        selectionModel: prodSelectionModel, 
	        track: true,
	        filterModel: { on: true, mode: "AND", header: true },
            pageModel: { type: "local", rPP: 10, strRpp: "{0}", strDisplay: "{0} to {1} of {2}" },
            toolbar:dev_viewProductionTable.prodToolBar,
            quitEditMode: function (evt, ui) {                
                if (evt.keyCode != $.ui.keyCode.ESCAPE) {
                    $grid.pqGrid("saveEditCell");
                }
            },
            editModel: {
                saveKey: $.ui.keyCode.ENTER
            }
	    };
	    
	    obj.columnTemplate = { minWidth: '10%', maxWidth: '80%' };
	    obj.colModel = prodColumns,
	    obj.dataModel = {
	    	dataType: "JSON",
	        location: "local",
	        sorting: "local",
	        sortIndx: "KEY",
	        sortDir: "down",
	        recIndx: "KEY",
	        url: "rest/rsrc/all",
            data: data
	    };
	    $grid = $("#grid_array").pqGrid(obj);
	    //get instance of the grid.
        grid = $grid.data("paramqueryPqGrid");
}


var propertyDownLoadObject={
		loadFeaturesChangedFromDB:function()
		{
			$.ajax({
		        type: "GET",
		        url:"rest/rsrc/fetchFiltersDev",
		        dataType: "json",
		        success: function (ebiresponse) {
		        	$('#featureSelectBox').empty();
		        	var items=ebiresponse.alListOfMaps;
		        	if(items!=null && items.length>0)
		        	{
		        		$.each(items,function(i,item)
		        				{
		        			var div_data="<option value='"+item.SOURCE+"'>"+item.SOURCE+"</option>";
		        			$(div_data).appendTo('#featureSelectBox'); 
		        				});

		        		$(".dummyfeatureSelectBox").prop( "disabled", false );
		        		$('.dropdownSucessText').show();
		        		$('.dropdownErrorText').hide();		        		
		        	}
		        	else
		        	{
		        		$(".dummyfeatureSelectBox").prop( "disabled", true );
		        		$('.dropdownSucessText').hide();
		        		$('.dropdownErrorText').show();
		        	}		            
		            $(".dummyfeatureSelectBox").chosen('destroy'); 
		            $(".dummyfeatureSelectBox").chosen();		            
		          },
		          complete:function(){
		        	  $('#featureSelectBox_chosen').css('width','500px');
		          }
		      });
		},	
		fetchCustomProps:function(){
			$.ajax({
		        type: "GET",
		        url:"rest/rsrc/fetchCustomKeys",
		        dataType: "json",
		        success: function (ebiresponse) {
		        	var items=ebiresponse.appRsrcList;
		        	var info="<div class='left'>\
			    		<label class='customKeyDummyClass' style='line-height: 25px;'>{0}</label>\
        			  </div>\
					  <div class='right'>\
					    <input class='customValueDummyClass' style='width:100%' type='text' value='{1}'>\
					  </div>";
		        	for (var int = 0; int < items.length; int++) {
		        		 $('#dummyCustomPropDisplay').append(info.format(items[int].key,items[int].englishval));	
					}
		        	
			       
		        },
		        error:function(){
		        
		        }
		    });
		}
}


var releaseMlStoneTracker={
	fetchArtifacts:function(){
		if($("#milestoneSelectBox").chosen().val()!=null && $("#milestoneSelectBox").chosen().val()!='')
		{
			refUrl=$("#milestoneSelectBox").chosen().val();	
			refUrl = refUrl.split('||')[1];
		}	
		var data={};
		data.refUrl=refUrl;
		$.ajax({
	        type: "POST",
	        url:"rest/rsrc/fetchMlStone/artifacts",
	        headers: { 
                'Accept': 'application/json',
                'Content-Type': 'application/json' 
            },
	        dataType: "json",
	        data:JSON.stringify(data),
	        async: true,
			beforeSend: function (jqXHR, settings) {
				attLaoderAnimation.showLoaderAnimation();
			},
			complete: function () {
				attLaoderAnimation.stopAnimation();
			},
	        success: function (ebiResponse) {
	        	attLaoderAnimation.stopAnimation();
	        	var rallyResponse = ebiResponse.rallyResponse;
				rallyResponseJson = JSON.parse(rallyResponse);
				var result = rallyResponseJson.QueryResult.Results;
				result.sort(commonUtil.compareRallyArtifactByName);
				$('#simpleTableBody').empty();
				for (var i = 0; i < result.length; i++) {
                	var artifactId = result[i].FormattedID;
                	var artifactName = result[i]._refObjectName; 
                	$('#simpleTableBody').append("<tr><td>"+(i+1)+"</td><td>"+artifactId+"</td><td>"+artifactName+"</td></tr>");
            	}
				$('#artifactTable').show();
	        },
	        error:function(){
	        
	        }
	    });
	},
	updateCurrMlStone:function(){
		var tagMessage = $('#currMlStoneMessageId').val();
		var currMlselectedVal=$("#milestoneSelectBox").chosen().val();	
		var mlValue = currMlselectedVal.split('||')[1];
		var mlKey = currMlselectedVal.split('||')[0];
		var data={"prop_key":mlKey,"prop_val":mlValue,"description":tagMessage};
		$.ajax({
			dataType: "json",
			type: "POST",
			async: true,
			beforeSend: function (jqXHR, settings) {
				attLaoderAnimation.showLoaderAnimation();
			},
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json' 
			},
			url: 'rest/rsrc/milestone/update',
			data:JSON.stringify(data),
			success: processResponseData.processSuccess,
			error:processResponseData.processError,
			complete: function () {
				attLaoderAnimation.stopAnimation();
				releaseMlStoneTracker.fetchCurrMlStone();
			}
		});

	},
	updateCurrentMlStoneButtonClk:function()
	{
		$("#popup-dialog-mlstone").dialog({
				title : "Update Current MileStone",
				buttons : {
					Submit : {
						text : "Submit",
						id : "tag-submit-id",
						click : function() {
							if ($("#currMlStoneMessageId").val() == '') {
								alert("Please provide a message for updating milestone.");
								return false;
							}
							releaseMlStoneTracker.updateCurrMlStone();
							$(this).dialog("close");
						}
					},
					Cancel : {
						text : "Cancel",
						id : "tag-cancel-id",
						click : function() {
							$(this).dialog("close");
						}
					}
				}
			});
		$('#currMlStoneMessageId').val("");
		$("#popup-dialog-tagtoFile").dialog("open");
	},
	fetchCurrMlStone:function(){
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
			url: 'rest/rsrc/milestone/current',
			success:  function (ebiResponse) {
				var items = ebiResponse.alListOfMaps;
				if(items!=null && items.length>0)
	        	{
	        		var item =items[0];
	        		var displayVal = item.PROP_KEY;
	        		var refUrl = item.PROP_VAL;
	        		var changedBy = item.CHANGED_BY;
	        		var desc = item.DESCRIPTION+ " - (Changed By "+changedBy+")";	        		
	        		$('#currentFeatureSelectBoxDiv').html("Current marked milestone is <a style='color:blue' href='#' target='_blank' title='Comment: "+desc+"'>"+displayVal+"</a>");
	        		$('#currentFeatureSelectBoxDiv').attr("data-id",(displayVal+'||'+refUrl));
	        		
	        	}
			},
			error:processResponseData.processError,
			complete: function () {
				attLaoderAnimation.stopAnimation();
			}
		});

	},
	displayReleaseMlStoneTracker:function()
	{
		releaseMlStoneTracker.fetchCurrMlStone();
		rallyService.populateRallyMileStoneInDropDown($('#milestoneSelectBox'));
	}
	
		
}

dev_viewProductionTable.getAllProdVal();
rallyService.populateRallyFeatureDefectsInDropDown($('.dummyEditorFeature'));
propertyDownLoadObject.fetchCustomProps();

$(document).ready(function(){
	/*$('#checkbox-prod').click(function(e){
		$('#dummyFeatureSelectBoxDiv').toggle();
			
	});*/
	
});

