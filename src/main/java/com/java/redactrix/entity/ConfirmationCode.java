package com.java.redactrix.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ConfirmationCode {

	

	private String emailId;
	
	
	@Id
	private String confCode;
	
	
	
	private String Name;
	private String password;
	
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getConfCode() {
		return confCode;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setConfCode() {
		this.confCode = UUID.randomUUID().toString();
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		this.Name = name;
	}
	
	
	
}
