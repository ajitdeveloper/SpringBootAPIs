package com.java.redactrix.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;
	private final Path detectLocation;
	private final String rootStringLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
		this.detectLocation = Paths.get(properties.getDetectLoc());
		this.rootStringLocation = properties.getLocation();
	}

	@Override
	public void store(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
	}

	// passing multipart file aswell as req id
	@Override
	public void store(MultipartFile file, String reqId, int fileNo, String extension) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Path path = Paths.get(reqId + "/" + rootStringLocation);
				if (!Files.exists(path)) {
					try {
						Files.createDirectories(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Files.copy(inputStream, path.resolve(fileNo + "." + extension), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
	}

	/*
	 * public void store (Mat outputFile) {
	 * 
	 * try (InputStream inputStream = ((InputStreamSource)
	 * outputFile).getInputStream()) { Files.copy(inputStream, this.detectLocation,
	 * StandardCopyOption.REPLACE_EXISTING);
	 * 
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 * 
	 */
	@Override
	public Path getOutputPath() {
		return this.detectLocation;
	}

//	public Path getDummyPath() {
//		return null;
//	}
//	
	@Override
	public String store(Mat img, String reqId, String filename) {
		// String fileName = this.detectLocation.toAbsolutePath().toString() + "/" +
		// filename;
		String fileName = reqId + "/output/" + filename;
		try {
			Imgcodecs.imwrite(fileName, img);
			return fileName;
		} catch (Exception e) {
			throw new StorageException("Failed to store file " + fileName, e);
		}

	}

	@Override
	public Boolean check(String reqId, String fileName) {
		// TODO Auto-generated method stub
		Path path = Paths.get(reqId + "/output");
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		Path checkFilePath = Paths.get(reqId + "/output/" + fileName);
		if (!Files.exists(checkFilePath)) {
			return false;
		}
		return true;
	}

	// load all input files as path stream and request id generator

	@Override
	public Stream<Path> loadAllInput() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Stream<Path> loadAllInputWReqId(String reqId) {
		Path inputPath = Paths.get(reqId + "/" + rootStringLocation);
		try {
			return Files.walk(inputPath, 1).filter(path -> !path.equals(inputPath)).map(inputPath::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

//		return null;
	}

	@Override
	public Stream<Path> loadAllOutput() {
		try {
			return Files.walk(this.detectLocation, 1).filter(path -> !path.equals(this.detectLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path loadInput(String reqId, String fileName) {
		Path path = Paths.get(reqId + "/input/" + fileName);
		// System.out.println(path.toString());
		// return rootLocation.resolve(filename);
		return path;
	}

	@Override
	public Resource loadAsResourceInput(String reqId, String fileName) {
		try {
			Path file = loadInput(reqId, fileName);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + fileName);
			}

		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + fileName, e);
		}

	}

	@Override
	public Path loadOutput(String reqId, String fileName) {
		Path path = Paths.get(reqId + "/output/" + fileName);
		return path;
	}

	@Override
	public Resource loadAsResourceOutput(String reqId, String filename) {
		try {
			Path file = loadOutput(reqId, filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);

			}
		}

		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
