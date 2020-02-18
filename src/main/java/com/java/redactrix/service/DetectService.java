package com.java.redactrix.service;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.redactrix.storage.StorageService;

@Service
public class DetectService {

	@Autowired
	private SettingModelServiceImp settingModelService;
	

	@Autowired
	private UploadService applicationService;

	@Autowired
	private final StorageService storageService;

	public DetectService(StorageService storageService) {
		this.storageService = storageService;
	}

	
	public Rect[] detectFaces(String uploadedImage) {

		String myFile = FilenameUtils.getBaseName(uploadedImage) + "." + FilenameUtils.getExtension(uploadedImage);

		//***********replace with replace generator parameter soon*************
		String reqGenerator = applicationService.countFiles() + "";

		String uploadedFile = storageService.loadInput(reqGenerator, myFile).toString();
		System.out.println(uploadedFile + "---------------------------------------------");
		nu.pattern.OpenCV.loadShared();
		Mat src = Imgcodecs.imread(uploadedFile);
		String xmlFile = "/home/ajith/Downloads/opencv-develop/upstream/res/raw/lbpcascade_frontalface.xml";
		CascadeClassifier classifier = new CascadeClassifier(xmlFile);
		// Detecting the face in the snap
		MatOfRect faceDetections = new MatOfRect();
		classifier.detectMultiScale(src, faceDetections);
		String detectedFaces = String.format("Detected %s faces", faceDetections.toArray().length);
		System.out.println(detectedFaces);
		return faceDetections.toArray();
	}
}
