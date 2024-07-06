package br.com.elton.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.elton.data.vo.v1.security.AccountCredentialsVO;
import br.com.elton.data.vo.v1.security.TokenVO;
import br.com.elton.repositories.UserRepository;
import br.com.elton.security.jwt.JwtTokenProvider;

@Service
public class AuthServices {
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository repository;

	@SuppressWarnings("rawtypes")
	public ResponseEntity signin(AccountCredentialsVO accountCredentialsVO) {
		try {
			var username = accountCredentialsVO.getUsername();
			var password = accountCredentialsVO.getPassword();
			this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			var user = this.repository.findByUsername(username);
			var tokenResponse = new TokenVO();
			if (user != null) {
				tokenResponse = this.jwtTokenProvider.createAccessToken(username, user.getRoles());
			} else {
				throw new UsernameNotFoundException("Username " + username + " not found!");
			}
			return ResponseEntity.ok(tokenResponse);
		} catch (Exception e) {
			// TODO: handle exception
			throw new BadCredentialsException("Invalid username/password supplied!");
		}
	}

	@SuppressWarnings("rawtypes")
	public ResponseEntity refreshToken(String username, String refreshToken) {
		var user = this.repository.findByUsername(username);
		var tokenResponse = new TokenVO();
		if (user != null) {
			tokenResponse = this.jwtTokenProvider.refreshToken(refreshToken);
		} else {
			throw new UsernameNotFoundException("Username " + username + " not found!");
		}
		return ResponseEntity.ok(tokenResponse);
	}
}
