package br.com.elton.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.elton.config.FileStorageConfig;
import br.com.elton.exceptions.FileStorageException;
import br.com.elton.exceptions.MyFileNotFoundException;

@Service
public class FileStorageServices {
	private final Path path;

	public FileStorageServices(FileStorageConfig config) {
		Path path = Paths.get(config.getUploadDir()).toAbsolutePath().normalize();
		this.path = path;
		try {
			Files.createDirectories(this.path);
		} catch (Exception e) {
			// TODO: handle exception
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored!",
					e);
		}
	}

	public String storeFile(MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (fileName.contains(".."))
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			Path path = this.path.resolve(fileName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		} catch (Exception e) {
			// TODO: handle exception
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
		}
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path path = this.path.resolve(fileName).normalize();
			Resource resource = new UrlResource(path.toUri());
			if (resource.exists())
				return resource;
			throw new MyFileNotFoundException("File not found");
		} catch (Exception e) {
			// TODO: handle exception
			throw new MyFileNotFoundException("File not found " + fileName, e);
		}
	}
}
