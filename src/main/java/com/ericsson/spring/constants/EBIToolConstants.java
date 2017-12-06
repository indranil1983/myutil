/*
 * 
 */
package com.ericsson.spring.constants;

/**
 * The Interface EBIToolConstants.
 */
public interface EBIToolConstants {
	
	
	/*public static final String MODIFIED="MODIFIED";
	public static final String NEW="NEW";
	public static final String DELETED="DELETED";*/
	
	public final static class APP_DEV_TABLE
	{		
		
		public static final String KEY="KEY";
		public static final String ENGLISH_VAL="ENGLISH_VAL";
		public static final String SPANISH_VAL="SPANISH_VAL";
		public static final String CHANGED_BY="CHANGED_BY";
		public static final String UPDATED_TIME="UPDATED_TIME";
		public static final String SOURCE="SOURCE";
		public static final String ACTION="ACTION";
		public static final String DESCRIPTION="DESCRIPTION";		
	}
	
	public final static  class APP_PROD_TABLE
	{		
		public static final String OBJECT_NAME="APPRESOURCE_PROPERTIES_PROD";
		public static final String KEY="KEY";
		public static final String ENGLISH_VAL="ENGLISH_VAL";
		public static final String SPANISH_VAL="SPANISH_VAL";
		public static final String CHANGED_BY="CHANGED_BY";
		public static final String UPDATED_TIME="UPDATED_TIME";
		public static final String SOURCE="SOURCE";
		public static final String ACTION="ACTION";
		public static final String DESCRIPTION="DESCRIPTION";		
	}
	
	public final static class APP_AUDIT_TABLE
	{		
		public static final String KEY="KEY";
		public static final String ENGLISH_VAL="ENGLISH_VAL";
		public static final String SPANISH_VAL="SPANISH_VAL";
		public static final String CHANGED_BY="CHANGED_BY";
		public static final String UPDATED_TIME="UPDATED_TIME";
		public static final String SOURCE="SOURCE";
		public static final String ACTION="ACTION";
		public static final String DESCRIPTION="DESCRIPTION";		
	}
	
	public final static class APPRESOURCE_PROPERTIES_PUBLISH
	{
	
		public static final String OBJECT_NAME="APPRESOURCE_PROPERTIES_PUBLISH";
	}
	
	public enum ACTIONS { NEW, MODIFIED, DELETED}  ;
	
	public final static class APPRESOURCE_FILE_NAMES
	{
		public static final String PROPERTY_ENG_FILE="property.english.file";
		public static final String PROPERTY_SPA_FILE="property.spanish.file";
		public static final String PROPERTY_ZIP_FILE="property.zip.file";
		
		public static final String HEADER_ZIP_ATTACHMENT="attachment; filename=\"%s\"";
		
		public static final String PROPERTY_EXT=".properties";
	}

	public final static class USERSESSION_CONSTANTS
	{
		public static final String USER_SESSION_STRING= "USER_SESS";
	}
	
	public final static class RELEASE_INFO
	{		
		public static final String RELEASE_ID="RELEASE_ID";
		public static final String RELEASE_TYPE="RELEASE_TYPE";
		public static final String LINKED_RELEASE_ID="LINKED_RELEASE_ID";
		public static final String APP_NAME="APP_NAME";
		public static final String UPDATED_TIME="UPDATED_TIME";
	}
	
	public final static class RELEASE_INFO_FILE_MAP
	{		
		public static final String RELEASE_ID="RELEASE_ID";
		public static final String ENGLISH_FILE="ENGLISH_FILE";
		public static final String SPANISH_FILE="SPANISH_FILE";
		public static final String UPDATED_TIME="UPDATED_TIME";
		public static final String UPDATED_BY="UPDATED_BY";
	}
	
}
