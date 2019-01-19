package com.codecracker.vo;

import java.io.Serializable;

public class Update implements Serializable{

	private String name;
	
	private String key;
	
	public Update(String name, String key) {
		super();
		this.name = name;
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	
}
