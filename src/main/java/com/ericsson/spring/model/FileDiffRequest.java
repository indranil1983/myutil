package com.ericsson.spring.model;

public class FileDiffRequest {
	String[] filePublishId = null;
	String language = null;
	public String[] getFilePublishId() {
		return filePublishId;
	}
	public void setFilePublishId(String[] filePublishId) {
		this.filePublishId = filePublishId;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
}
