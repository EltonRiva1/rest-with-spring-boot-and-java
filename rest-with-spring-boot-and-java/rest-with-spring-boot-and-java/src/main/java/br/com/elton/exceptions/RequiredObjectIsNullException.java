package br.com.elton.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectIsNullException extends RuntimeException {

	public RequiredObjectIsNullException(String string) {
		// TODO Auto-generated constructor stub
		super(string);
	}

	public RequiredObjectIsNullException() {
		// TODO Auto-generated constructor stub
		super("It's now allowed to persist a null object!");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
