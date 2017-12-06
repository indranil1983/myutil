
package com.ericsson.spring.model.flowdoc;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "event",
    "author",
    "title",
    "external_thread_id",
    "thread",
    "flow_token"
})
public class FlowDocBean {

    @JsonProperty("event")
    private String event;
    @JsonProperty("author")
    private Author author;
    @JsonProperty("title")
    private String title;
    @JsonProperty("external_thread_id")
    private String externalThreadId;
    @JsonProperty("thread")
    private Thread thread;
    @JsonProperty("flow_token")
    private String flowToken;
    @JsonProperty("additionalProperties")
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("event")
    public String getEvent() {
        return event;
    }

    @JsonProperty("event")
    public void setEvent(String event) {
        this.event = event;
    }

    @JsonProperty("author")
    public Author getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(Author author) {
        this.author = author;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("external_thread_id")
    public String getExternalThreadId() {
        return externalThreadId;
    }

    @JsonProperty("external_thread_id")
    public void setExternalThreadId(String externalThreadId) {
        this.externalThreadId = externalThreadId;
    }

    @JsonProperty("thread")
    public Thread getThread() {
        return thread;
    }

    @JsonProperty("thread")
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @JsonProperty("flow_token")
    public String getFlowToken() {
        return flowToken;
    }

    @JsonProperty("flow_token")
    public void setFlowToken(String flowToken) {
        this.flowToken = flowToken;
    }

    @JsonProperty("additionalProperties")
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonProperty("additionalProperties")
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FlowDocBean [");
		if (event != null) {
			builder.append("event=");
			builder.append(event);
			builder.append(", ");
		}
		if (author != null) {
			builder.append("author=");
			builder.append(author);
			builder.append(", ");
		}
		if (title != null) {
			builder.append("title=");
			builder.append(title);
			builder.append(", ");
		}
		if (externalThreadId != null) {
			builder.append("externalThreadId=");
			builder.append(externalThreadId);
			builder.append(", ");
		}
		if (thread != null) {
			builder.append("thread=");
			builder.append(thread);
			builder.append(", ");
		}
		if (flowToken != null) {
			builder.append("flowToken=");
			builder.append(flowToken);
			builder.append(", ");
		}
		if (additionalProperties != null) {
			builder.append("additionalProperties=");
			builder.append(additionalProperties);
		}
		builder.append("]");
		return builder.toString();
	}

}
