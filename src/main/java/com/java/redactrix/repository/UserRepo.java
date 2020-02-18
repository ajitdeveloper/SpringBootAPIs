package com.java.redactrix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.redactrix.entity.User;


public interface UserRepo extends JpaRepository<User, String> {

}
