package br.com.elton.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.elton.data.vo.v1.UploadFileResponseVO;
import br.com.elton.services.FileStorageServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/file/v1")
@Tag(name = "File Endpoint")
public class FileController {
	private Logger logger = Logger.getLogger(FileController.class.getName());
	@Autowired
	private FileStorageServices fileStorageServices;

	@PostMapping("/uploadFile")
	public UploadFileResponseVO uploadFile(@RequestParam("file") MultipartFile file) {
		this.logger.info("Storing file to disk");
		var fileName = this.fileStorageServices.storeFile(file);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/file/v1/downloadFile/")
				.path(fileName).toUriString();
		return new UploadFileResponseVO(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@PostMapping("/uploadMultipleFiles")
	public List<UploadFileResponseVO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		this.logger.info("Storing files to disk");
		return Arrays.asList(files).stream().map(file -> this.uploadFile(file)).collect(Collectors.toList());
	}

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest httpServletRequest) {
		this.logger.info("Reading a file on disk");
		Resource resource = this.fileStorageServices.loadFileAsResource(fileName);
		String contentType = "";
		try {
			contentType = httpServletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (Exception e) {
			// TODO: handle exception
			this.logger.info("Could not determine file type!");
		}
		if (contentType.isBlank())
			contentType = "application/octet-stream";
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
