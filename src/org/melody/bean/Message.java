package org.melody.bean;

import java.io.Serializable;

public class Message implements Serializable{

	/**
	 * @see message detail
	 */
	private static final long serialVersionUID = 8937511L;

	private int sender_id;
	private char status;
	private String content;
	private String time;
	
	public int getSender_id() {
		return sender_id;
	}
	public void setSender_id(int sender_id) {
		this.sender_id = sender_id;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
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
	public void setTime(String date) {
		this.time = date;
	}
	
	@Override
	public String toString() {
		return sender_id + "|" + status + "|" + content + "|" + time;
	}
}
