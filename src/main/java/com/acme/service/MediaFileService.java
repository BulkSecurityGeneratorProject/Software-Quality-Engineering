package com.acme.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Path;

@Service
public class MediaFileService {

	private final Logger log = LoggerFactory.getLogger(MediaFileService.class);

	private final static String MEDIA_FOLDER = new File(".").getAbsolutePath()+"/mediaResources/";

	public List<File> getMediaFileList() {
		return Arrays.asList(new File(MEDIA_FOLDER).listFiles(file -> file.isFile() && !file.getName().startsWith(".")));
	}

	public File getMediaFile(String fileName) {
		System.out.println("I am trying to access filename: " + fileName);
		File file = new File(MEDIA_FOLDER + fileName);

		java.nio.file.Path mediaPath = Paths.get(MEDIA_FOLDER).normalize().toAbsolutePath();
		java.nio.file.Path filePath = Paths.get(file.getAbsolutePath()).normalize().toAbsolutePath();

		if (!filePath.startsWith(mediaPath) || filePath.compareTo(mediaPath) == 0) {
			return null;
		}

		if (file.exists() && file.canRead() && file.isFile()) {
			return file;
		}

		return null;
	}

	public void saveMediaFile(MultipartFile multiPartFile) throws IOException {
		File file = new File(MEDIA_FOLDER + multiPartFile.getOriginalFilename());

		java.nio.file.Path mediaPath = Paths.get(MEDIA_FOLDER).normalize().toAbsolutePath();
		java.nio.file.Path filePath = Paths.get(file.getAbsolutePath()).normalize().toAbsolutePath();

		if (!filePath.startsWith(mediaPath) || filePath.compareTo(mediaPath) == 0) {
			throw new RuntimeException("Illegal file name");
		}

		multiPartFile.transferTo(file);
	}

	public boolean deleteMediaFile(String fileName) {
		File file = new File(MEDIA_FOLDER + fileName);

		java.nio.file.Path mediaPath = Paths.get(MEDIA_FOLDER).normalize().toAbsolutePath();
		java.nio.file.Path filePath = Paths.get(file.getAbsolutePath()).normalize().toAbsolutePath();

		if (!filePath.startsWith(mediaPath) || filePath.compareTo(mediaPath) == 0) {
			return false;
		}

		return file.exists() && file.canWrite() && file.delete();
	}
}
