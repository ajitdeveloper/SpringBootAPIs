
package com.java.redactrix.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {
	
	@Autowired
	private UserServiceImp userService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	
		/*
		 * Search in database for a user
		 * If user doesn't exists then wrong credentials
		 * also for a password and role
		 * Since we haven't set role we will set default to USER
		 */

		 Optional<com.java.redactrix.entity.User> user = userService.findUserByEmail(email);
		 if(user.isPresent()) {
			 System.out.println("User Found");
			 com.java.redactrix.entity.User getUser =  user.get();
			 String password = getUser.getPassword();
			 return new org.springframework.security.core.userdetails.User(email, password, new ArrayList<>());
		 }
		 else {
			 return null;
		 }
	}

}

