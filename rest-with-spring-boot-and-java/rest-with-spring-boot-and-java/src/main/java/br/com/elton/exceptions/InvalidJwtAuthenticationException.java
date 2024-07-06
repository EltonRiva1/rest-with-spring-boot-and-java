package br.com.elton.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidJwtAuthenticationException extends AuthenticationException {

	public InvalidJwtAuthenticationException(String string) {
		// TODO Auto-generated constructor stub
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
