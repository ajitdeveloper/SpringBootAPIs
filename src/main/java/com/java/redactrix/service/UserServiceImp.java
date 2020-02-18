package com.java.redactrix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.redactrix.entity.User;
import com.java.redactrix.repository.UserRepo;

@Service
public class UserServiceImp  {

	@Autowired
	private UserRepo userRepo;

	
	public List<User> getAllUser() {
		// TODO Auto-generated method stub
		return userRepo.findAll();
	}

	
	public Optional<User> findUserByEmail(String Email) {
		// TODO Auto-generated method stub
		return userRepo.findById(Email);
	}

	
	public long countFiles() {
		// TODO Auto-generated method stub
		return userRepo.count();
	}


	public User createUser(User user) {
		// TODO Auto-generated method stub
		return userRepo.save(user);
	}

}
