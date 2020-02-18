package com.java.redactrix.service;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.redactrix.model.ImageDimension;
import com.java.redactrix.storage.StorageService;

@Service
public class ImageDimensionService {

	@Autowired 
	private UploadService applicationService;
	
	@Autowired
	StorageService storageService;
	
	public ImageDimensionService(StorageService storageService) {
		this.storageService = storageService;
	}
	

	
	public ImageDimension getImageDimension(String imageUrl) {
		String baseUrl = FilenameUtils.getPath(imageUrl);

		String myFile = FilenameUtils.getBaseName(imageUrl) + "." + FilenameUtils.getExtension(imageUrl);

		//***********replace with replace generator parameter soon*************
		String reqGenerator = applicationService.countFiles() + "";
		System.out.println(reqGenerator); // 32

		String originalName = reqGenerator + "/input/" + myFile;
		System.out.println(originalName);

//		Boolean exists = storageService.check(reqGenerator, myFile);

		String uploadedFile = storageService.loadInput(reqGenerator, myFile).toString();
		System.out.println(uploadedFile + "---------------------------------------------");
		nu.pattern.OpenCV.loadShared();
		Mat src = Imgcodecs.imread(uploadedFile);
		System.out.println("IMAGE WIDTH AND HEIGHT");
		System.out.println(src.width());
		System.out.println(src.height());
		ImageDimension imageDimension = new ImageDimension();
		imageDimension.setHeight(src.height());
		imageDimension.setWidth(src.width());
		return imageDimension;
	}
}
