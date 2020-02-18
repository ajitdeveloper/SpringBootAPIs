package com.java.redactrix.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.java.redactrix.model.ImageFile;;


public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

}