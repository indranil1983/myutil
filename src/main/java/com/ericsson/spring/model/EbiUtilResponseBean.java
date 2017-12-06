package com.ericsson.spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EbiUtilResponseBean 
{
	boolean status =false;;
	List<String> errorList=new ArrayList<String>(5);
	List<String> successList=new ArrayList<String>(5);
	List<AppResourceBean> appRsrcList = null;
	String[] fileStringDiff = new String[2];
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EbiUtilResponseBean [status=");
		builder.append(status);
		builder.append(", ");
		if (errorList != null) {
			builder.append("errorList=");
			builder.append(errorList);
			builder.append(", ");
		}
		if (successList != null) {
			builder.append("successList=");
			builder.append(successList);
			builder.append(", ");
		}
		if (appRsrcList != null) {
			builder.append("appRsrcList=");
			builder.append(appRsrcList);
			builder.append(", ");
		}
		if (deletedKeys != null) {
			builder.append("deletedKeys=");
			builder.append(deletedKeys);
			builder.append(", ");
		}
		if (alListOfMaps != null) {
			builder.append("alListOfMaps=");
			builder.append(alListOfMaps);
			builder.append(", ");
		}
		if (rallyResponse != null) {
			builder.append("rallyResponse=");
			builder.append(rallyResponse);
		}
		builder.append("]");
		return builder.toString();
	}
	List<AppResourceBean> deletedKeys = null;
	List<Map<String, Object>> alListOfMaps=null;
	private String rallyResponse = null;
	
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public List<String> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}
	public List<String> getSuccessList() {
		return successList;
	}
	public void setSuccessList(List<String> successList) {
		this.successList = successList;
	}
	public List<AppResourceBean> getAppRsrcList() {
		return appRsrcList;
	}
	public void setAppRsrcList(List<AppResourceBean> appRsrcList) {
		this.appRsrcList = appRsrcList;
	}
	public EbiUtilResponseBean(boolean status) {
		super();
		this.status = status;
	}
	
	public EbiUtilResponseBean() {
		
	}
	public List<Map<String, Object>> getAlListOfMaps() {
		return alListOfMaps;
	}
	public void setAlListOfMaps(List<Map<String, Object>> alListOfMaps) {
		this.alListOfMaps = alListOfMaps;
	}
	public String getRallyResponse() {
		return rallyResponse;
	}
	public void setRallyResponse(String rallyResponse) {
		this.rallyResponse = rallyResponse;
	}
	public List<AppResourceBean> getDeletedKeys() {
		return deletedKeys;
	}
	public void setDeletedKeys(List<AppResourceBean> deletedKeys) {
		this.deletedKeys = deletedKeys;
	}
	public String[] getFileStringDiff() {
		return fileStringDiff;
	}
	public void setFileStringDiff(String[] fileStringDiff) {
		this.fileStringDiff = fileStringDiff;
	}
	
	
}
