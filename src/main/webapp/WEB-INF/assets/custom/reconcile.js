$(document).ready(function() {

	       $(".file-dropzone").on('dragover', handleDragEnter);
	       $(".file-dropzone").on('dragleave', handleDragLeave);
	       $(".file-dropzone").on('drop', handleDragLeave);

	       function handleDragEnter(e) {

	              this.classList.add('drag-over');
	       }

	       function handleDragLeave(e) {

	              this.classList.remove('drag-over');
	       }

	       // "dropzoneForm" is the camel-case version of the form id "dropzone-form"
	       Dropzone.options.dropzoneForm = {

	    		   url : "rest/rsrc/reconcile", // not required if the <form> element has action attribute
	    		   autoProcessQueue : false,
	    		   uploadMultiple : true,
	    		   maxFilesize : 1, // MB
	    		   parallelUploads : 2,
	    		   maxFiles : 2,
	    		   addRemoveLinks : true,
	    		   previewsContainer : ".dropzone-previews",
	    		   acceptedFiles:".properties",
	    		   success:function(files,ebiResponse)
	    		   {
	    			   //alert(ebiResponse);

	    		   },
	    		   // The setting up of the dropzone
	    		   init : function() {

	    			   var myDropzone = this;

	    			   // first set autoProcessQueue = false
	    			   $('#upload-button').on("click", function(e) {

	    				   myDropzone.processQueue();
	    			   });

	    			   // customizing the default progress bar
	    			   this.on("uploadprogress", function(file, progress) {
	    				   progress = parseFloat(progress).toFixed(0);
	    				   var progressBar = file.previewElement.getElementsByClassName("dz-upload")[0];
	    				   progressBar.innerHTML = progress + "%";
	    			   });

	    			   // displaying the uploaded files information in a Bootstrap dialog
	    			   this.on("successmultiple", function(files, serverResponse) {
	    					if(serverResponse.errorList.length>0)
	    					{
	    						alert(serverResponse.errorList);
	    					}
	    					else{	    						
	  	    				   reconcileTableObject.showReconcileGrid(serverResponse.appRsrcList);
	    					}
	    			   });
	    			   
	    			   this.on("complete", function(file) {
	    				   this.removeFile(file);
	    				 });
	    			   
	    			   this.on("removedfile", function(file) {
	    				   $('#upload-button').prop("disabled",true);
	    			   });
	    			   
	    			   this.on("addedfile", function(file) {
	    				   if("AppResources_en.properties"!=file.name && "AppResources_es.properties"!=file.name)
    					   {
	    					   myDropzone.removeFile(file);
	    					   alert("Only AppResources_en.properties and AppResources_es.properties can be uploaded.");
    					   }
	    				   else
    					   {
	    					   var progressBar = file.previewElement.getElementsByClassName("dz-upload")[0];
		    				   progressBar.innerHTML = "0%";
	    					   var fileList = myDropzone.files;
	    					   var engFile=false; var spaFile=false;
	    					   for(var i=0;i<fileList.length;i++)
    						   {
	    						   if("AppResources_en.properties"==fileList[i].name)
    							   {
	    							   engFile=true;
    							   }
	    						   else if("AppResources_es.properties"==fileList[i].name)
    							   {
	    							   spaFile=true;
    							   }
    						   }
	    					   if(engFile && spaFile)
    						   {
	    						   $('#upload-button').prop("disabled",false);
    						   }
	    					   else $('#upload-button').prop("disabled",true);
    					   }
	    				   
	    				 });
	    			   
	    		   }
	       }

	});