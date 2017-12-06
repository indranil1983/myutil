package com.ericsson.spring.model;

public class SwReleaseBean {
	private String appName;
	private String buildVersion;
	private String buildType;
	private String englishval;
	private String spanishval;
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
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
	public String getBuildVersion() {
		return buildVersion;
	}
	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}
	public String getBuildType() {
		return buildType;
	}
	public void setBuildType(String buildType) {
		this.buildType = buildType;
	}
}
