package com.ericsson.spring.model;

public class ReleaseInfoTO {
	private String appName;
	private String releaseId;
	private String linkedReleaseId;
	private String releaseType;
	private String englishval;
	private String spanishval;
	private String updatedBy;
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getReleaseId() {
		return releaseId;
	}
	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}
	public String getLinkedReleaseId() {
		return linkedReleaseId;
	}
	public void setLinkedReleaseId(String linkedReleaseId) {
		this.linkedReleaseId = linkedReleaseId;
	}
	public String getReleaseType() {
		return releaseType;
	}
	public void setReleaseType(String releaseType) {
		this.releaseType = releaseType;
	}
	
	public String getEnglishval() {
		return englishval;
	}

	public void setEnglishval(String englishval) {
		this.englishval = englishval;
	}

	public String getSpanishval() {
		return spanishval;
	}

	public void setSpanishval(String spanishval) {
		this.spanishval = spanishval;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
}
