package com.ericsson.spring.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.hsqldb.lib.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ericsson.spring.constants.EBIToolConstants;
import com.ericsson.spring.dao.AppresourceDao;
import com.ericsson.spring.model.AppResourceBean;
import com.ericsson.spring.model.EbiUtilResponseBean;
import com.ericsson.spring.model.MiscPropertiesBean;
import com.ericsson.spring.model.ReleaseInfoTO;
import com.ericsson.spring.model.SwReleaseBean;
import com.ericsson.spring.model.UploadedFile;
import com.ericsson.spring.util.AppresourceUtil;
import com.fasterxml.jackson.core.JsonParser;

import static com.ericsson.spring.util.AppresourceUtil.appendNewLine;

@Service
public class AppresourceService 
{
	@Autowired
	RallyService rallyService;
	
	@Autowired
	private Properties utilProp;
	
	@Autowired
	private Properties customProp;
	
	private static final Logger logger = LoggerFactory.getLogger(AppresourceService.class);
	private static String SCHEMA_CONSTANT=null;
	private static String APP_RESOURCE_PROD=null;
	private static String APP_RESOURCE_DEV=null;
	private static String APP_RESOURCE_AUDIT=null;
	private static String APPRESOURCE_PROPERTIES_PUBLISH = null;
	private static String MISC_PROPS = null;
	private static String RELEASE_INFO=null;
	
		
	private static final  String FILTER_CLAUSE = "where SOURCE IN (%s) ORDER BY KEY ASC";
	private static final  String PROD_WHERE_CLAUSE = "ORDER BY LOWER(KEY) ASC";	
	private static final  String FETCH_FILTER_SELECT_ClAUSE  = "DISTINCT(SOURCE) as SOURCE";
	private static final String FETCH_FILTER_WHERE_ClAUSE  = "ORDER BY SOURCE ASC";	
	private static  String FETCH_PUBLISH_STATS_WHERE_CLAUSE = null;
	
	private static SimpleDateFormat SDFT = null;
	private static String VERSION_FORMAT = null;
	
	@Autowired
	private PlatformTransactionManager transactionManager;

	
	@PostConstruct
	public void init() 
	{		
		if(null!=utilProp.getProperty("SCHEMA"))
		{
			SCHEMA_CONSTANT=utilProp.getProperty("SCHEMA")+".";
			APP_RESOURCE_PROD=SCHEMA_CONSTANT+"APPRESOURCE_PROPERTIES_PROD";
			APP_RESOURCE_DEV=SCHEMA_CONSTANT+"APPRESOURCE_PROPERTIES";
			APP_RESOURCE_AUDIT=SCHEMA_CONSTANT+"APPRESOURCE_PROPERTIES_AUDIT";
			APPRESOURCE_PROPERTIES_PUBLISH=SCHEMA_CONSTANT+"APPRESOURCE_PROPERTIES_PUBLISH";
			MISC_PROPS = SCHEMA_CONSTANT+"MISC_PROPS";			
			RELEASE_INFO = SCHEMA_CONSTANT+"RELEASE_INFO";
				
			FETCH_PUBLISH_STATS_WHERE_CLAUSE = "where UPDATED_TIME>(Select COALESCE(MAX(TIME),TIMESTAMP ('1970-01-01','20:30:40')) from "+APPRESOURCE_PROPERTIES_PUBLISH+")";
			
			SDFT = new SimpleDateFormat(utilProp.getProperty("version.date.format")); 
			VERSION_FORMAT = utilProp.getProperty("version.format");
			
			
		}
		logger.info("AppresourceService initialized = "+SCHEMA_CONSTANT+APP_RESOURCE_PROD+APP_RESOURCE_DEV+APP_RESOURCE_AUDIT);
		
	}
	
	
	@Autowired
	private AppresourceDao appresourceDao;
	
	public  EbiUtilResponseBean fetchAllAppresourceValues() 
	{
		String dbObjectName = APP_RESOURCE_PROD;
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		try 
		{
			oEbiUtilResponseBean.setAlListOfMaps(appresourceDao.getAllDataFromVUTbl(dbObjectName, null, null)); 
			oEbiUtilResponseBean.setStatus(true);
		} 
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage());
		}
		return oEbiUtilResponseBean;
	}
	
	public  EbiUtilResponseBean fetchAllFeaturesFromDevTable() throws Exception
	{
		String dbObjectName = APP_RESOURCE_DEV;
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		try 
		{
			oEbiUtilResponseBean.setAlListOfMaps(appresourceDao.getAllDataFromVUTbl(dbObjectName, FETCH_FILTER_SELECT_ClAUSE, FETCH_FILTER_WHERE_ClAUSE));
			oEbiUtilResponseBean.setStatus(true);
		} 
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage());
		}
		return oEbiUtilResponseBean;
	}
	
	private String generateFilterClause(String[] filters)
	{
		String whereClause = null;
		if(filters!=null && filters.length>0)
		{
			StringBuilder sb= new StringBuilder();
			String filter = "";
			for(int i=0;i<filters.length;i++){  
				//printval[i];
				sb.append( "'"+filters[i]+"'," );
			}
			filter = sb.toString();
			filter = filter.substring(0, filter.length()-1);
			whereClause =  String.format(FILTER_CLAUSE, filter) ;
		}
		return whereClause;
	}
	
	private Map<String,AppResourceBean> fetchAllPropertiesInAppResrcMap(String filters) throws Exception
	{
		String whereClause = null;		
		Map<String,AppResourceBean> appMap = new LinkedHashMap<String, AppResourceBean>();
		String dbObjectNameProd = APP_RESOURCE_PROD; 
		List<Map<String, Object>> alRows = appresourceDao.getAllDataFromVUTbl(dbObjectNameProd, null, PROD_WHERE_CLAUSE);
		for (Map<String, Object> mapObj : alRows) {
			AppResourceBean appBean = new AppResourceBean();
			appBean.setKey((String)mapObj.get(EBIToolConstants.APP_PROD_TABLE.KEY));
			appBean.setEnglishval((String)mapObj.get(EBIToolConstants.APP_PROD_TABLE.ENGLISH_VAL));
			appBean.setSpanishval((String)mapObj.get(EBIToolConstants.APP_PROD_TABLE.SPANISH_VAL));
			appMap.put(appBean.getKey(), appBean);
		}
		
		if(!StringUtils.isEmpty(filters))
		{
			String[] filterArr = filters.split(",");
			whereClause = generateFilterClause(filterArr);
			String dbObject = APP_RESOURCE_DEV;
			alRows = appresourceDao.getAllDataFromVUTbl(dbObject, null, whereClause);
			for (Map<String, Object> mapObj : alRows) {
				AppResourceBean appBean = new AppResourceBean();
				appBean.setDev(true);
				String action = (String)mapObj.get(EBIToolConstants.APP_DEV_TABLE.ACTION);
				appBean.setKey((String)mapObj.get(EBIToolConstants.APP_DEV_TABLE.KEY));
				appBean.setEnglishval((String)mapObj.get(EBIToolConstants.APP_DEV_TABLE.ENGLISH_VAL));
				appBean.setSpanishval((String)mapObj.get(EBIToolConstants.APP_DEV_TABLE.SPANISH_VAL));			
				appBean.setSource((String)mapObj.get(EBIToolConstants.APP_DEV_TABLE.SOURCE));
				appMap.remove(appBean.getKey());
				if(!EBIToolConstants.ACTIONS.DELETED.toString().equalsIgnoreCase(action))
				{
					appMap.put(appBean.getKey(), appBean);
				}

			}
		}
		
		return appMap;
	}
	
	/**
	 * Fetch current ml stone name with url.
	 *
	 * @return the string
	 */
	private String fetchCurrentMlStoneName()
	{
		String currMlStone=null;
		try
		{
			List<Map<String, Object>> alMap = appresourceDao.getAllDataFromVUTbl(MISC_PROPS,null,"WHERE ACTIVE_FLAG=\'Y\' AND PROP_TYPE=\'CURRENT_MILESTONE\'");
			if(alMap!=null && alMap.size()>0){
				Map<String, Object> mapObj = alMap.get(0);
				currMlStone = (String)mapObj.get("PROP_KEY");
			}
		}
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			//oEbiUtilResponseBean.getErrorList().add(th.getMessage());
		}	
		return currMlStone;
	}
	
	private String generateFilterListFromCurrMileStone()
	{
		StringBuilder filterBuilder = new StringBuilder();
		String filter = null;
		Map<String,AppResourceBean> appMap = null;
		try
		{
			List<Map<String, Object>> alMap = appresourceDao.getAllDataFromVUTbl(MISC_PROPS,null,"WHERE ACTIVE_FLAG=\'Y\' AND PROP_TYPE=\'CURRENT_MILESTONE\'");
			if(alMap!=null && alMap.size()>0){
				Map<String, Object> mapObj = alMap.get(0);
				String mlStoneUrl = (String)mapObj.get("PROP_VAL");
				EbiUtilResponseBean oEbiUtilResponseBean= rallyService.rallyFetchMileStonesArtifacts(mlStoneUrl);
				if(oEbiUtilResponseBean!=null){
					String rallyResponse = oEbiUtilResponseBean.getRallyResponse();
					JSONObject jsonObj = new JSONObject(rallyResponse);
					JSONArray arrList = (JSONArray)((JSONObject)jsonObj.get("QueryResult")).get("Results");
					logger.debug("generatePropertiesFileFromCurrMileStone Rally Response "+jsonObj.toString());
					for (int i = 0; i < arrList.length(); i++) {
						JSONObject jsonResultObj = arrList.getJSONObject(i);
						String artifactId = (String)jsonResultObj.get("FormattedID");
						if(artifactId!=null) {
							filterBuilder.append(artifactId);
							if(i!=(arrList.length()-1)){
								filterBuilder.append(",");
							}							
						}						
					}		
					
					filter = filterBuilder.toString();
					if(filter!=null && !filter.isEmpty())
					{
						filter=filter.substring(0, filter.length()-1);
					}					
				}
			}			
		}
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			//oEbiUtilResponseBean.getErrorList().add(th.getMessage());
		}	
		return filter;
	}
	
	public List<StringBuilder> generatePropertiesFile(String filters,List<AppResourceBean> alAppResourceBean, String tagMessage) throws Exception
	{
		
		String versionTimeStamp = "";
		if(!StringUtil.isEmpty(tagMessage)) //tells it is for tagging
		{
			//This is for production file tagging.
			versionTimeStamp = generateVersionFormatForTagging();		
		}
		else{
			//For development 
			versionTimeStamp = generateVersionFormatForDev(filters);
		}
		
		Map<String,AppResourceBean> appMap = fetchAllPropertiesInAppResrcMap(filters);	
		if(alAppResourceBean!=null)
		{
			for (AppResourceBean appResourceBean : alAppResourceBean) {
				appMap.put(appResourceBean.getKey(), appResourceBean);
			}
		}
		
		StringBuilder strbuilEnglish =  new StringBuilder(versionTimeStamp);
		if(!StringUtil.isEmpty(tagMessage))
		{
			AppresourceUtil.appendNewLine(strbuilEnglish);
			strbuilEnglish.append("###"+tagMessage+"### ");
			AppresourceUtil.appendNewLine(strbuilEnglish);
		}
		
		StringBuilder strbuilSpanish =  new StringBuilder(versionTimeStamp);
		if(!StringUtil.isEmpty(tagMessage))
		{
			AppresourceUtil.appendNewLine(strbuilSpanish);
			strbuilSpanish.append("###"+tagMessage+"###");
			AppresourceUtil.appendNewLine(strbuilSpanish);
		}
		Set<String> keySet = appMap.keySet();
		String tempSource=null;
		for (String keyString : keySet) 
		{			
			AppResourceBean app = appMap.get(keyString);
			String currSource = app.getSource();
			if(app.isDev())
			{
				if(tempSource==null || !tempSource.equalsIgnoreCase(currSource))
				{ 					
					appendSourceComment(currSource, strbuilEnglish, strbuilSpanish);					
					tempSource = currSource;
				}
			}
			logger.debug(keyString+" "+app.getEnglishval()+" "+app.getSpanishval());
			strbuilEnglish.append(keyString+"="+app.getEnglishval());
			AppresourceUtil.appendNewLine(strbuilEnglish);
			strbuilSpanish.append(keyString+"="+app.getSpanishval());
			AppresourceUtil.appendNewLine(strbuilSpanish);
		}	
	    
	    List<StringBuilder> alSbl = new ArrayList<StringBuilder>();
	    alSbl.add(strbuilEnglish);
	    alSbl.add(strbuilSpanish);
	    
	    return alSbl;    
	   
	}
	
	/**
	 * Generate version format for tagging.
	 *
	 * @return the string
	 */
	private String generateVersionFormatForTagging()
	{
		Date currDate = new Date();
		String date = SDFT.format(currDate);
		String versionTimeStamp = null;
		int currfileVersion = getLatestFileVersion();
		if(currfileVersion==0)
		{
			versionTimeStamp = String.format(VERSION_FORMAT,"1",date);
		}
		else
		{
			versionTimeStamp = String.format(VERSION_FORMAT,String.valueOf(currfileVersion+1),date);
		}
		
		return versionTimeStamp;
	}
	
	private String generateVersionFormatForDev(String filters)
	{
		StringBuilder sb = new StringBuilder();
		appendNewLine(sb);
		sb.append("######Current Production version:-"+String.valueOf(getLatestFileVersion())+"############");
		appendNewLine(sb);
		
		if(!StringUtils.isEmpty(filters))
		{
			sb.append("##############Features and Defects Merged: "+filters+"###########");
			appendNewLine(sb);
		}
		sb.append("###########################END###################");
		appendNewLine(sb);
		
		return sb.toString();
	}
	
	private int getLatestFileVersion(){
		int fileVersion = 0;
		EbiUtilResponseBean oEbiUtilResponseBean =  fetchProdReport();
		List<Map<String, Object>> list = oEbiUtilResponseBean.getAlListOfMaps();
		if(list!=null){
			fileVersion = list.size();
		}
		return fileVersion;		
	}
	
	public void appendSourceComment(String currSource,StringBuilder strbuilEnglish,StringBuilder strbuilSpanish)
	{
		strbuilEnglish.append("\n################"+currSource+"################ \n");
		strbuilSpanish.append("\n################"+currSource+"################ \n");
	}
	
	public  EbiUtilResponseBean fetchFilteredAppresourceValuesFromDev(String[] filters) throws Exception
	{
		String dbObjectName = APP_RESOURCE_DEV;
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		try 
		{
			oEbiUtilResponseBean.setAlListOfMaps(appresourceDao.getAllDataFromVUTbl(dbObjectName, null, generateFilterClause(filters)));
			oEbiUtilResponseBean.setStatus(true);
		} 
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage()+"<br>");
		}
		return oEbiUtilResponseBean;
	}
	
	public  EbiUtilResponseBean addAppResourceRow(List<AppResourceBean> rsrcs,String userName) throws Exception
	{
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		for (AppResourceBean appResourceBean : rsrcs) 
		{
			TransactionDefinition def = new DefaultTransactionDefinition();
		    TransactionStatus transactionStatus = transactionManager.getTransaction(def);
			try
			{				
				if(appResourceBean!=null){
					
					AppresourceUtil.cleanUpAppRsrcValue(appResourceBean);
					AppresourceUtil.replaceSpecialCharacterInAppresourceBean(appResourceBean);
					appResourceBean.setChangedBy(userName);
					appResourceBean.setSource("PROD");
				}				
				if(EBIToolConstants.ACTIONS.MODIFIED.toString().equalsIgnoreCase(appResourceBean.getAction()))
				{
					//appresourceDao.copyFromProdToAudit(appResourceBean);
					appresourceDao.deleteFromAppResourceProd(appResourceBean);
					appresourceDao.insertToAppResourceProd(appResourceBean);
					appresourceDao.insertToAppResourceAudit(appResourceBean);
				}
				else if(EBIToolConstants.ACTIONS.NEW.toString().equalsIgnoreCase(appResourceBean.getAction()))
				{
					appresourceDao.insertToAppResourceProd(appResourceBean);
					appresourceDao.insertToAppResourceAudit(appResourceBean);
				}	
				logger.info("Success "+appResourceBean.getKey()+" "+appResourceBean.getEnglishval()+" "+appResourceBean.getSpanishval()+"<br>");
				transactionManager.commit(transactionStatus);
				oEbiUtilResponseBean.getSuccessList().add("<h3>Successfully updated "+appResourceBean.getKey()+"</h3><div><b><u>English:-</u></b><xmp> "
				+appResourceBean.getEnglishval()+"</xmp><br><br> <b><u>Spanish:-</u> </b><br> <xmp>"+appResourceBean.getSpanishval()+"</xmp><br></div>");
			}
			catch(Exception e)
			{				
				transactionManager.rollback(transactionStatus);
				oEbiUtilResponseBean.getSuccessList().add("Failed "+appResourceBean.getKey()+" description: "+e.getMessage()+"<br>");
				logger.error(e.getMessage(),e);
			}
		}		
		return oEbiUtilResponseBean;
	}
	
	/**
	 * Adds the app resource dev row.
	 *
	 * @param rsrcs the rsrcs
	 * @param userName 
	 * @return the ebi util response bean
	 * @throws Exception the exception
	 */
	public  EbiUtilResponseBean addAppResourceDevRow(List<AppResourceBean> rsrcs, String userName) throws Exception
	{
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean(false);
		boolean status = true;
		String dbObjectName = APP_RESOURCE_DEV;
		String whereClause = null;
		for (AppResourceBean appResourceBean : rsrcs) 
		{
			if(appResourceBean!=null && !"RE_MODFIED".equals(appResourceBean.getAction()))
			{
				try 
				{
					whereClause = "where KEY='"+appResourceBean.getKey()+"' AND SOURCE='"+appResourceBean.getSource()+"'";
					if(checkExistingKey(dbObjectName,whereClause))
					{
						oEbiUtilResponseBean.getErrorList().add("Error occurred for Key="+appResourceBean.getKey()
						+" Source = "+appResourceBean.getSource()
						+" Cause= Duplicate key and source exists");
						status=false;
					}
				} 
				catch (Exception e) {
					oEbiUtilResponseBean.getErrorList().add("Error occurred for Key="+appResourceBean.getKey()
					+" Source = "+appResourceBean.getSource()
					+" Cause= "+e.getMessage());
					status=false;
				}
			}
		}
		if(status)
		{
			for (AppResourceBean appResourceBean : rsrcs) 
			{
				TransactionDefinition def = new DefaultTransactionDefinition();
			    TransactionStatus transactionStatus = transactionManager.getTransaction(def);
				try 
				{
					if(appResourceBean!=null)
					{
						appResourceBean.setChangedBy(userName);
						
						AppresourceUtil.cleanUpAppRsrcValue(appResourceBean);
						AppresourceUtil.replaceSpecialCharacterInAppresourceBean(appResourceBean);
						if(appResourceBean!=null && "RE_MODFIED".equals(appResourceBean.getAction()))
						{
							String source = appResourceBean.getSource();
							if(!StringUtils.isEmpty(appResourceBean.getOld_source()))
							{
								appResourceBean.setSource(appResourceBean.getOld_source());
							}							
							appresourceDao.deleteFromAppResourceDev(appResourceBean);
							appResourceBean.setSource(source);
							appResourceBean.setAction(appResourceBean.getOld_action());
						}
						appresourceDao.insertToAppResourceDev(appResourceBean);
						transactionManager.commit(transactionStatus);
						oEbiUtilResponseBean.getSuccessList().add("<h3>Successfully updated "
								+appResourceBean.getKey()+"</h3><div><b><u>English:-</u></b> "+appResourceBean.getEnglishval()+"<br><br> <b><u>Spanish:-</u> </b><br> <xmp>"+appResourceBean.getSpanishval()+"</xmp><br></div>");

					}
					
				}
				catch (Exception e) {
					transactionManager.rollback(transactionStatus);
					oEbiUtilResponseBean.getErrorList().add("Error occurred for Key="+appResourceBean.getKey()
					+" Source = "+appResourceBean.getSource()
					+" Cause= "+e.getMessage()+"<br>");
					status=false;
				}
			}
		}		
		
		oEbiUtilResponseBean.setStatus(status);
		return oEbiUtilResponseBean;
	}
	
	public EbiUtilResponseBean addAppResourceProdRow(List<AppResourceBean> rsrcs,String userName) throws Exception
	{
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		boolean status = false;
		for (AppResourceBean appResourceBean : rsrcs) {
			TransactionDefinition def = new DefaultTransactionDefinition();
		    TransactionStatus transactionStatus = transactionManager.getTransaction(def);
		    try 
		    {
		    	if (EBIToolConstants.ACTIONS.NEW.toString().equalsIgnoreCase(appResourceBean.getAction())) {
		    		appresourceDao.copyFromDevToProd(appResourceBean);
		    		appresourceDao.copyFromDevToAudit(appResourceBean);
		    		appresourceDao.deleteFromAppResourceDev(appResourceBean);
		    		status = true;
		    	} else if (EBIToolConstants.ACTIONS.MODIFIED.toString().equalsIgnoreCase(appResourceBean.getAction())) {
		    		//appresourceDao.copyFromProdToAudit(appResourceBean);
		    		appresourceDao.deleteFromAppResourceProd(appResourceBean);
		    		appresourceDao.copyFromDevToProd(appResourceBean);
		    		appresourceDao.copyFromDevToAudit(appResourceBean);
		    		appresourceDao.deleteFromAppResourceDev(appResourceBean);
		    		status = true;
		    	} else if (EBIToolConstants.ACTIONS.DELETED.toString().equalsIgnoreCase(appResourceBean.getAction())) {
		    		//appresourceDao.copyFromProdToAudit(appResourceBean);
		    		appresourceDao.deleteFromAppResourceProd(appResourceBean);
		    		appresourceDao.copyFromDevToAudit(appResourceBean);
		    		appresourceDao.deleteFromAppResourceDev(appResourceBean);
		    		status = true;
		    	}
		    	appResourceBean.setAction("PROMOTED");
		    	appResourceBean.setChangedBy(userName);
		    	appresourceDao.insertToAppResourceAudit(appResourceBean);
		    	oEbiUtilResponseBean.setStatus(status);
		    	transactionManager.commit(transactionStatus);
		    	oEbiUtilResponseBean.getSuccessList().add("<h3>Successfully promoted "+appResourceBean.getKey()+"</h3><div><b><u>English:-</u></b><xmp> "
		    			+appResourceBean.getEnglishval()+"</xmp><br><br> <b><u>Spanish:-</u> </b><br> <xmp>"+appResourceBean.getSpanishval()+"</xmp><br></div>");
		    } 
		    catch (Exception e) {
		    	transactionManager.rollback(transactionStatus);
		    	oEbiUtilResponseBean.getSuccessList().add("Failed "+appResourceBean.getKey()+" description: "+e.getMessage()+"<br>");
		    }
		}		
		return oEbiUtilResponseBean;
	}
	
		
	public EbiUtilResponseBean saveReconcileToProd(List<AppResourceBean> rsrcs,String userName) throws Exception
	{
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		boolean status = false;
		for (AppResourceBean appResourceBean : rsrcs) {
			TransactionDefinition def = new DefaultTransactionDefinition();
		    TransactionStatus transactionStatus = transactionManager.getTransaction(def);
			try {
				appResourceBean.setChangedBy(userName);
				if (EBIToolConstants.ACTIONS.NEW.toString().equalsIgnoreCase(appResourceBean.getAction())) {
					appresourceDao.insertToAppResourceProd(appResourceBean);
					status = true;
				} else if (EBIToolConstants.ACTIONS.MODIFIED.toString().equalsIgnoreCase(appResourceBean.getAction())) {
					//appresourceDao.copyFromProdToAudit(appResourceBean);
					appresourceDao.deleteFromAppResourceProd(appResourceBean);
					appresourceDao.insertToAppResourceProd(appResourceBean);
					status = true;
				} else if (EBIToolConstants.ACTIONS.DELETED.toString().equalsIgnoreCase(appResourceBean.getAction())) {
					//appresourceDao.copyFromProdToAudit(appResourceBean);
					appresourceDao.deleteFromAppResourceProd(appResourceBean);
					status = true;
				}
				appResourceBean.setAction(appResourceBean.getAction()+"-RECONCILED");
				appResourceBean.setChangedBy(userName);
				appresourceDao.insertToAppResourceAudit(appResourceBean);
				oEbiUtilResponseBean.setStatus(status);
				transactionManager.commit(transactionStatus);
				oEbiUtilResponseBean.getSuccessList().add("Succesfully updated "+appResourceBean.getKey() +
										" English: " +appResourceBean.getEnglishval()+ " Spanish: "+appResourceBean.getSpanishval()+"<br>");
			} catch (Exception e) {
				transactionManager.rollback(transactionStatus);
				oEbiUtilResponseBean.getErrorList().add("Failed "+appResourceBean.getKey()+" description: "+e.getMessage()+"<br>");
			}
		}		
		return oEbiUtilResponseBean;
	}
	
	public boolean deleteAppResourceFromDev(AppResourceBean appResourceBean) throws Exception
	{
		boolean status = false;
		status = appresourceDao.deleteFromAppResourceDev(appResourceBean);
		
		return status;
	}

	public EbiUtilResponseBean fetchAllAppresourceAudit(AppResourceBean appResourceBean) {
		String dbObjectName = APP_RESOURCE_AUDIT;
		String whereClause = "where KEY='"+appResourceBean.getKey()+"' ORDER BY UPDATED_TIME DESC";
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		try 
		{
			oEbiUtilResponseBean.setAlListOfMaps(appresourceDao.getAllDataFromVUTbl(dbObjectName, "KEY,ENGLISH_VAL,SPANISH_VAL,CHANGED_BY,UPDATED_TIME as UPDATED_TIME,SOURCE,ACTION", whereClause));
			oEbiUtilResponseBean.setStatus(true);
		} 
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage()+"<br>");
		}
		return oEbiUtilResponseBean;
	}
	
	private boolean checkExistingKey(String dbObjectName,String whereClause) throws Exception
	{
		boolean isExisting = true;
		try 
		{
			List<Map<String, Object>>  rows =  appresourceDao.getAllDataFromVUTbl(dbObjectName, null, whereClause);
			if(rows==null || rows.isEmpty())
			{
				isExisting = false;
			}
		} 
		catch (Throwable th) 
		{
			throw new Exception(th);
		}
		return isExisting;
	}
	
	public EbiUtilResponseBean loadAppresourceInDB()
	{
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean(false);
		Properties propEng = new Properties();
		Properties propSpa = new Properties();
		InputStream input1 = null;
		InputStream input2 = null;
		Map<String,AppResourceBean> mapApps = null;
		try {

			input1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("AppResources_en.properties");
			input2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("AppResources_es.properties");

			// load a properties file
			propEng.load(input1);
			propSpa.load(input2);
			Enumeration e = propEng.propertyNames();
			mapApps = new HashMap<>();
			while (e.hasMoreElements()) {
			      String key = (String) e.nextElement();
			      String engVal = propEng.getProperty(key);
			      String spaVal = propSpa.getProperty(key);
			      if(engVal==null || spaVal==null)
			      {
			    	  logger.error("Key not in sync "+key);
			    	  oEbiUtilResponseBean.getErrorList().add("Key not in sync "+key);
			      }
			      else
			      {
			    	  /*if(engVal.isEmpty()) engVal="&nbsp;";
			    	  if(spaVal.isEmpty()) spaVal="&nbsp;";
			   */ 	  AppResourceBean apBean = new AppResourceBean();
			    	  apBean.setKey(key);
			    	  apBean.setEnglishval(engVal);
			    	  apBean.setSpanishval(spaVal);
			    	  mapApps.put(key,apBean);
			      }
			}
			if(mapApps!=null)
			{
				ArrayList<AppResourceBean> alApps =  new ArrayList<AppResourceBean>(mapApps.values());
				int[] rows = appresourceDao.insertBatch(alApps);
				oEbiUtilResponseBean.getSuccessList().add(Arrays.toString(rows));
				oEbiUtilResponseBean.setStatus(true);
			}
			
			
		} catch (IOException ex) {
			logger.error("Error reading in file",ex);
			oEbiUtilResponseBean.getErrorList().add(ex.getMessage()+"<br>");
			oEbiUtilResponseBean.setStatus(false);
		} 
		catch(Exception e)
		{
			logger.error("Error Db Update",e);
			oEbiUtilResponseBean.getErrorList().add(e.getMessage());
		}
		finally {
			if (input1 != null) {
				try {
					input1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (input2 != null) {
				try {
					input2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return oEbiUtilResponseBean;
	}
	
	
	public EbiUtilResponseBean publishProd(String userName,AppResourceBean oAppResourceBean) 
	{
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		String tagMessage="New Production File Version comment:- ";
		if(oAppResourceBean!=null && oAppResourceBean.getTagMessage()!=null){
			tagMessage = tagMessage+oAppResourceBean.getTagMessage();
		}
		else{
			tagMessage="No comment provided";
		}
		try 
		{
			int currentFileVersion = getLatestFileVersion();
			List<StringBuilder> alFileStringList = generatePropertiesFile(null,null,tagMessage);
			EbiUtilResponseBean objEbiUtilResponseBean =  fetchPublishStats();
			List<Map<String, Object>> list = objEbiUtilResponseBean.getAlListOfMaps();
			
			appresourceDao.insertToAppResourcePublish(alFileStringList, userName,tagMessage);
			oEbiUtilResponseBean.setStatus(true);
			oEbiUtilResponseBean.getSuccessList().add("Changes published succesfully.<br>");

			//Post to flowDoc
			int newFileVersion = currentFileVersion+1;
			rallyService.postAppresourceProdToFlowDocChat(tagMessage,newFileVersion);
			rallyService.postAppresourceProdToFlowDocInbox(tagMessage,list,newFileVersion);
		} 
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage()+"<br>");
		}
		return oEbiUtilResponseBean;
	}
	
	/**
	 * Fetch prod publish details .
	 *
	 * @return the ebi util response bean
	 */
	public EbiUtilResponseBean fetchProdReport() 
	{
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		String whereClause = "ORDER BY TIME DESC";
		try 
		{
			oEbiUtilResponseBean.setAlListOfMaps(appresourceDao.getAllDataFromVUTbl(APPRESOURCE_PROPERTIES_PUBLISH,"PUBLISHED_BY,TIME,PUBLISH_KEY,PUBLISH_COMMENT",whereClause));
			oEbiUtilResponseBean.setStatus(true);
			
		} 
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage()+"<br>");
		}
		return oEbiUtilResponseBean;
	}
	
	/**
	 * Fetch prod publish details .
	 *
	 * @return the ebi util response bean
	 */
	public EbiUtilResponseBean fetchPublishStats() 
	{
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		
		try 
		{
			
			List<Map<String, Object>> alROw = appresourceDao.getAllDataFromVUTbl(APP_RESOURCE_PROD, null , FETCH_PUBLISH_STATS_WHERE_CLAUSE);
			oEbiUtilResponseBean.setAlListOfMaps(alROw);			
		} 
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage()+"<br>");
		}
		return oEbiUtilResponseBean;
	}
	
	public EbiUtilResponseBean fetchPublishedProdFile(String publishKey,String languageFile) 
	{
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		try 
		{
			if(!("ENGLISH_FILE".equalsIgnoreCase(languageFile) || "SPANISH_FILE".equalsIgnoreCase(languageFile)))
			{
				throw new Exception("Invalid language parameter");
			}
			else
			{
				String whereClause="where PUBLISH_KEY="+publishKey;
				oEbiUtilResponseBean.setAlListOfMaps(appresourceDao.getAllDataFromVUTbl(APPRESOURCE_PROPERTIES_PUBLISH,null,whereClause));
				oEbiUtilResponseBean.setStatus(true);
			}			
		} 
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage()+"<br>");
		}
		return oEbiUtilResponseBean;
	}
		
		
	
	
	public EbiUtilResponseBean reconcileProperties(Map<String, MultipartFile> fileMap)
	{
		EbiUtilResponseBean oEbiUtilResponseBean =  new EbiUtilResponseBean();
		boolean englishFilePresent=false;
		boolean spanishFilePresent= false;
		
		UploadedFile engFile = null;
		UploadedFile spanishFile = null;
		try {
			for (MultipartFile multipartFile : fileMap.values()) {
				if("AppResources_en.properties".equalsIgnoreCase(multipartFile.getOriginalFilename()))
				{
					englishFilePresent=true;
					engFile = getUploadedFileInfo(multipartFile);
				}
				else if("AppResources_es.properties".equalsIgnoreCase(multipartFile.getOriginalFilename()))
				{
					spanishFilePresent=true;
					spanishFile = getUploadedFileInfo(multipartFile);
				}
			}
			
			if(englishFilePresent && spanishFilePresent)
			{
				Map<String, AppResourceBean> mapBean = fetchAllPropertiesInAppResrcMap(null);
				keysDeleted(mapBean,engFile.getPropMap(), spanishFile.getPropMap(),oEbiUtilResponseBean);
				matchNewWithExisting(mapBean, engFile.getPropMap(), spanishFile.getPropMap(), oEbiUtilResponseBean);				
			}
			else
			{
				oEbiUtilResponseBean.setStatus(false);
				oEbiUtilResponseBean.getErrorList().add("Both English and Spanish files are not present. Files should be named as AppResources_en.properties and AppResources_es.properties");
				
			}
		} catch (Exception th) {
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage()+"<br>");
		}
		
		return oEbiUtilResponseBean;
	}
	
	/**
	 * Fetch custom keys properties.
	 *
	 * @return the ebi util response bean
	 */
	public EbiUtilResponseBean fetchCustomKeysProperties()
	{
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		if(customProp!=null)
		{
			Set<String> customKeys = customProp.stringPropertyNames();
			List<AppResourceBean> alAppResourceBean =  new ArrayList<>();
			for (String propName : customKeys) 
			{
				AppResourceBean oAppResourceBean = new AppResourceBean();
				oAppResourceBean.setKey(propName);
				oAppResourceBean.setEnglishval(customProp.getProperty(propName));
				oAppResourceBean.setSpanishval(customProp.getProperty(propName));
				alAppResourceBean.add(oAppResourceBean);

			}
			oEbiUtilResponseBean.setAppRsrcList(alAppResourceBean);
		}
		else 
		{
			oEbiUtilResponseBean.getErrorList().add("Missing file custom properties.");
		}		
		return oEbiUtilResponseBean;
	}
	
	private void matchNewWithExisting(Map<String, AppResourceBean> mapBean, Map<String, String> propMapEng, Map<String, String> propMapSpan, EbiUtilResponseBean oEbiUtilResponseBean) {

		List<AppResourceBean> modifiedList = oEbiUtilResponseBean.getAppRsrcList();
		if(modifiedList==null)
		{
			modifiedList =  new ArrayList<>();
			oEbiUtilResponseBean.setAppRsrcList(modifiedList);
		}
		Set<String> existingKeys = mapBean.keySet();
		Set<String> propKeys = propMapEng.keySet();
		
		for (String newKeys : propKeys) {
			if(existingKeys.contains(newKeys))
			{
				String newEngVal = propMapEng.get(newKeys);
				String newSpaVal = propMapSpan.get(newKeys);
				if(newEngVal==null ||  newSpaVal==null)
				{
					oEbiUtilResponseBean.getErrorList().add("Either English or Spanish value is missing in new file for"+newKeys+"\n ");
					continue;
				}
				
				String oldEngVal = mapBean.get(newKeys).getEnglishval();
				String oldSpaVal = mapBean.get(newKeys).getSpanishval();
				
				if(oldEngVal==null ||  oldSpaVal==null)
				{
					oEbiUtilResponseBean.getErrorList().add("Either English or Spanish value is missing in existing for"+newKeys+"\n ");
					continue;
				}
				
				if(!(newEngVal.equals(oldEngVal) && newSpaVal.equals(oldSpaVal)))
				{
					AppResourceBean oAppResourceBean = new AppResourceBean();
					oAppResourceBean.setKey(newKeys);
					oAppResourceBean.setEnglishval(newEngVal);
					oAppResourceBean.setSpanishval(newSpaVal);
					
					oAppResourceBean.setOldspanishval(oldSpaVal);
					oAppResourceBean.setOldenglishval(oldEngVal);
					
					oAppResourceBean.setAction(EBIToolConstants.ACTIONS.MODIFIED.toString());
					oAppResourceBean.setSource("PROD");
					modifiedList.add(oAppResourceBean);
				}
				
			}
			else
			{
				String newEngVal = propMapEng.get(newKeys);
				String newSpaVal = propMapSpan.get(newKeys);
				if(newEngVal!=null && newSpaVal!=null)
				{
					AppResourceBean oAppResourceBean = new AppResourceBean();
					oAppResourceBean.setKey(newKeys);
					oAppResourceBean.setEnglishval(newEngVal);
					oAppResourceBean.setSpanishval(newSpaVal);
					oAppResourceBean.setAction(EBIToolConstants.ACTIONS.NEW.toString());
					oAppResourceBean.setSource("PROD");
					modifiedList.add(oAppResourceBean);
				}				
				
			}
		}			
	}

	private void keysDeleted(Map<String, AppResourceBean> mapBean, Map<String, String> propMapEng, Map<String, String> propMapSpan,EbiUtilResponseBean oEbiUtilResponseBean)
	{
		List<AppResourceBean>  appList = oEbiUtilResponseBean.getAppRsrcList();
		Set<String> existingKeys = mapBean.keySet();
		Set<String> propKeys = propMapEng.keySet();
		
		for (String existKeys : existingKeys) {
			if(!propKeys.contains(existKeys))
			{
				AppResourceBean oAppResourceBean = new AppResourceBean();
				oAppResourceBean.setAction(EBIToolConstants.ACTIONS.DELETED.toString());
				oAppResourceBean.setKey(existKeys);
				oAppResourceBean.setOldenglishval(mapBean.get(existKeys).getEnglishval());
				oAppResourceBean.setOldspanishval(mapBean.get(existKeys).getSpanishval());
				if(appList==null) {
					appList= new ArrayList<>();
					oEbiUtilResponseBean.setAppRsrcList(appList);
				}
				appList.add(oAppResourceBean);
			}
		}
	}

	private UploadedFile getUploadedFileInfo(MultipartFile multipartFile) throws Exception {

		UploadedFile fileInfo = new UploadedFile();
		fileInfo.setName(multipartFile.getOriginalFilename());
		fileInfo.setSize(multipartFile.getSize());
		fileInfo.setType(multipartFile.getContentType());		
		fileInfo.setContent(IOUtils.toString(multipartFile.getInputStream()));
		fileInfo.setPropMap(readPropertiesFileAsMap(fileInfo.getContent(),"="));
		return fileInfo;
	}
	
	private String getOutputFilename(MultipartFile multipartFile) {

		return multipartFile.getOriginalFilename();
	}
	
	/*private Properties parsePropertiesString(InputStream inputStream) throws IOException {
	    // grr at load() returning void rather than the Properties object
	    // so this takes 3 lines instead of "return new Properties().load(...);"
		
		ResourceBundle bundle = new PropertyResourceBundle(new InputStreamReader(inputStream, "ISO-8859-1"));
	    final Properties p = new Properties();
	    p.load(inputStream);
	    return p;
	}*/

	private Map<String, String> readPropertiesFileAsMap(String s,String delimiter) throws Exception
	{
		Map<String, String> map = new HashMap<>();
		BufferedReader reader = new BufferedReader(new StringReader(s));
		String line;
		try {
			while ((line = reader.readLine()) != null)
			{
				if (line.trim().length()==0) continue;
				if (line.charAt(0)=='#') continue;
				// assumption here is that proper lines are like "String : http://xxx.yyy.zzz/foo/bar",
				// and the ":" is the delimiter
				int delimPosition = line.indexOf(delimiter);
				if(delimPosition==-1) {
					throw new Exception("There is some issue in this line as no key could be found, Please fix it.<br>"+line);
				}
				String key = line.substring(0, delimPosition).trim();
				String value = line.substring(delimPosition+1);
				logger.debug("Reconcile file Key processed "+key);
				map.put(key, value);
			}

			reader.close();
		} catch (Exception e) {
			logger.error("Error in readPropertiesFileAsMap:- "+e.getMessage());
			throw e;
		}
		return map;
	}
	
	public EbiUtilResponseBean fetchCurrentMarkedMileStone(){
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		try
		{
			oEbiUtilResponseBean.setAlListOfMaps(appresourceDao.getAllDataFromVUTbl(MISC_PROPS,null,"WHERE ACTIVE_FLAG=\'Y\' AND PROP_TYPE=\'CURRENT_MILESTONE\'"));
		}
		catch (Throwable th) 
		{
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage());
		}		
		return oEbiUtilResponseBean;
	}
	
	public EbiUtilResponseBean updateCurrentMarkedMileStone(MiscPropertiesBean mpb){
		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		TransactionDefinition def = new DefaultTransactionDefinition();
	    TransactionStatus transactionStatus = transactionManager.getTransaction(def);
		try
		{   
			mpb.setProp_type("CURRENT_MILESTONE");
			mpb.setFlag("Y");
		    appresourceDao.updateAllCurrentMileStoneAsInactive();
		    appresourceDao.insertCurrentMileStoneAsActive(mpb);
		    transactionManager.commit(transactionStatus);
			
		}
		catch (Throwable th) 
		{
			transactionManager.rollback(transactionStatus);
			logger.error(th.getMessage(), th);
			oEbiUtilResponseBean.getErrorList().add(th.getMessage());
		}		
		return oEbiUtilResponseBean;
	}

	public EbiUtilResponseBean updateReleaseVersion(SwReleaseBean srb) {
		boolean status = false;
		EbiUtilResponseBean erb = new EbiUtilResponseBean();
		if(srb!=null && srb.getBuildVersion()!=null && srb.getBuildType()!=null){
			ReleaseInfoTO rto = null;
			if("INTEGRATE".equals(srb.getBuildType())){
				rto = new ReleaseInfoTO();
				rto.setAppName(srb.getAppName());
				rto.setReleaseId(srb.getBuildVersion());
				rto.setReleaseType(srb.getBuildType());
				status=true;
			}
			else if("RELEASE".equals(srb.getBuildType())){
				rto = new ReleaseInfoTO();
				rto.setAppName(srb.getAppName());
				rto.setReleaseId(srb.getBuildVersion());
				rto.setReleaseType(srb.getBuildType());
				status=true;
			}
			
			if(rto!=null)
			{
				try {
					if(!appresourceDao.insertReleaseVersion(rto))
					{
						erb.getErrorList().add("There was an error executing in DB");
					}
				} catch (Exception e) {
					logger.error("An exception has occurred");
					erb.getErrorList().add("An exception has occurred");
				}
			}
			else{
				erb.getErrorList().add("Release type not valid");
			}
		}
		else
		{			
			erb.getErrorList().add("Mandatory parameters missing");
		}				
		if(!status){
			erb.getErrorList().add("Unexpected error occurred");
		}
		if(status){
			erb.getSuccessList().add("Success");
		}
		
		return erb;
	}
	
	public List<StringBuilder> fetchCurrentMlStoneFilesWithStorage(String userName,String releaseId) throws Exception{

		if(releaseId==null)
		{
			throw new Exception("Release id not sent .");
		}
		
		String mlStoneName = fetchCurrentMlStoneName();
		String filters = generateFilterListFromCurrMileStone();
		List<StringBuilder> alsbl = generatePropertiesFile(filters,null,null);
		if(alsbl!=null && alsbl.size()>1)
		{			
			String mlStonePrependString = System.lineSeparator()+"####### MILESTONE NAME: "+mlStoneName+"###########";
			alsbl.get(0).insert(0,mlStonePrependString);			
			String engFile = "####### MILESTONE NAME: "+mlStoneName+"###########"+System.lineSeparator()+alsbl.get(0).toString();
			alsbl.get(1).insert(0,mlStonePrependString);	
			String spanishFile="####### MILESTONE NAME: "+mlStoneName+"###########"+System.lineSeparator()+alsbl.get(1).toString();
			ReleaseInfoTO rto = new ReleaseInfoTO();
			rto.setEnglishval(engFile);
			rto.setSpanishval(spanishFile);
			rto.setUpdatedBy(userName);
			rto.setReleaseId(releaseId);
			TransactionDefinition def = new DefaultTransactionDefinition();
		    TransactionStatus transactionStatus = transactionManager.getTransaction(def);		    
			try {
				appresourceDao.deleteReleaseVersionFileMap(rto);
				appresourceDao.insertReleaseVersionFileMap(rto);
				transactionManager.commit(transactionStatus);
			} catch (Exception e) {
				transactionManager.rollback(transactionStatus);
				throw new Exception("Error while inserting generated files against integrate version id in db");
			}
		}
		else{
			throw new Exception("File generation failed.");
		}
		return alsbl;
	}
	
	
	public SwReleaseBean fetchReleaseFileByReleaseId(String releaseId) throws Exception{
		SwReleaseBean srb = new SwReleaseBean();
		ReleaseInfoTO rto = fetchReleaseInfoByReleaseId(releaseId);
		if("RELEASE".equals(rto.getReleaseType())){
			String linkedReleaseId = rto.getLinkedReleaseId();
			if(linkedReleaseId==null){
				throw new Exception("Corresponding Integrate Sw id not found.");
			}
			rto = appresourceDao.fetchReleaseFileInfoByReleaseId(linkedReleaseId);
			srb.setEnglishval(rto.getEnglishval());
			srb.setSpanishval(rto.getSpanishval());
			
		}	
		if(srb.getEnglishval()==null || srb.getSpanishval()==null){
			throw new Exception("Files for these release not found");
		}
		return srb;
	}
	
	private ReleaseInfoTO fetchReleaseInfoByReleaseId(String releaseId){
		ReleaseInfoTO rto = appresourceDao.fetchReleaseIdInfo(releaseId);		
		return rto;
	}
	
	
	
}
