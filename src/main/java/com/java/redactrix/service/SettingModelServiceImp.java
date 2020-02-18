package com.java.redactrix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.redactrix.entity.SettingsFile;
import com.java.redactrix.entity.SettingsModel;
import com.java.redactrix.repository.SettingsModelRepo;


@Service
public class SettingModelServiceImp {

	@Autowired
	SettingsModelRepo settingModelRepo;
	
	public SettingsModel createSettingModel(SettingsModel settingsModel) {
		// TODO Auto-generated method stub
		return settingModelRepo.save(settingsModel);
	}

	public Optional<SettingsModel> getById(long id) {
		// TODO Auto-generated method stub
		return settingModelRepo.findById(id);
	}

	public List<SettingsFile> getAllModels() {
		// TODO Auto-generated method stub
		return null;
	}



	
}
