<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<link rel="stylesheet" href="assets\dropzone\dropzone.css" />
	<link rel="stylesheet" href="assets\custom\reconcile.css" />
	<style type="text/css">
    .reconcile{
        /* Permalink - use to edit and share this gradient: http://colorzilla.com/gradient-editor/#f8ffe8+0,e3f5ab+33,b7df2d+100;Green+3D+%234 */
		background: #f8ffe8; /* Old browsers */
		background: -moz-linear-gradient(top, #f8ffe8 0%, #e3f5ab 33%, #b7df2d 100%) !important; /* FF3.6-15 */
		background: -webkit-linear-gradient(top, #f8ffe8 0%,#e3f5ab 33%,#b7df2d 100%)!important; /* Chrome10-25,Safari5.1-6 */
		background: linear-gradient(to bottom, #f8ffe8 0%,#e3f5ab 33%,#b7df2d 100%) !important; /* W3C, IE10+, FF16+, Chrome26+, Opera12+, Safari7+ */
		filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#f8ffe8', endColorstr='#b7df2d',GradientType=0 ); /* IE6-9 */
    }
    
    .reconcile:disabled {
    	/* Permalink - use to edit and share this gradient: http://colorzilla.com/gradient-editor/#eeeeee+0,cccccc+100;Gren+3D */
		background: #eeeeee  !important; /* Old browsers */
		background: -moz-linear-gradient(top, #eeeeee 0%, #cccccc 100%)  !important; /* FF3.6-15 */
		background: -webkit-linear-gradient(top, #eeeeee 0%,#cccccc 100%)  !important; /* Chrome10-25,Safari5.1-6 */
		background: linear-gradient(to bottom, #eeeeee 0%,#cccccc 100%)  !important; /* W3C, IE10+, FF16+, Chrome26+, Opera12+, Safari7+ */
		filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#eeeeee', endColorstr='#cccccc',GradientType=0 )  !important; /* IE6-9 */;
    }
    
    .dz-details{
    	padding-top: 10px !important;
    	margin-bottom: 1px !important;
    }
    
    .dz-size{
    	margin-bottom: 1px !important;
    }
</style>
</head>
	<div class="container">
              <div class="panel panel-default">
                     <div class="panel-heading text-center">
                           <h3>To reconcile pages , you have to drag and drop or click to add the properties file in the white location.
                           The name of the files must match  AppResources_en.properties and AppResources_es.properties.
                            Both files must be uploaded together</h3>
                     </div>
                     <div class="panel-body">
                           <div>
                                  <form id="dropzone-form" class="dropzone"
                                         enctype="multipart/form-data">

                                         <div class="dz-default dz-message file-dropzone text-center well col-sm-12">
                                                 <span class="glyphicon glyphicon-paperclip"></span> <span>
                                                       To attach files, drag and drop here</span><br>
                                                <span>OR</span><br>
                                                <span>Just Click</span>
                                         </div>

                                         <!-- this is were the previews should be shown. -->
                                         <div class="dropzone-previews"></div>
                                  </form>
                                  
                                  <div align="center" style="padding-top: 15px;">
	                                  <button type="button" class="ui-corner-all  ui-button ui-widget ui-state-default ui-button-text-icon-primary ui-button-text reconcile" id="upload-button"  disabled="disabled" style="margin:0px 5px;" role="button" aria-disabled="false">
		                                  
		                                  <span class="ui-button-text"><i class="fa fa-exchange"></i>&nbsp;Reconcile Files</span>
	                                 </button>
                                 </div>
                           </div>
                     </div>
              </div>
       </div>
	<div class="clear" style="height: 45px;"></div>	
	
	<div>
		<div id="grid_reconcile"></div>
		<div class="clear" style="height: 20px;"></div>		
	</div>	
	
	<script src="assets\dropzone\dropzone.js"></script>
	
	<script type="text/javascript"></script>
	
	<script src="assets\custom\reconcile.js"></script>
	
	<!-- <div id="tpl">
		<div class="dz-preview dz-file-preview">
		  <div class="dz-details">
		    <div class="dz-filename"><span data-dz-name></span></div>
		    <div class="dz-size" data-dz-size></div>
		    <img data-dz-thumbnail />
		  </div>
		  <div class="dz-progress"><span class="dz-upload" data-dz-uploadprogress></span></div>
		  <div class="dz-success-mark"><span><i class="fa fa-check" aria-hidden="true"></i></span></div>
		  <div class="dz-error-mark"><span><i class="fa fa-times" aria-hidden="true"></i></span></div>
		  <div class="dz-error-message"><span data-dz-errormessage></span></div>
		</div>	
	</div> -->
	
	
	
</html>