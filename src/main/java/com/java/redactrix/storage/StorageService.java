package com.java.redactrix.storage;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.opencv.core.Mat;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface StorageService {

	void init();

	void store(MultipartFile file);

	void store(MultipartFile file, String reqId, int fileNo, String extension);

	String store(Mat img, String reqId, String fileName); // storing the output

	Stream<Path> loadAllInput();

	Stream<Path> loadAllOutput();

	Stream<Path> loadAllInputWReqId(String reqId);

	Path loadInput(String reqId, String fileName);

	Resource loadAsResourceInput(String reqId, String fileNames);

	Path loadOutput(String reqId, String filename);

	Resource loadAsResourceOutput(String reqId, String filename);

	Path getOutputPath();

	void deleteAll();

	Boolean check(String reqId, String fileName);

	
}
