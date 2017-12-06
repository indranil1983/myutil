package com.ericsson.spring.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ericsson.spring.model.EbiUtilResponseBean;
import com.ericsson.spring.model.FlowDocBeanChat;
import com.ericsson.spring.model.flowdoc.Author;
import com.ericsson.spring.model.flowdoc.Field;
import com.ericsson.spring.model.flowdoc.FlowDocBean;
import com.ericsson.spring.model.flowdoc.Status;
import com.ericsson.spring.model.flowdoc.Thread;
import com.ericsson.spring.util.AppresourceUtil;

@Service
public class RallyService 
{
	private static final Logger logger = LoggerFactory.getLogger(RallyService.class);
	@Autowired
	private Properties utilProp;

	//Rally URLS
	private static String RALLY_GET_FEATURES=null;//"https://rally1.rallydev.com/slm/webservice/v2.0/portfolioitem/feature?workspace=https://rally1.rallydev.com/slm/webservice/v2.0/workspace/50775741420&query=(State.Name%20%3D%20%22Implementing%22)&fetch=FormattedID&start=1&pagesize=60";
	private static String RALLY_GET_MILESTONES=null;
	private static String RALLY_GET_MILESTONES_ARTIFACTS=null;
	private static String RALLY_GET_DEFECTS=null;
	private static String RALLY_KEY=null;//"Basic Y2lfdXNlckBlcmljc3Nvbi5jb206Y2lfdXNlcjAx";
	private static int RALLY_CONN_TIMEOUT;
	private static String RALLY_CONN_PROXY;
	private static int RALLY_CONN_PORT;
	
	private static String FLOWDOC_APPRESOURCE_CHAT_URL=null;
	private static String FLOWDOC_APPRESOURCE_TEAMINBOX_URL=null;
	
	private static String FLOWDOC_MESSAGE=null;
	
	@PostConstruct
	public void init() 
	{		
		//RALLY
		RALLY_GET_FEATURES=utilProp.getProperty("getFeatures");
		RALLY_GET_MILESTONES=utilProp.getProperty("getMilestones");
		RALLY_GET_MILESTONES_ARTIFACTS=utilProp.getProperty("getMilestoneArtifacts");
		RALLY_GET_DEFECTS=utilProp.getProperty("getDefects");
		RALLY_KEY="Basic "+utilProp.getProperty("rally_key");
		RALLY_CONN_TIMEOUT=new Integer(utilProp.getProperty("rally_conn_timeout"));
		RALLY_CONN_PROXY=utilProp.getProperty("http.proxy.ip");
		if(utilProp.getProperty("http.port")!=null && !utilProp.getProperty("http.port").isEmpty()){
			RALLY_CONN_PORT=new Integer(utilProp.getProperty("http.port"));
		}			
		
		FLOWDOC_APPRESOURCE_CHAT_URL = utilProp.getProperty("flowdoc.appresource.chat.url");
		FLOWDOC_APPRESOURCE_TEAMINBOX_URL=utilProp.getProperty("flowdoc.appresource.inbox.url");
		FLOWDOC_MESSAGE=utilProp.getProperty("flowDocMessage");
		
		logger.info("AppresourceService initialized = "+RALLY_GET_FEATURES+RALLY_KEY);

	}
	
	private EbiUtilResponseBean fetchRallyArtifacts(String rallyUrl){

		EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
		HttpURLConnection conn=null;
		
		try 
		{
			URL url = new URL(rallyUrl);
			if(RALLY_CONN_PROXY!=null && !RALLY_CONN_PROXY.isEmpty() && RALLY_CONN_PORT>0)
			{
				logger.info("RALLY_CONN_Proxy and PORT = "+RALLY_CONN_PROXY+RALLY_CONN_PORT);
				Proxy proxy=null;
				proxy= new Proxy(Proxy.Type.HTTP, new InetSocketAddress(RALLY_CONN_PROXY, RALLY_CONN_PORT));
				conn = (HttpURLConnection) url.openConnection(proxy);
			}
			else
			{
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(RALLY_CONN_TIMEOUT);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", RALLY_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			StringBuilder strBuild = new StringBuilder();
			while ((output = br.readLine()) != null) {
				strBuild.append(output);				
			}

			logger.debug(strBuild.toString());
			oEbiUtilResponseBean.setStatus(true);
			oEbiUtilResponseBean.setRallyResponse(strBuild.toString());
		} 
		catch (MalformedURLException e) 
		{
			logger.error(e.getMessage(),e);
			oEbiUtilResponseBean.getErrorList().add("Malformed Url Exception");
		} 
		catch (IOException e) 
		{
			logger.error(e.getMessage(),e);
			oEbiUtilResponseBean.getErrorList().add(e.getMessage());
		}
		finally 
		{
			if(conn!=null)
				conn.disconnect();
		}		
		return oEbiUtilResponseBean;
	
	}
	
	public EbiUtilResponseBean rallyFetchMileStones()
	{
		return fetchRallyArtifacts(RALLY_GET_MILESTONES);
	}
		
	public EbiUtilResponseBean rallyFetchMileStonesArtifacts(String refUrl) {
		String rallyGetMileStoneArtifacts = RALLY_GET_MILESTONES_ARTIFACTS;
		refUrl=refUrl+rallyGetMileStoneArtifacts;
		return fetchRallyArtifacts(refUrl);
	}
	
	public EbiUtilResponseBean rallyFetchDefects()
	{
		return fetchRallyArtifacts(RALLY_GET_DEFECTS);
	}
	/**
	 * Rally fetch features.
	 *
	 * @return the ebi util response bean
	 */
	public EbiUtilResponseBean rallyFetchFeatures()
	{
		return fetchRallyArtifacts(RALLY_GET_FEATURES);
	}
	
	
	public void postAppresourceProdToFlowDocChat(String tagMessage, int currentFileVersion){
		
		RestTemplate restTemplate =null;		
		if("ON".equalsIgnoreCase((String)utilProp.get("flowdoc.feature.status")))
		{
			if(RALLY_CONN_PROXY!=null && !RALLY_CONN_PROXY.isEmpty() && RALLY_CONN_PORT>0)
			{
				logger.info("RALLY_CONN_Proxy and PORT = "+RALLY_CONN_PROXY+RALLY_CONN_PORT);
				SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
				Proxy proxy=null;
				proxy= new Proxy(Proxy.Type.HTTP, new InetSocketAddress(RALLY_CONN_PROXY, RALLY_CONN_PORT));
				requestFactory.setProxy(proxy);
				restTemplate =  new RestTemplate(requestFactory);
			}
			else
			{
				restTemplate = new RestTemplate();
			}		
			//final String uri = FLOWDOC_URL;
			FlowDocBeanChat objFlowDocBean = new FlowDocBeanChat();
			objFlowDocBean.setEvent("message");
			String flowDocMsg = String.format(utilProp.getProperty("flowdoc.appresource.message"), currentFileVersion);
			objFlowDocBean.setContent(flowDocMsg+"<br>"+tagMessage);
			objFlowDocBean.setExternal_user_name(utilProp.getProperty("flowdoc.sender_name"));
			objFlowDocBean.setSubject(utilProp.getProperty("flowdoc.appresource.subject"));
			objFlowDocBean.setSource(utilProp.getProperty("flowdoc.sender_name"));
			objFlowDocBean.setTags(utilProp.getProperty("flowdoc.appresource.tag"));
			objFlowDocBean.setFrom_address(utilProp.getProperty("flowdoc.sender_address"));
			
			String[] chatUrls = FLOWDOC_APPRESOURCE_CHAT_URL.split(",");
			for (int i = 0; i < chatUrls.length; i++) {
				try {
					restTemplate.postForObject( chatUrls[i], objFlowDocBean, FlowDocBeanChat.class);
					logger.debug("Flowdoc chat message sent");
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}		
		//restTemplate.postForObject( FLOWDOC_APPRESOURCE_TEAMINBOX_URL, objFlowDocBean, FlowDocBeanChat.class);	
	}
	
	
	public void postAppresourceProdToFlowDocInbox(String tagMessage, List<Map<String, Object>> list2, int currentFileVersion){
		
		RestTemplate restTemplate =null;		
		
		if("ON".equalsIgnoreCase((String)utilProp.get("flowdoc.feature.status")))
		{
			if(RALLY_CONN_PROXY!=null && !RALLY_CONN_PROXY.isEmpty() && RALLY_CONN_PORT>0)
			{
				logger.info("RALLY_CONN_Proxy and PORT = "+RALLY_CONN_PROXY+RALLY_CONN_PORT);
				SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
				Proxy proxy=null;
				proxy= new Proxy(Proxy.Type.HTTP, new InetSocketAddress(RALLY_CONN_PROXY, RALLY_CONN_PORT));
				requestFactory.setProxy(proxy);
				restTemplate =  new RestTemplate(requestFactory);
			}
			else
			{
				restTemplate = new RestTemplate();
			}	


			//final String uri = FLOWDOC_URL;
			Author author = new Author();
			author.setEmail("mailto:chuck.norris@jenkins.com");
			author.setName("EBI Utility Tool");
			FlowDocBean objFlowDocBean = new FlowDocBean();
			objFlowDocBean.setAuthor(author);
			objFlowDocBean.setEvent("activity");

			objFlowDocBean.setExternalThreadId("ebiutilid"+currentFileVersion);
			objFlowDocBean.setTitle("Appresource Properties Published");

			Thread objThrd = new Thread();
			String flowDocMsg = String.format(utilProp.getProperty("flowdoc.appresource.message"), currentFileVersion);
			objThrd.setBody(":information_source: "+flowDocMsg+"<br>"+tagMessage);
			objThrd.setExternalUrl(utilProp.getProperty("flowdoc.appresource.ext.url"));
			objThrd.setTitle("Appresource Properties Published");
			Status objStatus = new Status();
			objStatus.setColor("blue");
			objStatus.setValue("AppResKey Changed");
			objThrd.setStatus(objStatus);

			for (Map<String, Object> map : list2) {
				//objThrd.getAdditionalProperties().put((String)map.get("KEY"), (String)map.get("SOURCE"));
				objThrd.getFields().add(new Field("KEY",(String)map.get("KEY")));
				for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
					String str = (String)iterator.next();
					Object objvalue = map.get(str);
					if(objvalue!=null && !str.equalsIgnoreCase("KEY"))
					{
						objThrd.getFields().add(new Field(str,objvalue.toString()));
					}
				}

			}

			objFlowDocBean.setThread(objThrd);

			String[] flowTokens = utilProp.getProperty("flowdoc.appresource.inbox.token").split(",");
			for (int i = 0; i < flowTokens.length; i++)
			{
				try {
					objFlowDocBean.setFlowToken(flowTokens[i]);
					restTemplate.postForObject(FLOWDOC_APPRESOURCE_TEAMINBOX_URL, objFlowDocBean, FlowDocBeanChat.class);
					logger.debug("Flowdoc Inbox message sent:- " + objFlowDocBean);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
		
				

	}

	
			
}
