package com.ericsson.spring.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ericsson.spring.constants.EBIToolConstants;
import com.ericsson.spring.model.AppResourceBean;
import com.ericsson.spring.model.EbiUtilResponseBean;
import com.ericsson.spring.model.FileDiffRequest;
import com.ericsson.spring.model.MiscPropertiesBean;
import com.ericsson.spring.model.SwReleaseBean;
import com.ericsson.spring.service.AppresourceService;
import com.ericsson.spring.service.RallyService;
import com.ericsson.spring.util.AppresourceUtil;
/**
 * Handles requests for the Employee service.
 */
 


@Controller
public class AppResourceController {

	private static final Logger logger = LoggerFactory.getLogger(AppResourceController.class);

	@Autowired
	AppresourceService appservice;	
	
	@Autowired
	RallyService rallyservice;	
	
	@Autowired
	private Properties utilProp;
	
	 @Value("${app.env}")
	 private String environment;

	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcAll, method = RequestMethod.GET)
	public @ResponseBody EbiUtilResponseBean getDevAppResrcList(HttpServletResponse response) throws Exception {

		EbiUtilResponseBean oEbiUtilResponseBean =  appservice.fetchAllAppresourceValues();
		processEbiResponse(oEbiUtilResponseBean, response);
		return oEbiUtilResponseBean;
	}

	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcDirectUpdateProduction, method = RequestMethod.POST)
	public @ResponseBody EbiUtilResponseBean updateRecords(@RequestBody List<AppResourceBean> rsrcs) throws Exception {
		if(rsrcs!=null)
		{
			logger.info(rsrcs.toString());
		}
		return appservice.addAppResourceRow(rsrcs,getUserName());	
	}	


	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcPromoteDevToProd, method = RequestMethod.POST)
	public @ResponseBody EbiUtilResponseBean updateProdRecords(@RequestBody List<AppResourceBean> rsrcs) throws Exception {
		if(rsrcs!=null)
		{
			logger.info(rsrcs.toString());
		}
		String userName = getUserName();
		return appservice.addAppResourceProdRow(rsrcs,userName);	
	}
	
	
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcSaveReconcileProd, method = RequestMethod.POST)
	public @ResponseBody EbiUtilResponseBean saveReconcileToProd(@RequestBody List<AppResourceBean> rsrcs) throws Exception {
		if(rsrcs!=null)
		{
			logger.info(rsrcs.toString());
		}
		String userName = getUserName();
		return appservice.saveReconcileToProd(rsrcs,userName);		
	}

	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcDevUpdate, method = RequestMethod.POST)
	public @ResponseBody EbiUtilResponseBean updateDevRecords(@RequestBody List<AppResourceBean> rsrcs,HttpServletResponse response) throws Exception {
		EbiUtilResponseBean oEbiUtilResponseBean=null;
		if(rsrcs!=null)
		{
			logger.info(rsrcs.toString());
			String userName = getUserName();
			oEbiUtilResponseBean =  appservice.addAppResourceDevRow(rsrcs,userName);	
			processEbiResponse(oEbiUtilResponseBean, response);
			
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
			oEbiUtilResponseBean= new EbiUtilResponseBean();
			oEbiUtilResponseBean.setStatus(false);
			oEbiUtilResponseBean.getErrorList().add("Invalid request parameter");			
		}
		return oEbiUtilResponseBean;
	}
	
	

	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFilter, method = RequestMethod.POST)
	public @ResponseBody EbiUtilResponseBean getFilteredDevAppResrcList(@RequestBody String[] filters,HttpServletResponse response) throws Exception {
		logger.info("getFilteredDevAppResrcList :-"+filters);

		EbiUtilResponseBean oEbiUtilResponseBean =  appservice.fetchFilteredAppresourceValuesFromDev(filters);
		processEbiResponse(oEbiUtilResponseBean, response);
		return oEbiUtilResponseBean;
	}

	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcDeleteRsrcFromDev, method = RequestMethod.POST)
	public @ResponseBody boolean getFilteredDevAppResrcList(@RequestBody AppResourceBean appResourceBean) throws Exception {
		logger.info(appResourceBean.toString());
		return appservice.deleteAppResourceFromDev(appResourceBean);

	}

	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFetchFiltersFromDev, method = RequestMethod.GET)
	public @ResponseBody EbiUtilResponseBean getFilteredDevAppResrcList(HttpServletResponse response) throws Exception {
		
		EbiUtilResponseBean oEbiUtilResponseBean =  appservice.fetchAllFeaturesFromDevTable();
		processEbiResponse(oEbiUtilResponseBean, response);
		return oEbiUtilResponseBean;

	}

	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcGenerateFilter, method = RequestMethod.GET)
	public void  generateAppResourceProperties(@RequestParam String filters,@RequestParam String type,@RequestParam String keys,HttpServletResponse response) throws Exception {
		logger.info("getFilteredDevAppResrcList :-"+filters);
		filters = URLDecoder.decode(filters);
		filters = filters.replace("=","");
		keys = URLDecoder.decode(keys);
		String[] arrKey = keys.split("\\|\\|");
		List<AppResourceBean> alAppResourceBean = new ArrayList<>();
		for (int i = 1; i < arrKey.length; i=i+2) {
			AppResourceBean oAppResourceBean = new AppResourceBean();
			oAppResourceBean.setKey(arrKey[i]);
			oAppResourceBean.setEnglishval(arrKey[i+1]);
			oAppResourceBean.setSpanishval(arrKey[i+1]);
			oAppResourceBean.setSource("CUSTOM_KEYS");
			oAppResourceBean.setDev(true);
			alAppResourceBean.add(oAppResourceBean);
		}
		List<StringBuilder> alsbl = appservice.generatePropertiesFile(filters,alAppResourceBean,null);
		if("zip".equals(type))
		{
			createZipFileForDev(alsbl, response);
		}
		else{
			createFileForDev(alsbl, response, type);
		}
		

	}
	
	private void createFileForDev(List<StringBuilder> alsbl,HttpServletResponse response,String languageFile)
	{
		ServletOutputStream  sout = null;	
		response.setContentType("text/plain;charset=UTF-8");
		String engFileName = utilProp.getProperty("property.english.file");
		String spaFileName = utilProp.getProperty("property.spanish.file");
		String engheader="attachment; filename=\""+engFileName+"\"";
		String spanishheader="attachment; filename=\""+spaFileName+"\"";
		String fileString = null;
		try 
		{
			sout=response.getOutputStream();	
			if("english".equalsIgnoreCase(languageFile)){
				response.setHeader("Content-Disposition", engheader);
				fileString=alsbl.get(0).toString();
			}
			else if("spanish".equalsIgnoreCase(languageFile)){
				response.setHeader("Content-Disposition", spanishheader);
				fileString=alsbl.get(1).toString();
			}
			else throw new Exception("Invalid language parameter");
			byte[] stringByte = fileString.getBytes();			
			sout.write(stringByte);
		} 
		catch (Exception e) 
		{
			logger.error("Error in downloading file for  language="+languageFile,e);
		}
		finally 
		{
			try {
				sout.flush();
				sout.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	
	}
	
	private void createZipFileForDev(List<StringBuilder> alsbl,HttpServletResponse response)
	{
		InputStream input = null;
		OutputStream output = null;
		File tempEnglish=null;
		File tempSpanish=null;
		try 
		{
			String zipFileName = utilProp.getProperty(EBIToolConstants.APPRESOURCE_FILE_NAMES.PROPERTY_ZIP_FILE);
			String engFileName = utilProp.getProperty(EBIToolConstants.APPRESOURCE_FILE_NAMES.PROPERTY_ENG_FILE);
			String spaFileName = utilProp.getProperty(EBIToolConstants.APPRESOURCE_FILE_NAMES.PROPERTY_SPA_FILE);
			
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", String.format(EBIToolConstants.APPRESOURCE_FILE_NAMES.HEADER_ZIP_ATTACHMENT, zipFileName));
			tempEnglish = File.createTempFile("Appresource_en", ".properties");
			BufferedWriter out = new BufferedWriter(new FileWriter(tempEnglish));
			out.write(alsbl.get(0).toString());
			out.close();

			tempSpanish = File.createTempFile("Appresource_es", ".properties");
			out = new BufferedWriter(new FileWriter(tempSpanish));
			out.write(alsbl.get(1).toString());
			out.close();

			
			byte[] buffer = new byte[512];
			

			output = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
			input = new FileInputStream(tempEnglish);
			((ZipOutputStream)output).putNextEntry(new ZipEntry(engFileName));
			for (int length = 0; (length = input.read(buffer)) > 0;)
			{
				output.write(buffer, 0, length);
			}
			input.close();
			input=null;
			input = new FileInputStream(tempSpanish);
			((ZipOutputStream)output).putNextEntry(new ZipEntry(spaFileName));
			for (int length = 0; (length = input.read(buffer)) > 0;)
			{
				output.write(buffer, 0, length);
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(),e);
		}
		finally
		{
			try {
				tempEnglish.delete();
				tempSpanish.delete();
				input.close();
				((ZipOutputStream)output).closeEntry();
				output.flush();output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			}
		}
	}

	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcGenerateAudit, method = RequestMethod.POST)
	public @ResponseBody EbiUtilResponseBean getDevAppResrcAudit(@RequestBody AppResourceBean appResourceBean) throws Exception {
		return appservice.fetchAllAppresourceAudit(appResourceBean);
	}


	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcBatchUpdate, method = RequestMethod.GET)
	public @ResponseBody EbiUtilResponseBean getDevAppResrcAudit() throws Exception {
		return appservice.loadAppresourceInDB();
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcPublish, method = RequestMethod.POST)
	public @ResponseBody EbiUtilResponseBean publishAppResourceProd(@RequestBody AppResourceBean oAppResourceBean,HttpServletResponse response) throws Exception {
		EbiUtilResponseBean oEbiUtilResponseBean =  appservice.publishProd(getUserName(),oAppResourceBean);
		return processEbiResponse(oEbiUtilResponseBean, response);
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcPublishReport, method = RequestMethod.GET)
	public @ResponseBody EbiUtilResponseBean publishAppResourceProdReport(HttpServletResponse response) throws Exception {
		EbiUtilResponseBean oEbiUtilResponseBean =   appservice.fetchProdReport();
		return processEbiResponse(oEbiUtilResponseBean, response);
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFetchRallyFeatures, method = RequestMethod.GET)
	public @ResponseBody EbiUtilResponseBean fetchRallyFeatures(HttpServletResponse response) throws Exception {
		EbiUtilResponseBean oEbiUtilResponseBean =   rallyservice.rallyFetchFeatures();
		return processEbiResponse(oEbiUtilResponseBean, response);
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFetchRallyMilestone, method = RequestMethod.GET)
	public @ResponseBody EbiUtilResponseBean fetchRallyMilestones(HttpServletResponse response) throws Exception {
		EbiUtilResponseBean oEbiUtilResponseBean =   rallyservice.rallyFetchMileStones();
		return processEbiResponse(oEbiUtilResponseBean, response);
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFetchRallyMilestoneArtifacts, method = RequestMethod.POST)
	public @ResponseBody EbiUtilResponseBean fetchRallyMilestonesArtifacts(@RequestBody AppResourceBean appResrc , HttpServletResponse response) throws Exception {
		EbiUtilResponseBean oEbiUtilResponseBean =   rallyservice.rallyFetchMileStonesArtifacts(appResrc.getRefUrl());
		return processEbiResponse(oEbiUtilResponseBean, response);
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFetchRallyDefects, method = RequestMethod.GET)
	public @ResponseBody EbiUtilResponseBean fetchRallyDefects(HttpServletResponse response) throws Exception {
		EbiUtilResponseBean oEbiUtilResponseBean =   rallyservice.rallyFetchDefects();
		return processEbiResponse(oEbiUtilResponseBean, response);
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcDownloadPublishReport, method = RequestMethod.GET)
	public void downloadAppResourceProdReport(@RequestParam("publishKey")  String publishKey,@RequestParam("languageFile") String languageFile,HttpServletResponse response) throws Exception 
	{
		ServletOutputStream  sout = response.getOutputStream();		
		response.setContentType("text/plain;charset=UTF-8");
		String engFileName = utilProp.getProperty("property.english.file");
		String spaFileName = utilProp.getProperty("property.spanish.file");
		String engheader="attachment; filename=\""+engFileName+"\"";
		String spanishheader="attachment; filename=\""+spaFileName+"\"";
		try 
		{
			EbiUtilResponseBean oEbiUtilResponseBean =   appservice.fetchPublishedProdFile(publishKey,languageFile);
			if("ENGLISH_FILE".equalsIgnoreCase(languageFile))
				response.setHeader("Content-Disposition", engheader);
			else if("SPANISH_FILE".equalsIgnoreCase(languageFile))
				response.setHeader("Content-Disposition", spanishheader);
			else throw new Exception("Invalid language parameter");
			
			Map<String,Object> omap = oEbiUtilResponseBean.getAlListOfMaps().get(0);
			String fileString = (String)omap.get(languageFile);			
			byte[] stringByte = fileString.getBytes();			
			sout.write(stringByte);
		} 
		catch (Exception e) 
		{
			logger.error("Error in downloading file for key="+publishKey+" language="+languageFile,e);
		}
		finally 
		{
			sout.flush();
			sout.close();
		}
	}

	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFileDiff, method = RequestMethod.POST)
	public @ResponseBody EbiUtilResponseBean getFileDiff(@RequestBody FileDiffRequest  fileDiff,HttpServletResponse response) throws Exception {
		EbiUtilResponseBean oEbiUtilResponseBean=  new EbiUtilResponseBean();
		EbiUtilResponseBean oEbiUtilResponseBean1 =   appservice.fetchPublishedProdFile(fileDiff.getFilePublishId()[0],fileDiff.getLanguage());
		Map<String,Object> omap = oEbiUtilResponseBean1.getAlListOfMaps().get(0);
		String firstFileString = (String)omap.get(fileDiff.getLanguage());	
		oEbiUtilResponseBean.getFileStringDiff()[0]=firstFileString;
		oEbiUtilResponseBean1 =   appservice.fetchPublishedProdFile(fileDiff.getFilePublishId()[1],fileDiff.getLanguage());
		omap = oEbiUtilResponseBean1.getAlListOfMaps().get(0);
		String secondFileString = (String)omap.get(fileDiff.getLanguage());	
		oEbiUtilResponseBean.getFileStringDiff()[1]=secondFileString;
		logger.info(firstFileString);
		logger.info(secondFileString);
		return oEbiUtilResponseBean;
	}
	
	
	private EbiUtilResponseBean processEbiResponse(EbiUtilResponseBean oEbiUtilResponseBean,HttpServletResponse response)
	{
		if(!oEbiUtilResponseBean.getErrorList().isEmpty())
		{
			response.setStatus( HttpServletResponse.SC_EXPECTATION_FAILED);
		}
		return oEbiUtilResponseBean;
	}
	
	private String getUserName() throws Exception
	{
		try 
		{
			UserDetails user = null;
			if(AppresourceUtil.isProduction(environment))
			{
				user = (LdapUserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			}
			else
			{
				user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			}
			String name = user.getUsername(); //get logged in username
			return name;
		} 
		catch (Exception e) 
		{
			logger.error("User session invalid");
			throw e;
		}
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcReconcileProd,method = RequestMethod.POST)
    public @ResponseBody EbiUtilResponseBean reconcile(MultipartHttpServletRequest request,HttpServletResponse response) throws IOException 
	{
		// Getting uploaded files from the request object
		Map<String, MultipartFile> fileMap = request.getFileMap();
		EbiUtilResponseBean oEbiUtilResponseBean = appservice.reconcileProperties(fileMap);
		return oEbiUtilResponseBean;
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFetchPublishStatus,method = RequestMethod.GET)
    public @ResponseBody EbiUtilResponseBean fetchPublicStatus(HttpServletResponse response) throws IOException 
	{
		// Getting uploaded files from the request object
		EbiUtilResponseBean oEbiUtilResponseBean = appservice.fetchPublishStats();
		return processEbiResponse(oEbiUtilResponseBean, response);
		
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFetchCustomKeysList, method = RequestMethod.GET)
    public @ResponseBody EbiUtilResponseBean fetchCustomKeys(HttpServletResponse response) throws IOException 
	{
		// Getting uploaded files from the request object
		EbiUtilResponseBean oEbiUtilResponseBean = appservice.fetchCustomKeysProperties();
		return processEbiResponse(oEbiUtilResponseBean, response);
		
	}
	
	@RequestMapping(value = AppResourceUtilUriConstants.AppRsrcExportToExcel, method = RequestMethod.GET)
    public void exportToexcel(String filename,HttpServletRequest request,HttpServletResponse response) throws IOException 
	{
		 HttpSession ses = request.getSession(true);
         String excelString = (String) ses.getAttribute("excel");
         ses.removeAttribute("excel");
		// Getting uploaded files from the request object
		ServletOutputStream  sout = response.getOutputStream();		
		response.setContentType("text/plain");
		response.setContentType("text/plain");
        
        response.setHeader("Content-Disposition",
                "attachment;filename=AppResourceDevExcel.xls");
        
		try 
		{
		    byte[] stringByte = excelString.getBytes(Charset.forName("UTF-8"));	
			response.setContentLength(stringByte.length);
			sout.write(stringByte);
		} 
		catch (Exception e) 
		{
			logger.error("Error in downloading excel file ",e);
		}
		finally 
		{
			sout.flush();
			sout.close();
		}

	}
	
	 @RequestMapping(value = AppResourceUtilUriConstants.AppRsrcExportToExcel, method = RequestMethod.POST)
	    public @ResponseBody
	    String excel(String excel, String extension, HttpServletRequest request) throws IOException {
	        String filename="";
	        if (extension.equals("csv") || extension.equals("xml")) {
	            filename = "AppResourceDevExcel." + extension;
	            HttpSession ses = request.getSession(true);
	            ses.setAttribute("excel", excel);
	        } 
	        return filename;
	    }
	
	 @RequestMapping(value = AppResourceUtilUriConstants.AppRsrcUpdateCurrentMlStone, method = RequestMethod.POST)
	    public @ResponseBody EbiUtilResponseBean updateCurrentMlStoneInfo(@RequestBody MiscPropertiesBean mpb, HttpServletResponse response) throws Exception  
		{
			// Getting uploaded files from the request object
		 	mpb.setChangedBy(getUserName());
			EbiUtilResponseBean oEbiUtilResponseBean = appservice.updateCurrentMarkedMileStone(mpb);
			return processEbiResponse(oEbiUtilResponseBean, response);			
		}
	 
	 @RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFetchCurrentMlStone, method = RequestMethod.GET)
	    public @ResponseBody EbiUtilResponseBean fetchCurrentSavedMlStone(HttpServletResponse response) throws IOException 
		{
			// Getting uploaded files from the request object
			EbiUtilResponseBean oEbiUtilResponseBean = appservice.fetchCurrentMarkedMileStone();
			return processEbiResponse(oEbiUtilResponseBean, response);
			
		}
	 
	 @RequestMapping(value = AppResourceUtilUriConstants.AppRsrcFetchCurrentMlStoneFiles, method = RequestMethod.GET)
	 public void  fetchCurrentMlStoneFilesWithStorage(@RequestParam String langType,@RequestParam String releaseId, HttpServletResponse response) throws Exception  
	 {
		 try {
			List<StringBuilder> alsbl = appservice.fetchCurrentMlStoneFilesWithStorage(getUserName(),releaseId);
			 
			 if("zip".equals(langType))
			 {
				 createZipFileForDev(alsbl, response);
			 }
			 else
			 {
				 createFileForDev(alsbl, response, langType);
			 }
		} catch (Exception e) 
		 {
			// TODO Auto-generated catch block
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
		}	
	 }	
	 
	 
	 /**
 	 * Fetch release file by release id.
 	 * rest/rsrc/selfcare/release/file/{buildId}/{langType}
 	 * @param releaseId the release id
 	 * @param langType the lang type "english","spanish","zip"
 	 * @param response the response
	 * @throws IOException 
 	 * @throws Exception the exception
 	 */
 	@RequestMapping(value = AppResourceUtilUriConstants.FetchSelfcareReleaseFiles, method = RequestMethod.GET)	 
	 public void  fetchReleaseFileByReleaseId(@PathVariable("buildId") String buildId,@PathVariable("langType") String langType,HttpServletResponse response) throws IOException 
 	{
 		try 
 		{
 			SwReleaseBean srb = appservice.fetchReleaseFileByReleaseId(buildId);
 			List<StringBuilder> alsbl = new ArrayList<>();
 			alsbl.add(new StringBuilder(srb.getEnglishval()));
 			alsbl.add(new StringBuilder(srb.getSpanishval()));
 			if ("zip".equals(langType)) {
 				createZipFileForDev(alsbl, response);
 			} else {
 				createFileForDev(alsbl, response, langType);
 			} 	
 		}
 		catch (Exception e) 
 		{ 			
 			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());

 		}	
 	}	
	 
	 
	 
	 @RequestMapping(value = AppResourceUtilUriConstants.SelfcareSWRelease, method = RequestMethod.PUT)
	 public void  updateSCRelease(@RequestBody SwReleaseBean srb, HttpServletResponse response)
	 {
		 EbiUtilResponseBean erb = appservice.updateReleaseVersion(srb);
		 processEbiResponse(erb, response);
	 }
	 
	
}
