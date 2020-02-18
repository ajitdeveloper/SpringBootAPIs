package com.java.redactrix.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
	

	@Id
	private String email;
		
	private String userName;
	
	private String password;


	public User() {
		super();
	}
	
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



}