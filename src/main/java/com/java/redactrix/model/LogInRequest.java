package com.java.redactrix.model;

public class LogInRequest {


	private String uname;
	private String email;
	private String password;
	private String repeatpsw;
	
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRepeatpsw() {
		return repeatpsw;
	}
	public void setRepeatpsw(String repeatpsw) {
		this.repeatpsw = repeatpsw;
	}
}
