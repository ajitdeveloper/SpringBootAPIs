package com.java.redactrix.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.redactrix.entity.ConfirmationCode;
import com.java.redactrix.repository.ConfirmationCodeRepo;

@Service
public class ConfirmationCodeService{

	
	@Autowired
	ConfirmationCodeRepo confirmationCodeRepo;
	
	
	public Optional<ConfirmationCode> findById(String code) {
		return confirmationCodeRepo.findById(code);
	}

	
	public ConfirmationCode save(ConfirmationCode confirmationCode) {
		// TODO Auto-generated method stub
		return confirmationCodeRepo.save(confirmationCode);
	}

}
