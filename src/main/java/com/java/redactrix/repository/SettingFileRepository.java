package com.java.redactrix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.redactrix.entity.SettingsFile;

public interface SettingFileRepository extends 	JpaRepository<SettingsFile, Long>	 {

}
