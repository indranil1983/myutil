package com.ericsson.spring.util;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ericsson.spring.model.AppResourceBean;

@Component
public class AppresourceUtil 
{
	
	private static Properties utfProp;
	private static final Logger logger = LoggerFactory.getLogger(AppresourceUtil.class);
	
	/**
	 * Replace special character in appresource bean.
	 *
	 * @param oAppResourceBean the o app resource bean
	 */
	public static void replaceSpecialCharacterInAppresourceBean(AppResourceBean oAppResourceBean)
	{
		
		String englishValue=oAppResourceBean.getEnglishval();
		String spanishValue=oAppResourceBean.getSpanishval();	
		
		logger.debug("English text before translation :- "+englishValue);
		logger.debug("Spanish text before translation :- "+spanishValue);
		
		Set<Object> keySet = utfProp.keySet(); 		
		for (Object obj : keySet) 
		{
			String replaceableKey = (String)obj;
			englishValue = englishValue.replaceAll(replaceableKey, utfProp.getProperty(replaceableKey));
			spanishValue = spanishValue.replaceAll(replaceableKey, utfProp.getProperty(replaceableKey));
			
		}		
		oAppResourceBean.setEnglishval(englishValue);
		oAppResourceBean.setSpanishval(spanishValue);
		logger.debug("English text after translation :- "+englishValue);
		logger.debug("Spanish text after translation :- "+spanishValue);
	}
	
	
	public static void cleanUpAppRsrcValue(AppResourceBean oAppResourceBean)
	{		
		String englishValue=oAppResourceBean.getEnglishval();
		String spanishValue=oAppResourceBean.getSpanishval();	
		
		logger.debug("English text before cleanUpAppRsrcValue :- "+englishValue);
		logger.debug("Spanish text before cleanUpAppRsrcValue :- "+spanishValue);
				
		//replace double blank with single
		if(englishValue!=null){
			englishValue = englishValue.replaceAll("\\s{2,}", " ").trim();
		}
		
		if(spanishValue!=null){
			spanishValue = spanishValue.replaceAll("\\s{2,}", " ").trim();
		}
				
		logger.debug("English text after cleanUpAppRsrcValue :- "+englishValue);
		logger.debug("Spanish text after cleanUpAppRsrcValue :- "+spanishValue);
		
		oAppResourceBean.setEnglishval(englishValue);
		oAppResourceBean.setSpanishval(spanishValue);
	}
	
	public static void appendNewLine(StringBuilder strBuild)
	{
		strBuild.append(System.lineSeparator());
	}

	@Autowired
	public void setUtfProp(Properties utfProp) {
		this.utfProp = utfProp;
	}
	
	public static boolean isProduction(String env){
		if(!"DEV".equalsIgnoreCase(env)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static String printMapV2 (Map<?, ?> map) {
	    StringBuilder sb = new StringBuilder(128);
	    sb.append("{");
	    for (Map.Entry<?,?> entry : map.entrySet()) {
	        if (sb.length()>1) {
	            sb.append(", ");
	        }
	        if(!StringUtils.isEmpty(entry.getValue()))
	        {
	        	sb.append("<strong>").append(entry.getKey()).append("</strong>").append(" : ").append(entry.getValue()).append("<br>");
	        }
	        
	    }
	    sb.append("}");
	    return sb.toString();
	}
}
