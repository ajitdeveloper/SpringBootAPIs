package com.java.redactrix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.redactrix.model.ImageFile;
import com.java.redactrix.repository.ImageFileRepository;

@Service
public class UploadService {

	@Autowired
	ImageFileRepository imageFileRepository;

	
	public long countFiles() {
		// TODO Auto-generated method stub
		return imageFileRepository.count();

	}

	
	public ImageFile createImageFile(ImageFile imageFile) {
		return imageFileRepository.save(imageFile);
	}

	
	public List<ImageFile> getAllImage() {
		return imageFileRepository.findAll();

	}

}
