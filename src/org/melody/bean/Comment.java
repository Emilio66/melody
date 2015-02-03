package org.melody.bean;

import java.io.Serializable;
public class Comment implements Serializable{

	private static final long serialVersionUID = 4521351L;
	private String username;	
	private String content;
	private String time;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String String) {
		this.time = String;
	}
	@Override
	public String toString() {
		return username + "|" + content + "|" + time;
	}
	
}
