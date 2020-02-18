package com.java.redactrix.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.java.redactrix.entity.SettingsFile;
import com.java.redactrix.entity.SettingsModel;
import com.java.redactrix.model.Coordinates;
import com.java.redactrix.model.FileUploadResult;
import com.java.redactrix.model.ImageFile;
import com.java.redactrix.service.ConfirmationCodeService;
import com.java.redactrix.service.DetectService;
import com.java.redactrix.service.SettingModelServiceImp;
import com.java.redactrix.service.SettingsServiceImpl;
import com.java.redactrix.service.UploadService;
import com.java.redactrix.storage.StorageService;

@RestController
public class FileController {
	
	@Autowired 
	DetectService detectService;
	
	@Autowired
	ConfirmationCodeService confirmationCodeService;
	

	@Autowired
	SettingModelServiceImp settingModelService;

	@Autowired
	SettingsServiceImpl settingService;

	@Autowired
	UploadService applicationService;

	@Autowired
	private final StorageService storageService;

	public FileController(StorageService storageService) {
		this.storageService = storageService;
	}

	

	/*
	 * GETTING THE MULTIPART FILES AND UPLOADING 
	 * IN THE STORAGE AND ALSO SAVING THE PATH INTO THE DB
	 */
	@RequestMapping(value = "/imageFileUpload", method = RequestMethod.POST)
	public ResponseEntity<Object> createImages(@RequestParam(value = "file") MultipartFile[] files) throws IOException {
		ImageFile imageFile = new ImageFile();
		
		//CREATE A REQUEST GENERATOR FOR EACH NEWLY UPLOAD FILE
		String requestGenerator = (applicationService.countFiles() + 1) + "";
		String currDir = System.getProperty("user.dir");
		int fileCounter = 0;
		
		// COUNT THE EACH MULTIPART FILE AND ASSIGN EACH COUNT AS THE NAME OF EACH FILE, AND SET THE PATH IN THE DB
		for (MultipartFile file : files) {
			String extension = FilenameUtils.getExtension(file.getOriginalFilename());
			fileCounter++;
			storageService.store(file, requestGenerator, fileCounter, extension);
			imageFile.setPath(currDir + "/" + requestGenerator + "/");
		}
		
		applicationService.createImageFile(imageFile);
		
		//CREATING A LIST THAT HOLDS ALL THE FILES
		List<String> listFiles = new ArrayList<String>();
		File folder = new File(currDir + "/" + requestGenerator + "/" + "input/");
		File[] inputFiles = folder.listFiles();
		for (File file : inputFiles) {
			listFiles.add(file.getName());
		}
		
		//GETTING EACH FILE FROM THE RESOURCE URI, AND CREATING A URL FOR EACH FILE
		List<String> fileNames = listFiles.stream()
				.map(fileName -> MvcUriComponentsBuilder
						.fromMethodName(FileController.class, "getFile", fileName, requestGenerator).build().toString())
				.collect(Collectors.toList());
		
		//CREATING A MODEL FOR THE RESULT OF THE UPLOADED FILE
		FileUploadResult fileModel = new FileUploadResult();
		fileModel.files = fileNames;
		fileModel.requestId = requestGenerator;
		
		return ResponseEntity.ok().body(fileModel);
	}


	/*
	 * DETECTING AND BLURRING THE SELECTED IMAGES
	 */

	@PostMapping("/processedImage")
	public ResponseEntity<List<Coordinates>> BlurImage(@RequestParam("img") String url) {		
		Rect[] a = detectService.detectFaces(url);
		List<Coordinates> listOfCoordinate = new ArrayList<Coordinates>();
		for (int i = 0; i < a.length; i++) {
			Rect rectf = a[i];
			Coordinates coordinate = new Coordinates((int)rectf.x, (int)rectf.y, (int)rectf.width, (int)rectf.height);
			listOfCoordinate.add(coordinate);
		}
		return ResponseEntity.status(HttpStatus.OK).body(listOfCoordinate);
	}

	@PostMapping("/blur")
	public ResponseEntity<String> blur(@RequestParam("coords") String coords, @RequestParam("img") String url) {
		System.out.println("This is a file as parameter");
		System.out.println(url);
		String baseUrl = FilenameUtils.getPath(url);

		String myFile = FilenameUtils.getBaseName(url) + "." + FilenameUtils.getExtension(url);

		System.out.println(baseUrl); // local host 8080:reqgen/input
		System.out.println(myFile); // 1.jpg

		String reqGenerator = applicationService.countFiles() + "";
		System.out.println(reqGenerator); // 32

		String originalName = reqGenerator + "/output/" + myFile;
		System.out.println(originalName);

		String[] arrOfCoordinates = coords.split(",", 4);

		// getting all the coordinates

		double x = Double.valueOf(arrOfCoordinates[0]);
		double y = Double.valueOf(arrOfCoordinates[1]);
		double width = Double.valueOf(arrOfCoordinates[2]);
		double height = Double.valueOf(arrOfCoordinates[3]);

		// Loading the image from the storage

		String uploadedFile = storageService.loadOutput(reqGenerator, myFile).toString();
		nu.pattern.OpenCV.loadShared();
		Mat src = Imgcodecs.imread(uploadedFile);
		
		// created a rectangle

		Rect rect1 = new Rect((int) x, (int) y, (int) width, (int) height);

		// blurring on the rectangle
		Imgproc.GaussianBlur(src.submat(rect1), src.submat(rect1), new Size(55, 55), 0);
		myFile = "b" + myFile;

		storageService.store(src, reqGenerator, myFile);

		String path = ServletUriComponentsBuilder.fromCurrentContextPath().path(reqGenerator + "/output/" + myFile)
				.toUriString();
		return ResponseEntity.status(HttpStatus.OK).body(path);
	}

	@PostMapping("/back")
	public ResponseEntity<FileUploadResult> getAllImages(@RequestParam("requestId") String currReqId) {
		System.out.println("connected and ready to take");
		String currDir = System.getProperty("user.dir");
		List<String> listFiles = new ArrayList<String>();
		File folder = new File(currDir + "/" + currReqId + "/" + "input/");
		File[] inputFiles = folder.listFiles();

		// totally depends on the length of the string
		// because for us the largest length is the one which is edited at very last
		for (File file : inputFiles) {
			System.out.println("File to string");
			System.out.println(file.getName());
			System.out.println("These are the absolute paths");
			listFiles.add(file.getName());
		}

		System.out.println(listFiles);

		List<String> fileNames = listFiles.stream()
				.map(fileName -> MvcUriComponentsBuilder
						.fromMethodName(FileController.class, "getFile", fileName, currReqId).build().toString())
				.collect(Collectors.toList());

		System.out.println(fileNames);

		FileUploadResult fileModel = new FileUploadResult();
		fileModel.files = fileNames;
		fileModel.requestId = currReqId;

		return ResponseEntity.ok().body(fileModel);
	}

	@PostMapping("/coords")
	public ResponseEntity<Rect[]> getcoords(@RequestParam("requestId") String currId, @RequestParam("img") String img) {
		System.out.println("ready to get coordinates");
		String baseName = FilenameUtils.getBaseName(img);
		String ext = FilenameUtils.getExtension(img);
		// extract number
		String numberOnly = baseName.replaceAll("[^0-9]", "");
		System.out.println(numberOnly);
		// append and make a fresh input file
		String inputImg = numberOnly + "." + ext;
		System.out.println(inputImg);
		// run face detector on it
		String uploadedFile = storageService.loadInput(currId, inputImg).toString();
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
		// create rectangles on that face detector and get the values of the x y width
		// and height
		Rect[] a = faceDetections.toArray();
		for (int i = 0; i < faceDetections.toArray().length; i++) {
			Rect rectf = a[i];

			Rect rect1 = new Rect((int) rectf.x, (int) rectf.y, (int) rectf.width, (int) rectf.height);

			System.out.println("rect1::: " + rect1.width + " and " + rect1.height);
			System.out.println("face rect area isssss... " + rect1.area());
		}
		// return along with body in the response entity

		return ResponseEntity.ok().body(a);
	}

	// for blurring multiple region
	@PostMapping("/unBlurAll")
	public ResponseEntity<String> unBlurAll(@RequestParam("requestId") String currId,
			@RequestParam("interImg") String interImg, @RequestParam("coords") List<Integer> coords) {
		System.out.println(coords);
		String baseName = FilenameUtils.getBaseName(interImg);
		// extract number
		String numberOnly = baseName.replaceAll("[^0-9]", "");
		// extract extension
		String ext = FilenameUtils.getExtension(interImg);

		// interImg file
		String uploadedImg = FilenameUtils.getBaseName(interImg) + "." + FilenameUtils.getExtension(interImg);
		String freshImg = numberOnly + "." + ext;

		uploadedImg = storageService.loadOutput(currId, uploadedImg).toString();
		System.out.println("This is the uploaded image needed to unblur----->" + uploadedImg);
		freshImg = storageService.loadInput(currId, freshImg).toString();
		System.out.println("This is the fresh input image needed ----->" + freshImg);

		nu.pattern.OpenCV.loadShared();
		Mat src = Imgcodecs.imread(uploadedImg);
		Mat inp = Imgcodecs.imread(freshImg);
		for (int i = 3; i < coords.size(); i += 4) {
			int x = 0, y = 0, w = 0, h = 0;
			for (int j = i - 3; j <= i; j++) {
//			System.out.println(coords.get(j%4));
//				System.out.println(j%4);

				if (j % 4 == 0) {
					x = coords.get(j);
				}
				if (j % 4 == 1) {
					y = coords.get(j);
				}
				if (j % 4 == 2) {
					w = coords.get(j);
				}
				if (j % 4 == 3) {
					h = coords.get(j);
				}

				// do the unblurring here

			}
			System.out.println(x + "," + y + "," + w + "," + h);
			System.out.println();
//			 // created a rectangle
//			 
			Rect rect1 = new Rect((int) x, (int) y, (int) w, (int) h);
			System.out.println("This is rect1 to string");
			System.out.println(rect1.toString());
			inp.submat(rect1).copyTo(src.submat(rect1));
		}

		uploadedImg = "ua" + FilenameUtils.getBaseName(interImg) + "." + FilenameUtils.getExtension(interImg);
		storageService.store(src, currId, uploadedImg);

		String path = ServletUriComponentsBuilder.fromCurrentContextPath().path(currId + "/output/" + uploadedImg)
				.toUriString();
		System.out.println(path);
		return ResponseEntity.status(HttpStatus.OK).body(path);

	}

	@PostMapping("/blurAll")
	public ResponseEntity<String> blurAll(@RequestParam("requestId") String currId,
			@RequestParam("interImg") String interImg, @RequestParam("coords") List<Integer> coords) {
		String myFile = FilenameUtils.getBaseName(interImg) + "." + FilenameUtils.getExtension(interImg);
		String uploadedFile = storageService.loadOutput(currId, myFile).toString();
		nu.pattern.OpenCV.loadShared();
		Mat src = Imgcodecs.imread(uploadedFile);
		for (int i = 3; i < coords.size(); i += 4) {
			int x = 0, y = 0, w = 0, h = 0;
			for (int j = i - 3; j <= i; j++) {
//			System.out.println(coords.get(j%4));
//				System.out.println(j%4);
				if (j % 4 == 0) {
					x = coords.get(j);
				}
				if (j % 4 == 1) {
					y = coords.get(j);
				}
				if (j % 4 == 2) {
					w = coords.get(j);
				}
				if (j % 4 == 3) {
					h = coords.get(j);
				}

			}
			System.out.println(x + "," + y + "," + w + "," + h);
			System.out.println();
			// created a rectangle
			Rect rect1 = new Rect((int) x, (int) y, (int) w, (int) h);
			System.out.println("This is rect1 to string");
			System.out.println(rect1.toString());
			// blurring on the rectangle
			Imgproc.GaussianBlur(src.submat(rect1), src.submat(rect1), new Size(55, 55), 0);
		}
		myFile = "ba" + myFile;
		storageService.store(src, currId, myFile);
		String path = ServletUriComponentsBuilder.fromCurrentContextPath().path(currId + "/output/" + myFile)
				.toUriString();
		System.out.println(path);
		return ResponseEntity.status(HttpStatus.OK).body(path);
	}

	// SettingsFile newSetting = new SettingsFile();
	// to persist the settings into the database
	@PostMapping("/settings")
	public ResponseEntity<SettingsFile> settings(@RequestParam("size") String size, @RequestParam("type") String type) {
		System.out.println("Ready to get Settings");

		// create new
		if (settingService.countFiles() == 0) {

			SettingsFile newSettings = new SettingsFile();
			newSettings.setId(1);

			SettingsModel setmod1 = new SettingsModel();
			setmod1.setId(1);
			setmod1.setType("size");
			setmod1.setValue("medium");
			settingModelService.createSettingModel(setmod1);

			SettingsModel setmod2 = new SettingsModel();
			setmod2.setId(2);
			setmod2.setType("blur");
			setmod2.setValue("blur");
			settingModelService.createSettingModel(setmod2);

			ArrayList<SettingsModel> settings = new ArrayList<SettingsModel>();
			settings.add(setmod1);
			settings.add(setmod2);

			newSettings.setSettings(settings);
			settingService.createSettingFile(newSettings);

			return ResponseEntity.status(HttpStatus.OK).body(newSettings);
		}
		// else update the existing settings
		else {
			Optional<SettingsFile> existingSetting = settingService.getById(1);
			System.out.println(size);
			System.out.println(type);

			SettingsFile getSetting = existingSetting.get();

			SettingsFile newSettings = new SettingsFile();
			newSettings.setId(1);

			SettingsModel setmod1 = new SettingsModel();
			setmod1.setId(1);
			setmod1.setType("size");
			setmod1.setValue(size);
			settingModelService.createSettingModel(setmod1);

			SettingsModel setmod2 = new SettingsModel();
			setmod2.setId(2);
			if (type.contains("-")) {
				System.out.println("check for color");
				setmod2.setType("color");
				setmod2.setValue(type);
				settingModelService.createSettingModel(setmod2);
			} else {
				setmod2.setType("blur");
				setmod2.setValue(type);
				settingModelService.createSettingModel(setmod2);

			}

			ArrayList<SettingsModel> settings = new ArrayList<SettingsModel>();
			settings.add(setmod1);
			settings.add(setmod2);

			newSettings.setSettings(settings);
			settingService.createSettingFile(newSettings);

			settingService.createSettingFile(newSettings);
			return ResponseEntity.status(HttpStatus.OK).body(newSettings);
		}
	}

	@GetMapping("/settings")
	public ResponseEntity<List<SettingsModel>> settings() {
		Optional<SettingsModel> firstSize = settingModelService.getById(1);
		Optional<SettingsModel> secondType = settingModelService.getById(2);

		SettingsModel getFirstSize = firstSize.get();
		SettingsModel getSecondType = secondType.get();

		ArrayList<SettingsModel> allModels = new ArrayList<SettingsModel>();
		allModels.add(getFirstSize);
		allModels.add(getSecondType);

		return ResponseEntity.status(HttpStatus.OK).body(allModels);
	}

	@GetMapping("{reqId:.+}/input/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename, @PathVariable String reqId) {
		System.out.println(reqId + "/input/" + filename);
		Resource file = storageService.loadAsResourceInput(reqId, filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@GetMapping("{reqId:.+}/output/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFileOutput(@PathVariable String filename, @PathVariable String reqId) {

		Resource file = storageService.loadAsResourceOutput(reqId, filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
}
