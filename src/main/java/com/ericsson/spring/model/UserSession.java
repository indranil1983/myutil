package com.ericsson.spring.model;

public class UserSession {

	private String userId;
	private String sessionId;
	private long loginTime;
	private long logoutTime;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public long getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}
	public long getLogoutTime() {
		return logoutTime;
	}
	public void setLogoutTime(long logoutTime) {
		this.logoutTime = logoutTime;
	}
}
