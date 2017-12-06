package com.ericsson.spring.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ericsson.spring.constants.EBIToolConstants;
import com.ericsson.spring.model.AppResourceBean;
import com.ericsson.spring.model.MiscPropertiesBean;
import com.ericsson.spring.model.ReleaseInfoTO;
import com.ericsson.spring.model.SwReleaseBean;
import com.ericsson.spring.model.UserSession;

import sun.util.logging.resources.logging;


@Repository
public class AppresourceDao 
{
	private static final Logger log = LoggerFactory.getLogger(AppresourceDao.class);
	
	@Autowired
	private Properties utilProp;
	
	@Autowired
	private DataSource datasource;
	
	private JdbcTemplate jdbcTemplate;
	

	
	private static String SCHEMA_CONSTANT=null;
	private static String APP_RESOURCE_PROD=null;
	private static String APP_RESOURCE_DEV=null;
	private static String APP_RESOURCE_AUDIT=null;
	private static String APPRESOURCE_PROPERTIES_PUBLISH = null;
	private static String MISC_PROPS=null;
	private static String USER_SESSION=null;
	
	private static String ADD_TO_DEV_TABLE;
	private static String ADD_TO_PROD_TABLE;
	private static String ADD_TO_AUDIT_TABLE;
	private static String UPDATE_TO_PROD_TABLE;
	private static String DELETE_FROM_DEV;
	private static String DELETE_FROM_PROD;
	private static String PUBLISH_PROPERTIES;
	
	
	

    private static String COPY_TO_AUDIT_TABLE_FROM_PROD;
	private static String COPY_TO_AUDIT_TABLE_FROM_DEV ;
	private static String COPY_TO_PROD_FROM_DEV;
	
	private static String MARK_CURRENT_MLSTONE_INACTIVE;
	private static String INSERT_MISC_PROP_KEYS;
	
	private static String INSERT_USER_SESSION;
	private static String INSERT_RELEASE_INFO_FOR_INTEGRATE;
	private static String INSERT_RELEASE_INFO_FOR_RELEASE;
	
	private static String RELEASE_INFO;
	private static String TBL_RELEASE_INFO_FILE_MAP;
	
	private static String INSERT_INTO_RELEASE_INFO_FILE_MAP;
	private static String DELETE_FROM_RELEASE_INFO_FILE_MAP;
	private static String FETCH_RELEASE_FILE_INFO_BY_RELEASE_ID;
	private static String FETCH_RELEASE_INFO_BY_RELEASE_ID;

	
	@PostConstruct
	public void init() 
	{		
		if(null!=utilProp.getProperty("SCHEMA"))
		{
			SCHEMA_CONSTANT=utilProp.getProperty("SCHEMA")+".";
			APP_RESOURCE_PROD=SCHEMA_CONSTANT+"APPRESOURCE_PROPERTIES_PROD ";
			APP_RESOURCE_DEV=SCHEMA_CONSTANT+"APPRESOURCE_PROPERTIES ";
			APP_RESOURCE_AUDIT=SCHEMA_CONSTANT+"APPRESOURCE_PROPERTIES_AUDIT ";
			APPRESOURCE_PROPERTIES_PUBLISH=SCHEMA_CONSTANT+"APPRESOURCE_PROPERTIES_PUBLISH";
			MISC_PROPS=SCHEMA_CONSTANT+"MISC_PROPS";
			USER_SESSION=SCHEMA_CONSTANT+"USER_SESSION";
			RELEASE_INFO=SCHEMA_CONSTANT+"RELEASE_INFO";
			TBL_RELEASE_INFO_FILE_MAP=SCHEMA_CONSTANT+"RELEASE_INFO_FILE_MAP";
			
			ADD_TO_DEV_TABLE="INSERT INTO "+APP_RESOURCE_DEV+" (KEY,ENGLISH_VAL,SPANISH_VAL,CHANGED_BY,SOURCE,ACTION,UPDATED_TIME) values (?,?,?,?,?,?,CURRENT_TIMESTAMP)";
			ADD_TO_PROD_TABLE="INSERT INTO "+APP_RESOURCE_PROD+" (KEY,ENGLISH_VAL,SPANISH_VAL,CHANGED_BY,SOURCE,ACTION,UPDATED_TIME) values (?,?,?,?,?,?,CURRENT_TIMESTAMP)";
			ADD_TO_AUDIT_TABLE="INSERT INTO "+APP_RESOURCE_AUDIT+" (KEY,ENGLISH_VAL,SPANISH_VAL,CHANGED_BY,SOURCE,ACTION,UPDATED_TIME) values (?,?,?,?,?,?,CURRENT_TIMESTAMP)";
			UPDATE_TO_PROD_TABLE="UPDATE TABLE "+APP_RESOURCE_PROD+" SET ENGLISH_VAL=?,SPANISH_VAL=?,CHANGED_BY=?,SOURCE=?,UPDATED_TIME=CURRENT_TIMESTAMP where KEY=?";
			DELETE_FROM_DEV="DELETE FROM "+APP_RESOURCE_DEV+" WHERE KEY=? AND SOURCE=?";
			DELETE_FROM_PROD="DELETE FROM "+APP_RESOURCE_PROD+" WHERE KEY=?";
			PUBLISH_PROPERTIES="INSERT INTO "+APPRESOURCE_PROPERTIES_PUBLISH+" (PUBLISH_KEY,ENGLISH_FILE,SPANISH_FILE,PUBLISHED_BY,TIME,PUBLISH_COMMENT) values (UNIX_MILLIS(CURRENT_TIMESTAMP),?,?,?,CURRENT_TIMESTAMP,?)";
			
			COPY_TO_AUDIT_TABLE_FROM_PROD = "INSERT INTO "+APP_RESOURCE_AUDIT+"  (KEY,ENGLISH_VAL,SPANISH_VAL,CHANGED_BY,UPDATED_TIME,SOURCE,ACTION) "+
		    		"SELECT KEY,ENGLISH_VAL,SPANISH_VAL,CHANGED_BY,UPDATED_TIME,SOURCE,ACTION FROM "+APP_RESOURCE_PROD+" WHERE KEY=?";
			
			COPY_TO_AUDIT_TABLE_FROM_DEV = "INSERT INTO "+APP_RESOURCE_AUDIT+"  (KEY,ENGLISH_VAL,SPANISH_VAL,CHANGED_BY,UPDATED_TIME,SOURCE,ACTION)"+
					"SELECT KEY,ENGLISH_VAL,SPANISH_VAL,CHANGED_BY,UPDATED_TIME,SOURCE,ACTION FROM "+APP_RESOURCE_DEV+" WHERE KEY=? AND SOURCE=?";
			
			COPY_TO_PROD_FROM_DEV = "INSERT INTO "+APP_RESOURCE_PROD+" (KEY,ENGLISH_VAL,SPANISH_VAL,CHANGED_BY,UPDATED_TIME,SOURCE,ACTION)"+
					"SELECT KEY,ENGLISH_VAL,SPANISH_VAL,CHANGED_BY,CURRENT_TIMESTAMP,SOURCE,ACTION FROM "+APP_RESOURCE_DEV+" WHERE KEY=? AND SOURCE=?";
			
			MARK_CURRENT_MLSTONE_INACTIVE="UPDATE "+MISC_PROPS+" SET ACTIVE_FLAG=\'N\' WHERE PROP_TYPE=\'CURRENT_MILESTONE\'";
			
			INSERT_MISC_PROP_KEYS = "INSERT INTO "+MISC_PROPS+" (PROP_KEY,PROP_TYPE,PROP_VAL,CHANGED_BY,ACTIVE_FLAG,DESCRIPTION,UPDATED_TIME) values (?,?,?,?,?,?,CURRENT_TIMESTAMP)";
			
			INSERT_USER_SESSION ="INSERT INTO "+USER_SESSION+" (USER_ID,SESSION_ID,LOGGED_IN,LOGGED_OUT,UPDATED_TIME) VALUES (?,?,?,?,CURRENT_TIMESTAMP)";
			
			INSERT_RELEASE_INFO_FOR_INTEGRATE="INSERT INTO "+RELEASE_INFO+" (RELEASE_ID,RELEASE_TYPE,APP_NAME,UPDATED_TIME) VALUES (?,?,?,CURRENT_TIMESTAMP)";
			
			INSERT_RELEASE_INFO_FOR_RELEASE="INSERT INTO "+RELEASE_INFO+" (RELEASE_ID,RELEASE_TYPE,LINKED_RELEASE_ID,APP_NAME,UPDATED_TIME) VALUES (?,?,(select release_id from EBIUTIL.release_info where release_type='INTEGRATE' AND updated_time=(select max(updated_time) from EBIUTIL.release_info WHERE release_type='INTEGRATE' )),?,CURRENT_TIMESTAMP)";
			
			INSERT_INTO_RELEASE_INFO_FILE_MAP="INSERT INTO "+TBL_RELEASE_INFO_FILE_MAP+" (RELEASE_ID,ENGLISH_FILE,SPANISH_FILE,UPDATED_TIME,UPDATED_BY) values (?,?,?,CURRENT_TIMESTAMP,?)";
		
			DELETE_FROM_RELEASE_INFO_FILE_MAP="DELETE FROM "+TBL_RELEASE_INFO_FILE_MAP+" WHERE RELEASE_ID=?";
			
			FETCH_RELEASE_FILE_INFO_BY_RELEASE_ID = "SELECT * FROM "+TBL_RELEASE_INFO_FILE_MAP+" WHERE RELEASE_ID=?";
			
			FETCH_RELEASE_INFO_BY_RELEASE_ID= "SELECT * FROM "+RELEASE_INFO+" WHERE RELEASE_ID=?";
		}
		else
		{
			log.error("SCHEMA miisaing in config.properties");
			//stop system
			System.exit(1);
		}
		log.info("AppresourceService initialized = "+SCHEMA_CONSTANT+APP_RESOURCE_PROD+APP_RESOURCE_DEV+APP_RESOURCE_AUDIT);
		
	}
	
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	/**
	 * Generate query.
	 *
	 * @param dbObjectName the db object name
	 * @param commaSeparatedColumns the comma separated columns
	 * @param whereClause the where clause
	 * @return the string
	 */
	private String generateQuery(String dbObjectName,String commaSeparatedColumns,String whereClause)
	{
		String query = null; 
		if(commaSeparatedColumns==null || commaSeparatedColumns.trim().length()==0)
		{
			query= "SELECT * FROM "+dbObjectName;
		}
		else
		{
			StringBuilder sbf = new StringBuilder("SELECT ");
			sbf.append(commaSeparatedColumns).append(" from ").append(dbObjectName);
			query =  sbf.toString();
		}
		if(whereClause!=null && whereClause.length()>0)
		{
			query = query+" "+whereClause;
		}
		return query;
	}	
	
	
	/**
	 * Gets the all data from vu tbl.
	 *
	 * @param dbObjectName the db object name
	 * @param commaSeparatedColumns the comma separated columns
	 * @param whereClause the where clause
	 * @return the all data from vu tbl
	 * @throws SQLException the SQL exception
	 */
	public List<Map<String, Object>> getAllDataFromVUTbl(String dbObjectName,String commaSeparatedColumns,String whereClause) throws SQLException
	{
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(generateQuery(dbObjectName, commaSeparatedColumns, whereClause));
		if (rows == null || rows.isEmpty()) 
		{
			log.info("No record found!");
		}
		else
		{
			if (log.isDebugEnabled())
				log.debug("Found " + rows.size() + " records");
		}
		
		return rows;		
	}
	
	
	
	public boolean insertToAppResourceDev(AppResourceBean appBean)
	{
		Object[] params = new Object[] {appBean.getKey(),appBean.getEnglishval(),appBean.getSpanishval(),appBean.getChangedBy(),appBean.getSource(),appBean.getAction()};
		jdbcTemplate.update(
				ADD_TO_DEV_TABLE,
				params, new int[]{Types.VARCHAR, Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR}
			);		
		return false;
	}
	
	public boolean insertToAppResourceProd(AppResourceBean appBean)
	{
		Object[] params = new Object[] {appBean.getKey(),appBean.getEnglishval(),appBean.getSpanishval(),appBean.getChangedBy(),appBean.getSource(),appBean.getAction()};
		jdbcTemplate.update(
				ADD_TO_PROD_TABLE,
				params, new int[]{Types.VARCHAR, Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR}
			);
		
		return false;
	}
	
	public boolean insertToAppResourceAudit(AppResourceBean appBean)
	{
		Object[] params = new Object[] {appBean.getKey(),appBean.getEnglishval(),appBean.getSpanishval(),appBean.getChangedBy(),appBean.getSource(),appBean.getAction()};
		jdbcTemplate.update(
				ADD_TO_AUDIT_TABLE,
				params, new int[]{Types.VARCHAR, Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR}
			);
		
		return false;
	}
	
	public boolean insertToAppResourcePublish(List<StringBuilder> alStrList,String publishedBy, String tagMessage)
	{
		Object[] params = new Object[] {alStrList.get(0).toString(),alStrList.get(1).toString(),publishedBy,tagMessage};
		jdbcTemplate.update(
				PUBLISH_PROPERTIES,
				params, new int[]{Types.VARCHAR, Types.VARCHAR,Types.VARCHAR,Types.VARCHAR}
			);		
		return true;
	}
	
	public boolean insertFromDevToProd(AppResourceBean appBean)
	{
		copyFromDevToProd(appBean);		
		deleteFromAppResourceDev(appBean);
		return true;
	}
	
	public boolean updateFromDevToProd(AppResourceBean appBean)
	{
		copyFromProdToAudit(appBean);
		deleteFromAppResourceProd(appBean);
		copyFromDevToProd(appBean);		
		deleteFromAppResourceDev(appBean);
		return true;
	}
	
	public boolean deleteFromDevToProd(AppResourceBean appBean)
	{
		copyFromDevToAudit(appBean);
		deleteFromAppResourceProd(appBean);
		deleteFromAppResourceDev(appBean);
		return true;
	}
	
	public boolean deleteFromAppResourceProd(AppResourceBean appBean)
	{
		Object[] params = new Object[] {appBean.getKey()};
		jdbcTemplate.update(
				DELETE_FROM_PROD,
				params, new int[]{Types.VARCHAR}
			);
		
		return true;
	}
	
	public boolean deleteFromAppResourceDev(AppResourceBean appBean)
	{
		Object[] params = new Object[] {appBean.getKey(),appBean.getSource()};
		jdbcTemplate.update(
				DELETE_FROM_DEV,
				params, new int[]{Types.VARCHAR,Types.VARCHAR}
			);
		
		return true;
	}
	
	public boolean copyFromProdToAudit(AppResourceBean appBean)
	{
		
		Object[] params = new Object[] {appBean.getKey()};
		jdbcTemplate.update(
				COPY_TO_AUDIT_TABLE_FROM_PROD,
				params, new int[]{Types.VARCHAR}
			);
		
		return false;
	}
	
	public boolean copyFromDevToAudit(AppResourceBean appBean)
	{
		
		Object[] params = new Object[] {appBean.getKey(),appBean.getSource()};
		jdbcTemplate.update(
				COPY_TO_AUDIT_TABLE_FROM_DEV,
				params, new int[]{Types.VARCHAR,Types.VARCHAR}
			);
		
		return false;
	}
	public boolean copyFromDevToProd(AppResourceBean appBean)
	{
		
		Object[] params = new Object[] {appBean.getKey(),appBean.getSource()};
		jdbcTemplate.update(
				COPY_TO_PROD_FROM_DEV,
				params, new int[]{Types.VARCHAR, Types.VARCHAR}
			);
		
		return false;
	}
	
	
	
	public int[] insertBatch(final Collection<AppResourceBean> alAppResourceBean) throws DataAccessException{

		  String sql = "INSERT INTO "+APP_RESOURCE_PROD+" (KEY, ENGLISH_VAL, SPANISH_VAL,CHANGED_BY,UPDATED_TIME,SOURCE) VALUES (?, ?, ?,'ADMIN',CURRENT_TIMESTAMP,'PROD')";
		  
		  try {
			 	return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {				
				  @Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
					  
					  	AppResourceBean oAppResourceBean = ((List<AppResourceBean>)alAppResourceBean).get(i);
					  	if( oAppResourceBean.getEnglishval()==null ||  oAppResourceBean.getSpanishval()==null)
					  	{
					  		System.out.println("error"+oAppResourceBean.getKey());
					  	}
						ps.setString(1, oAppResourceBean.getKey());
						ps.setString(2, oAppResourceBean.getEnglishval());
						ps.setString(3, oAppResourceBean.getSpanishval());
						System.out.println(oAppResourceBean);
					}

					@Override
					public int getBatchSize() 
					{
						return alAppResourceBean.size();
					}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			int[] intVar={};
			return intVar;
		}
	}
	
	public boolean updateAllCurrentMileStoneAsInactive()
	{		
		int rows = jdbcTemplate.update(MARK_CURRENT_MLSTONE_INACTIVE);
		return true;
	}
	
	public boolean insertCurrentMileStoneAsActive(MiscPropertiesBean mpb)
	{		
		Object[] params = new Object[] {mpb.getProp_key(),mpb.getProp_type(),mpb.getProp_val(),mpb.getChangedBy(),mpb.getFlag(),mpb.getDescription()};
		jdbcTemplate.update(INSERT_MISC_PROP_KEYS,params, new int[]{Types.VARCHAR, Types.VARCHAR,Types.VARCHAR, Types.VARCHAR,Types.VARCHAR, Types.VARCHAR});
		return true;
	}
	
	
	public boolean updateUserLoginSession(UserSession us){
		boolean updateStatus = false;
		Object[] params = new Object[] {us.getUserId(),us.getSessionId(),new Timestamp(us.getLoginTime()),new Timestamp(us.getLogoutTime())};		
		jdbcTemplate.update(INSERT_USER_SESSION,params, new int[]{Types.VARCHAR,Types.VARCHAR, Types.TIMESTAMP,Types.TIMESTAMP});
		updateStatus=true;
		return updateStatus;
		
		
	}
	
	public boolean insertReleaseVersion(ReleaseInfoTO rto){
		boolean updateStatus = false;
		Object[] params = new Object[] {rto.getReleaseId(),rto.getReleaseType(),rto.getAppName()};
		if("INTEGRATE".equals(rto.getReleaseType())){
		jdbcTemplate.update(INSERT_RELEASE_INFO_FOR_INTEGRATE,params, new int[]{Types.VARCHAR, Types.VARCHAR,Types.VARCHAR});
		updateStatus=true;
		}
		else{
			jdbcTemplate.update(INSERT_RELEASE_INFO_FOR_RELEASE,params, new int[]{Types.VARCHAR, Types.VARCHAR,Types.VARCHAR});
			updateStatus=true;
		}
		return updateStatus;
	}
	
	public boolean insertReleaseVersionFileMap(ReleaseInfoTO rto){
		Object[] params = new Object[] {rto.getReleaseId(),rto.getEnglishval(),rto.getSpanishval(),rto.getUpdatedBy()};
		jdbcTemplate.update(
				INSERT_INTO_RELEASE_INFO_FILE_MAP,
				params, new int[]{Types.VARCHAR, Types.VARCHAR,Types.VARCHAR,Types.VARCHAR}
			);		
		return true;
	}
	
	public boolean deleteReleaseVersionFileMap(ReleaseInfoTO rto){
		Object[] params = new Object[] {rto.getReleaseId()};
		jdbcTemplate.update(
				DELETE_FROM_RELEASE_INFO_FILE_MAP,
				params, new int[]{Types.VARCHAR}
			);		
		return true;
	}
	
	
	public ReleaseInfoTO fetchReleaseFileInfoByReleaseId(String releaseId){
		Object[] params = new Object[] {releaseId};
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_RELEASE_FILE_INFO_BY_RELEASE_ID,params) ;
		
		ReleaseInfoTO rto = null;
		if(rows!=null){
			for (Map<String, Object> map : rows) {
				String engFile = (String)map.get(EBIToolConstants.RELEASE_INFO_FILE_MAP.ENGLISH_FILE);
				String spanishFile = (String)map.get(EBIToolConstants.RELEASE_INFO_FILE_MAP.SPANISH_FILE);
				rto= new ReleaseInfoTO();
				
				rto.setEnglishval(engFile);
				rto.setSpanishval(spanishFile);
				rto.setReleaseId(releaseId);
				break;
			}
		}
		
		return rto;
	}
	
	public ReleaseInfoTO fetchReleaseIdInfo(String releaseId){
		Object[] params = new Object[] {releaseId};
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(FETCH_RELEASE_INFO_BY_RELEASE_ID,params) ;		
		ReleaseInfoTO rto = null;
		if(rows!=null){
			for (Map<String, Object> map : rows) {
				String releaseType = (String)map.get(EBIToolConstants.RELEASE_INFO.RELEASE_TYPE);
				String linkedReleaseId = (String)map.get(EBIToolConstants.RELEASE_INFO.LINKED_RELEASE_ID);				
				rto= new ReleaseInfoTO();				
				rto.setReleaseType(releaseType);
				rto.setLinkedReleaseId(linkedReleaseId);
				break;
			}
		}		
		return rto;
	}

}
