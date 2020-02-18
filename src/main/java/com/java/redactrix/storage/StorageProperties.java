package com.java.redactrix.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

	/**
	 * Folder location for storing files
	 */
	private String location = "input";
	private String detectLoc = "output";

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDetectLoc() {
		return detectLoc;
	}

	public String setDetectLoc() {
		return detectLoc;
	}

}
