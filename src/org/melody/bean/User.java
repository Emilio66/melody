package org.melody.bean;

import java.io.Serializable;

public class User implements Serializable{
	/**
	 * @see user information 
	 */
	private static final long serialVersionUID = 24521231L;
	private int id;
	private String name;
	private String password;
	private String portrait;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	@Override
	public String toString() {
		return id + "|" + name + "|" + password + "|" + portrait;
	}
}
