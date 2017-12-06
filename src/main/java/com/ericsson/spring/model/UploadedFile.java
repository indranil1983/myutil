package com.ericsson.spring.model;

import java.util.Map;
import java.util.Properties;

public class UploadedFile {

	private Long id;
	private String name;
	private String location;
	private Long size;
	private String type;
	private String content;
	private Properties prop;
	private Map<String, String> propMap;

		public Properties getProp() {
		return prop;
	}


	public void setProp(Properties prop) {
		this.prop = prop;
	}


		public Long getId() {
		return id;
	}

	
	public String getName() {
		return name;
	}

	
	public void setId(Long id) {
		this.id = id;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public void setSize(Long size) {
		this.size = size;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getLocation() {
		return location;
	}

	public Long getSize() {
		return size;
	}

	
	public String getType() {
		return type;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public Map<String, String> getPropMap() {
		return propMap;
	}


	public void setPropMap(Map<String, String> propMap) {
		this.propMap = propMap;
	}
}
