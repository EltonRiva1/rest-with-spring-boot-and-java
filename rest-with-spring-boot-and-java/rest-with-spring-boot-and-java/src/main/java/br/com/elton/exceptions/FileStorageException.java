package br.com.elton.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageException extends RuntimeException {

	public FileStorageException(String string) {
		// TODO Auto-generated constructor stub
		super(string);
	}

	public FileStorageException(String string, Throwable throwable) {
		super(string, throwable);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
