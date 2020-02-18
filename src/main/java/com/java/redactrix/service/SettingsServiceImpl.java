package com.java.redactrix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.redactrix.entity.SettingsFile;
import com.java.redactrix.repository.SettingFileRepository;

@Service
public class SettingsServiceImpl {

	@Autowired
	SettingFileRepository settingFileRepository;
	
	
	
	public SettingsFile createSettingFile(SettingsFile settingsFile) {
		return settingFileRepository.save(settingsFile);
	}

	
	public List<SettingsFile> getAllSettings() {
		return settingFileRepository.findAll();
	}
	
	
	public long countFiles() {
		return settingFileRepository.count();
	}

	
	public Optional<SettingsFile> getById(long id) {
		// TODO Auto-generated method stub
		return settingFileRepository.findById(id);
	}

}
