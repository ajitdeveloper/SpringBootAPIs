package com.java.redactrix.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class SettingsFile {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
    @OneToMany(targetEntity=SettingsModel.class,fetch = FetchType.EAGER, cascade = CascadeType.PERSIST )  
	private List<SettingsModel> settings;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<SettingsModel> getSettings() {
		return settings;
	}

	public void setSettings(List<SettingsModel> settings) {
		this.settings = settings;
	}

    
}
