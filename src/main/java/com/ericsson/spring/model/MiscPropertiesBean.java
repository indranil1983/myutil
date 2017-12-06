package com.ericsson.spring.model;

public class MiscPropertiesBean {
	
	private String prop_key;
	private String prop_val;
	private String prop_type;
	private String changedBy;
	private String flag;
	private String description;
	
	
	public String getProp_key() {
		return prop_key;
	}
	public void setProp_key(String prop_key) {
		this.prop_key = prop_key;
	}
	public String getProp_val() {
		return prop_val;
	}
	public void setProp_val(String prop_val) {
		this.prop_val = prop_val;
	}
	public String getProp_type() {
		return prop_type;
	}
	public void setProp_type(String prop_type) {
		this.prop_type = prop_type;
	}
	public String getChangedBy() {
		return changedBy;
	}
	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
