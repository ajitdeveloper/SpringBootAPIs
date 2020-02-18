package com.java.redactrix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.redactrix.entity.ConfirmationCode;

public interface ConfirmationCodeRepo extends JpaRepository<ConfirmationCode, String> {

	

}
