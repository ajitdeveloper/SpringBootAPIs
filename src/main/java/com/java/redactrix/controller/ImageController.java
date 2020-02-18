package com.java.redactrix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.java.redactrix.model.ImageDimension;
import com.java.redactrix.service.ImageDimensionService;

@RestController
public class ImageController {

	@Autowired ImageDimensionService imageDimensionService;
	
	@RequestMapping(value = "/imageDimension", method = RequestMethod.POST)
	public ResponseEntity<ImageDimension> imageDimension(String imageUrl) {
		ImageDimension imageDimension = imageDimensionService.getImageDimension(imageUrl);
		System.out.println(imageDimension.getHeight());
		System.out.println(imageDimension.getWidth());
		return ResponseEntity.ok().body(imageDimension);
	}
}
