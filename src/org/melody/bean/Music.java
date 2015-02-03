package org.melody.bean;

import java.io.Serializable;

public class Music implements Serializable{

	/**
	 * @see music information
	 */
	private static final long serialVersionUID = 5467321L;
	
	private int id;
	private int s_id; //style of this music
	private int owner;
	private String name;
	private String artist;
	private String path;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getS_id() {
		return s_id;
	}
	public void setS_id(int s_id) {
		this.s_id = s_id;
	}
	public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return id + "|" + s_id + "|" + owner + "|" + name + "|" + artist + "|"
				+ path;
	}
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof Music){
			Music tmpMusic=(Music)obj;
			if(name.equals(tmpMusic.getName()))
				return true;
		}		
		return false;
	}
	
	
}
