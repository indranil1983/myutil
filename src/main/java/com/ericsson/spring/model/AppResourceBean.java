package com.ericsson.spring.model;

public class AppResourceBean 
{
	private String key;
	private String englishval;
	private String spanishval;
	private String changedBy;
	private String source;
	private String action;
	private String old_action;
	private String old_source;
	private String oldenglishval;
	private String oldspanishval;
	private boolean isDev=false;
	private String tagMessage=null;
	private String refUrl=null;

	public String getOldspanishval() {
		return oldspanishval;
	}

	public void setOldspanishval(String oldspanishval) {
		this.oldspanishval = oldspanishval;
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

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	
	
	public String getOld_action() {
		return old_action;
	}

	public void setOld_action(String old_action) {
		this.old_action = old_action;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AppResourceBean [");
		if (key != null) {
			builder.append("key=");
			builder.append(key);
			builder.append(", ");
		}
		if (englishval != null) {
			builder.append("englishval=");
			builder.append(englishval);
			builder.append(", ");
		}
		if (spanishval != null) {
			builder.append("spanishval=");
			builder.append(spanishval);
			builder.append(", ");
		}
		if (changedBy != null) {
			builder.append("changedBy=");
			builder.append(changedBy);
			builder.append(", ");
		}
		if (source != null) {
			builder.append("source=");
			builder.append(source);
			builder.append(", ");
		}
		if (action != null) {
			builder.append("action=");
			builder.append(action);
			builder.append(", ");
		}
		if (oldenglishval != null) {
			builder.append("oldenglishval=");
			builder.append(oldenglishval);
			builder.append(", ");
		}
		if (oldspanishval != null) {
			builder.append("oldspanishval=");
			builder.append(oldspanishval);
		}
		builder.append("]");
		return builder.toString();
	}

	public String getOldenglishval() {
		return oldenglishval;
	}

	public void setOldenglishval(String oldenglishval) {
		this.oldenglishval = oldenglishval;
	}

	public boolean isDev() {
		return isDev;
	}

	public void setDev(boolean isDev) {
		this.isDev = isDev;
	}

	public String getOld_source() {
		return old_source;
	}

	public void setOld_source(String old_source) {
		this.old_source = old_source;
	}

	public String getTagMessage() {
		return tagMessage;
	}

	public void setTagMessage(String tagMessage) {
		this.tagMessage = tagMessage;
	}

	public String getRefUrl() {
		return refUrl;
	}

	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}
	
	
	
	
}
