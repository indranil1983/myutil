var prodPageEditor=
{
		isProdEditableFlag: true,
		prodSelectionModel : {},
		textAreaValidation:{ type: 'maxLen', value: 5950, msg: "Max chars allowed =5950"},
		countTextAreaInTagToFile:function(){
			var text_max = 200;
			$('#count_message').html(text_max + ' remaining');

			$('#tagToFileMessageId').keyup(function() {
			  var text_length = $('#tagToFileMessageId').val().length;
			  var text_remaining = text_max - text_length;
			  
			  $('#count_message').html(text_remaining + ' remaining');
			});
		},
		getProdToolBar :function(){		   
			var toolBar=
			{ items :[

			          { type: 'button', icon: 'ui-icon-disk', label: 'New Key', style: 'margin:0px 5px;', listeners: [
			                                                                                                          { "click": prodPageEditor.addRow
			                                                                                                          }
			                                                                                                          ]
			          },
			          { type: 'button', icon: 'ui-icon-disk', label: 'Accept Changes', style: 'margin:0px 5px;', listeners: [
			                                                                                                                 { "click": function (evt, ui) {
			                                                                                                                	 prodPageEditor.acceptChanges();
			                                                                                                                 }
			                                                                                                                 }
			                                                                                                                 ]
			          },
			          { type: 'button', icon: 'ui-icon-trash', label: 'Reject Changes', listeners: [
			                                                                                         { "click": function (evt, ui) {
			                                                                                        	 prodPageEditor.$grid.pqGrid("rollback");
			                                                                                         }
			                                                                                         }
			                                                                                         ]
			          },
			          { type: 'separator' },
			          { type: 'button', icon: 'ui-icon-lightbulb', label: 'Show Changes', style: 'margin:0px 5px;', 
			        	  listeners: [
                                       { "click": function (evt, ui) 
                                    	   {
                                    	   prodPageEditor.showChanges();
                                    	   }
                                       }
                                       ]
			          },
			          { type: 'separator' },
			          { type: 'button', icon: 'ui-icon-tag', label: 'Tag to File',attr:'id="tagtoFileButton"'  , style: 'margin:0px 5px;', 
			        	  listeners: [
	                                    { "click": function (evt, ui) 
	                                    	{                                    	
		                                    	$("#popup-dialog-tagtoFile").dialog({ title: "Tag To File", 
		                                    		buttons: {
		                                    			Submit: {
		                                    				text: "Submit",
		                                    				id: "tag-submit-id",
		                                    				click: function () {
		                                    					if($("#tagToFileMessageId").val()==''){
		                                    						alert("Please provide a message for tagging.");
		                                    						return false;
		                                    					}
		                                    					prodPageEditor.publishChanges();   
		                                    					$(this).dialog("close");
		                                    				}
		                                    			},
			                            			    Cancel: {
			                            			    	  text: "Cancel",
			                            			    	  id: "tag-cancel-id",
			                            			    	  click: function () {
			                            			    		  $(this).dialog("close");
			                            			    	  }
			                            			    }
		                                    		}
		                            			});
		                                    	$('#tagToFileMessageId').val("");
		                                    	$("#popup-dialog-tagtoFile").dialog("open");	                                    		
	                                    	}
	                                    }
	                                  ]
			          },
			          { type: 'separator' },
			          { type: 'button', icon: 'ui-icon-circle-arrow-s', label: 'Download Properties', style: 'margin:0px 5px;',
			        	  listeners: [
	                      { "click": function (evt, ui) 
	                    	  {
	                    	  $.ajax({
	                    		  dataType: "json",
	                    		  type: "GET",
	                    		  async: true,
	                    		  beforeSend: function (jqXHR, settings) {
	                    			  prodPageEditor.grid.showLoading();
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
	                    			  prodPageEditor.grid.hideLoading();
	                    		  },
	                    		  error:processResponseData.processError
	                    	  });
	                    	  }
	                      }
	                      ]
			          }


			          ]
			}
			return toolBar;
		},	
		getProdColumns:function()
		{
			var prodColumns = [
			                   { title: "Key", dataType: "string", dataIndx: "KEY", width: '20', editable: function(ui){
			                	   var rowData = ui.rowData;
			                	   if(rowData!=null)
			                	   {
			                		   return false;
			                	   }
			                	   else return true;
			                   },
			                   filter: { type: 'textbox', condition: 'contain', listeners: ['keyup'],cls :"filterClass", attr: 'placeholder="Enter your keyword ..."'},
			                   editor: { type: "textarea", attr: "rows=2,readonly=true" },
			                   editModel: { keyUpDown: false, saveKey: '' },
			                   validations: [{ type: 'minLen', value: 1, msg: "Required"},
			                                 { type: 'maxLen', value: 99, msg: "Max chars allowed =99"}],
			                   render: function (ui) {
			                	   var rowData = ui.rowData;
			                	   return "<a href='javascript:void(0)' onclick='prodPageEditor.showAudit(this)' >"+rowData.KEY+"</a>";
			                   }   
			                   },
			                   { title: "ENGLISH_VAL", dataType: "string", dataIndx: "ENGLISH_VAL", width: '35' ,editable:this.isProdEditableFlag,
			                	   filter: { type: 'textbox', condition: 'contain', listeners: ['keyup'],cls :"filterClass",attr: 'placeholder="Enter your keyword"' },
			                	   editor: { type: "textarea", attr: "rows=5" },
			                	   editModel: { keyUpDown: false, saveKey: '' },
			                	   validations: [prodPageEditor.textAreaValidation]
			                   },
			                   { title: "SPANISH_VAL", dataType: "string", dataIndx: "SPANISH_VAL",width: '35',editable:this.isProdEditableFlag,
			                	   filter: { type: 'textbox', condition: 'contain', listeners: ['keyup'],cls :"filterClass",attr: 'placeholder="Enter your keyword"' },
			                	   editor: { type: "textarea", attr: "rows=5" },
			                	   editModel: { keyUpDown: false, saveKey: '' },
			                	   validations: [prodPageEditor.textAreaValidation]
			                   }			     
			                   ];
			return prodColumns;			
		},
		grid:null,
		$grid:null,		
		showAudit:commonUtil.showAudit,		
		addRow:function()
		{
			$("#popup-dialog-crud").dialog({ title: "Add Record", buttons: {
				Add: function () {
					var row ={};
					$frm = $('#crud-form');
					//save the record in DM.data.
					row.KEY = $frm.find("input[name='key']").val();
					row.ENGLISH_VAL = $frm.find("textarea[name='engVal']").val();
					row.SPANISH_VAL = $frm.find("textarea[name='spanishVal']").val();
					row.SOURCE="PROD";
					var rowIndex = prodPageEditor.$grid.pqGrid('addRow', { rowData: row});
					prodPageEditor.$grid.pqGrid("goToPage", { rowIndx: rowIndex });
					prodPageEditor.$grid.pqGrid("editFirstCellInRow", { rowIndx: rowIndex });    
					$(this).dialog("close");
				},
				Cancel: function () {
					$(this).dialog("close");
				}
			}
			});
			$("#popup-dialog-crud").dialog("open");
		},
		calculateChanges:function()
		{
			var addListModified = new HashMap();
			
			if (this.grid.saveEditCell() === false) {
				return false;
			}		    var isDirty = this.grid.isDirty();
			if (isDirty) {
				//validate the new added rows. 
				var addList = this.grid.getChanges().addList;
				var updatedList = this.grid.getChanges().updateList;
				
				for (var i = 0; i < updatedList.length; i++) {
					var rowData = updatedList[i];
					var isValid = this.grid.isValid({ "rowData": rowData }).valid;
					if (!isValid) {
						return;
					}
					var obj = {
							"key":rowData.KEY,
							"englishval":rowData.ENGLISH_VAL,
							"spanishval":rowData.SPANISH_VAL,
							"source":rowData.SOURCE,
							"action":'MODIFIED'
					}
					addListModified.put(obj.key,obj);
				}

				for (var i = 0; i < addList.length; i++) {
					var rowData = addList[i];
					var isValid = this.grid.isValid({ "rowData": rowData }).valid;
					if (!isValid) {
						return;
					}
					var obj = {
							"key":rowData.KEY,
							"englishval":rowData.ENGLISH_VAL,
							"spanishval":rowData.SPANISH_VAL,
							"source":rowData.SOURCE,
							"action":'NEW'				
					}
					addListModified.put(obj.key,obj);
				}
			}
			console.log(addListModified);
			console.log(addListModified.values());
			return  addListModified.values();   
		},
		acceptChanges:function() {
			//attempt to save editing cell.
			//debugger;
			var changesList=prodPageEditor.calculateChanges();
			if(changesList!=null && changesList.length>0)
			{
				$('#summaryTable').empty();
				for (var i = 0; i < changesList.length; i++) {
					var rowSummary = "<tr><td>"+changesList[i].action+"</td><td>"+changesList[i].key+"</td><td>"+changesList[i].englishval+"</td><td>"+changesList[i].spanishval+"</td></tr>";
					$('#summaryTable').append(rowSummary);
				}
				$("#popup-dialog-summary").dialog({ width: '900px', modal: true,autoOpen: false,title: "Accept Changes", buttons: {
					Add: function () {
						//post changes to server 
						$.ajax({
							dataType: "json",
							type: "POST",
							async: true,
							beforeSend: function (jqXHR, settings) {
								prodPageEditor.grid.showLoading();
								$("#popup-dialog-summary").dialog("close");
							},
							headers: { 
								'Accept': 'application/json',
								'Content-Type': 'application/json' 
							},
							url: "rest/rsrc/directUpdateProductionRsrc",                                           
							data: JSON.stringify(changesList),
							success: function (changes) {
								//debugger;		    		            	
								prodPageEditor.$grid.pqGrid( "destroy" );
								getAllProdVal();
								processResponseData.processSuccess(changes);

							},
							complete: function () {
								prodPageEditor.grid.hideLoading();
							}
						});

					},
					Cancel: function () {
						$(this).dialog("close");
					}
				}
				});
				$("#popup-dialog-summary").dialog("open");
			}
			else
			{
				alert("No changes detected");
			}

		},
		showChanges:function()
		{
			$('#summaryTable').empty();
			var changesList = prodPageEditor.calculateChanges();
			if(changesList!=undefined && changesList!=null && changesList.length>0)
			{
				for (var i = 0; i < changesList.length; i++) {
					var rowSummary = "<tr><td>"+changesList[i].action+"</td><td>"+changesList[i].key+"</td><td>"+changesList[i].englishval+"</td><td>"+changesList[i].spanishval+"</td></tr>";
					$('#summaryTable').append(rowSummary);
				}
				$("#popup-dialog-summary").dialog({ width: '900px', modal: true,autoOpen: false,title: "View Changes", buttons: {
					Cancel: function () {
						$(this).dialog("close");
					}
				}
				});
				$("#popup-dialog-summary").dialog("open");
			}
			else
			{
				alert("No changes detected");
			}

		},
		publishChanges:function()
		{
			var tagMessage = $('#tagToFileMessageId').val();
			var data={"tagMessage":tagMessage};
			$.ajax({
				dataType: "json",
				type: "POST",
				async: true,
				beforeSend: function (jqXHR, settings) {
					prodPageEditor.grid.showLoading();
				},
				headers: { 
					'Accept': 'application/json',
					'Content-Type': 'application/json' 
				},
				url: 'rest/rsrc/publish',
				data:JSON.stringify(data),
				success: processResponseData.processSuccess,
				error:processResponseData.processError,
				complete: function () {
					$('#tagToFileMessageId').val("");
					prodPageEditor.grid.hideLoading();
					prodPageEditor.enableFileTagButton();
				}
			});

		},
		loadGridData :function(data)
		{			
			var obj = {
					width: "100%",
					height: 600,
					//columnBorders: false,
					resizable: true,
					title: "Production AppResource Properties",
					showBottom: false,
					scrollModel: { autoFit: true },
					selectionModel: this.prodSelectionModel, 
					track: true,
					filterModel: { on: true, mode: "AND", header: true },
					pageModel: { type: "local", rPP: 20, strRpp: "{0}", strDisplay: "{0} to {1} of {2}" },
					toolbar:this.getProdToolBar(),
					quitEditMode: function (evt, ui) {                
						if (evt.keyCode != $.ui.keyCode.ESCAPE) {
							this.$grid.pqGrid("saveEditCell");
						}
					},
					editModel: {
						saveKey: $.ui.keyCode.ENTER
					}
			};

			obj.columnTemplate = { minWidth: '10%', maxWidth: '80%' };
			obj.colModel = this.getProdColumns(),
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
			this.$grid = $("#grid_array").pqGrid(obj);
			//get instance of the grid.
			this.grid = this.$grid.data("paramqueryPqGrid");
			this.$grid.editorEnd = function( event, ui ) {alert('')};
			$("#popup-dialog-crud").dialog({ width: 500, modal: true,
				open: function () { $(".ui-dialog").position({ of: "#grid_array" }); },
				autoOpen: false
			});
		},
		enableFileTagButton:function()
		{
			$.ajax({
				url: 'rest/rsrc/fetchPublishStats',
				error:function(xmlhttp){
					$('#tagtoFileButton').prop('disabled',true);
					$('#tagtoFileButton').find('.ui-button-text').html("Tag to File (0)");
					$('#tagtoFileButton').attr('title','new');
					$('#tagtoFileButton').attr('title','There are no changes to be tagged in file');
					processResponseData.processError(xmlhttp);
				},
				dataType: 'json',
				success: function(ebiResponse) {			
					if(ebiResponse.alListOfMaps!=undefined && ebiResponse.alListOfMaps.length>0)
					{
						$('#tagtoFileButton').prop('disabled',false);
						$('#tagtoFileButton').find('.ui-button-text').html("Tag to File ("+ebiResponse.alListOfMaps.length+")");
						$('#tagtoFileButton').attr('title','There are '+ebiResponse.alListOfMaps.length+' change(s) to be tagged in file');
					}
					else
					{
						$('#tagtoFileButton').prop('disabled',true);
						$('#tagtoFileButton').find('.ui-button-text').html("Tag to File ("+ebiResponse.alListOfMaps.length+")");
						$('#tagtoFileButton').attr('title','There are no changes to be tagged in file');
					}
				},
				type: 'GET'
			});
		}	
};


var stagingTableObject={
		fetchAllFeaturesInStaging:function()
		{
			 //load select box for feature
		   	$.ajax({
		        type: "GET",
		        url:"rest/rsrc/fetchFiltersDev",
		        dataType: "json",
		        success: function (data) {
		            $.each(data.alListOfMaps,function(i,item)
		            {
		             	var div_data="<option value='"+item.SOURCE+"'>"+item.SOURCE+"</option>";
		             	$(div_data).appendTo('#featureSelectBox'); 
		            });
		            $(".dummyfeatureSelectBox").chosen('destroy'); 
		            $(".dummyfeatureSelectBox").chosen();  
		            $('#featureSelectBox_chosen').css('width','500px');
		          },
		          error:function(data){
		        	  $(".dummyfeatureSelectBox").chosen();  
			          $('#featureSelectBox_chosen').css('width','500px');
		        	  processResponseData.processError(data);
		          }
		      });   	 
		},
		$gridStaging:null,
		gridStaging:null,
		promote:function(){
            //append empty row at the end.  
            selarray = stagingTableObject.$gridStaging.pqGrid('selection', { type: 'row', method: 'getSelection' });
            var addListModified =[];
            for (var i = 0; i < selarray.length; i++) {
				var rowData = selarray[i].rowData;
				var obj = {
						"key":rowData.KEY,
						"englishval":rowData.ENGLISH_VAL,
						"spanishval":rowData.SPANISH_VAL,
						"source":rowData.SOURCE,
						"action":rowData.ACTION					
		             }
				addListModified.push(obj);
            }
            $.ajax({
                dataType: "json",
                type: "POST",
                async: true,
                beforeSend: function (jqXHR, settings) {
               	 stagingTableObject.gridStaging.showLoading();
                },
                headers: { 
                    'Accept': 'application/json',
                    'Content-Type': 'application/json' 
                },
                url: "rest/rsrc/promoteDevToProd",                                           
                data: JSON.stringify(addListModified),
                success: function (changes) {
                    //debugger;			                                	 
               	 getAllProdVal();
               	 stagingTableObject.showStagingGrid();
               	 processResponseData.processSuccess(changes);

                },
                complete: function () {
               	 stagingTableObject.gridStaging.hideLoading();
                }
            });
        },
		showStagingGrid:function()
		{
			try{$("#grid_staging").pqGrid("destroy");}
			catch(e){}
			var selectedVal =$(".chosen-select").chosen().val();
			var getObj = {
		            url: "rest/rsrc/filter",
		            data: JSON.stringify(selectedVal)
		        };

		      var objStaging = { 
		      		width: "100%",
		     	    height: 400,
		             editable: false,            
		             title:"Checkbox selections",
		             selectionModel: { type: 'none', subtype:'incr', cbHeader:true, cbAll:true}, 
		             pageModel: {type:"local", rPP:10 },
		             toolbar: 
		             {
		                 items: [
		                     { type: 'button', icon: 'ui-icon-plus', label: 'Promote Properties', 
		                       listeners: 
		                       [
		                         { "click": function (evt, ui) 
		                             {
		                        	 	commonUtil.showConfirmDialog("Are you sure you want to promote the selected keys to production",stagingTableObject.promote);
		                             }
		                         }
		                        ]
		                     }
		                   ]
		             },
		             colModel:
		          	   [
								{ title: "", dataIndx: "state", width: "2%", minWidth:30, align: "center", type:'checkBoxSelection', cls: 'ui-state-default', resizable: false, sortable:false },
		            	        { title: "Key", dataType: "string", dataIndx: "KEY", width: "18%", editable: false
		            		     },
		            	        { title: "ENGLISH_VAL", dataType: "string", dataIndx: "ENGLISH_VAL",width: "25%", editable: false
		            		     },
		            	        { title: "SPANISH_VAL", dataType: "string", align: "right", dataIndx: "SPANISH_VAL",width: "25%", editable: false
		            		     },
		            	        { title: "CHANGED_BY", dataType: "string", align: "right", dataIndx: "CHANGED_BY",width: "10%", editable: false
		            		     },
		            		     { title: "SOURCE", dataType: "string", align: "right", dataIndx: "SOURCE",width: "10%", editable: false
		            		     },
		            		     { title: "ACTION", dataType: "string", align: "right", dataIndx: "ACTION",width: "8%", editable: false
		            		     }
		            		  
		            	    ],
		             dataModel: {
		          	   dataType: "JSON",
		     	       	   location: "remote",
		                 sorting: "local",
		                 sortIndx: "KEY",
		                 sortDir: "down",
		                 recIndx: "KEY",
		                 method: "POST",
		                 contentType: "application/json",
		                 getUrl: function () {return getObj;},
		                 getData: function (response) {
		                     return { data: response.alListOfMaps };
		                 }
		             }
		         };
		      	this.$gridStaging = $("#grid_staging").pqGrid(objStaging);
		      	this.gridStaging = this.$gridStaging.data("paramqueryPqGrid");

		}		
};

var reconcileTableObject={
		
		$gridReconcile:null,
		gridReconcile:null,
		saveReconciliation:function(){
            //append empty row at the end.  
            selarray = reconcileTableObject.$gridReconcile.pqGrid('selection', { type: 'row', method: 'getSelection' });
            var addListModified =[];
            for (var i = 0; i < selarray.length; i++) {
				var rowData = selarray[i].rowData;
				var obj = {
						"key":rowData.key,
						"englishval":rowData.englishval,
						"spanishval":rowData.spanishval,
						"source":"RECONCILE",
						"action":rowData.action					
		             }
				addListModified.push(obj);
            }
            $.ajax({
                dataType: "json",
                type: "POST",
                async: true,
                beforeSend: function (jqXHR, settings) {
                	reconcileTableObject.gridReconcile.showLoading();
                },
                headers: { 
                    'Accept': 'application/json',
                    'Content-Type': 'application/json' 
                },
                url: "rest/rsrc/saveReconcileToProd",                                           
                data: JSON.stringify(addListModified),
                success: function (ebiResponse) {
                	//debugger;			                                	 
                	//getAllProdVal();
                	if(ebiResponse.errorList!=null && ebiResponse.errorList.length>0)
            		{
                		processResponseData.processError(ebiResponse);
            		}
                	else{
                		alert("Files have been sucessfully reconciled. There are no errors. Please refresh the page to view the Production table.");
                	}
                	/*for (var i = 0; i < selarray.length; i++) {
         				var rowIndx = selarray[i].rowIndx-i;
         				console.log('delete '+selarray[i].rowIndx);
         				reconcileTableObject.$gridReconcile.pqGrid("deleteRow", { rowIndx: rowIndx } );
                	}*/
                	reconcileTableObject.showReconcileGrid(null);
                	getAllProdVal();
                	
                },
                complete: function () {
                	reconcileTableObject.gridReconcile.hideLoading();
                }
            });
        },
		showReconcileGrid:function(listBeans)
		{
			try{$("#grid_reconcile").pqGrid("destroy");}
			catch(e){}
			var objReconcile = { 
		      		width: "99%",
		             editable: false,            
		             title:"Checkbox selections",
				     scrollModel: { autoFit: true },
		             selectionModel: { type: 'none', subtype:'incr', cbHeader:true, cbAll:true}, 
		             pageModel: {type:"local", rPP:10 },
		             toolbar: 
		             {
		                 items: [
		                     { type: 'button', icon: 'ui-icon-plus', label: 'Save Reconciliation', 
		                       listeners: 
		                       [
		                         { "click": function (evt, ui) 
		                             {
		                        	 	commonUtil.showConfirmDialog("Are you sure you want to promote the selected keys to production",reconcileTableObject.saveReconciliation);
		                             }
		                         }
		                        ]
		                     }
		                   ]
		             },
		             colModel:
		          	   [
								{ title: "", dataIndx: "state", width: "2%", minWidth:30, align: "center", type:'checkBoxSelection', cls: 'ui-state-default', resizable: false, sortable:false },
		            	        { title: "Key", dataType: "string", dataIndx: "key", width: "18%", editable: false
		            		     },
		            	        { title: "ENGLISH_VAL", dataType: "string", dataIndx: "englishval",width: "35%", editable: false
		            		     },
		            	        { title: "SPANISH_VAL", dataType: "string", align: "right", dataIndx: "spanishval",width: "35%", editable: false
		            		     },
		            		     { title: "ACTION", dataType: "string", align: "right", dataIndx: "action",width: "9.5%", editable: false
		            		     }
		            	    ],
		             dataModel: {
		          	   dataType: "JSON",
		     	       	   location: "local",
		                 sorting: "local",
		                 sortIndx: "key",
		                 sortDir: "down",
		                 recIndx: "key",
		                 contentType: "application/json",
		                 data: listBeans
		             }
		         };
		      	this.$gridReconcile = $("#grid_reconcile").pqGrid(objReconcile);
		      	this.gridReconcile = this.$gridReconcile.data("paramqueryPqGrid");

		}		
};

function getAllProdVal()
{
	try{
		prodPageEditor.$grid.pqGrid( "destroy" );
	}catch (e) {
		// TODO: handle exception
	}
	$.ajax({
		url: 'rest/rsrc/all',
		data: {
			format: 'json'
		},
		error:function(xhtml){
			prodPageEditor.enableFileTagButton();
			processResponseData.processError(xhtml);
		},
		dataType: 'json',
		success: function(ebiResponse) {			
			prodPageEditor.loadGridData(ebiResponse.alListOfMaps);
			prodPageEditor.enableFileTagButton();
			$("button[title='Refresh']").each(function() {
				$(this).remove();
			});
		},
		type: 'GET'
	});
	
	
	
}
	


$( document ).ready(function() {
	getAllProdVal();
	stagingTableObject.fetchAllFeaturesInStaging();
	stagingTableObject.showStagingGrid();
	
	
});





