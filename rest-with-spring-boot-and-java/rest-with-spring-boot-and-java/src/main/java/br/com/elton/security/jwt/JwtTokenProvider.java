package br.com.elton.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.elton.data.vo.v1.security.TokenVO;
import br.com.elton.exceptions.InvalidJwtAuthenticationException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtTokenProvider {
	@Value("${security.jwt.token.secret-key:secret}")
	private String secretKey = "secret";
	@Value("${security.jwt.token.expire-length:3600000}")
	private long validityInMilliseconds = 3600000;
	@Autowired
	private UserDetailsService detailsService;
	Algorithm algorithm = null;

	@PostConstruct
	protected void init() {
		this.secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
		this.algorithm = Algorithm.HMAC256(this.secretKey.getBytes());
	}

	public TokenVO createAccessToken(String username, List<String> roles) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + this.validityInMilliseconds);
		var accessToken = this.getAccessToken(username, roles, now, validity);
		var refreshToken = this.getRefreshToken(username, roles, now);
		return new TokenVO(username, accessToken, refreshToken, true, now, validity);
	}

	private String getAccessToken(String username, List<String> roles, Date now, Date validity) {
		// TODO Auto-generated method stub
		String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
		return JWT.create().withClaim("roles", roles).withIssuedAt(now).withExpiresAt(validity).withSubject(username)
				.withIssuer(issuerUrl).sign(this.algorithm).strip();
	}

	private String getRefreshToken(String username, List<String> roles, Date now) {
		// TODO Auto-generated method stub
		Date validityRefreshToken = new Date(now.getTime() + (this.validityInMilliseconds * 3));
		return JWT.create().withClaim("roles", roles).withIssuedAt(now).withExpiresAt(validityRefreshToken)
				.withSubject(username).sign(this.algorithm).strip();
	}

	public Authentication getAuthentication(String token) {
		DecodedJWT decodedJWT = this.decodedToken(token);
		UserDetails details = this.detailsService.loadUserByUsername(decodedJWT.getSubject());
		return new UsernamePasswordAuthenticationToken(details, "", details.getAuthorities());
	}

	private DecodedJWT decodedToken(String token) {
		// TODO Auto-generated method stub
		var algorithm = Algorithm.HMAC256(this.secretKey.getBytes());
		JWTVerifier jwtVerifier = JWT.require(algorithm).build();
		return jwtVerifier.verify(token);
	}

	public String resolveToken(HttpServletRequest httpServletRequest) {
		String bearerToken = httpServletRequest.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring("Bearer ".length());
		}
		return null;
	}

	public boolean validateToken(String token) {
		DecodedJWT decodedJWT = this.decodedToken(token);
		try {
			if (decodedJWT.getExpiresAt().before(new Date())) {
				return false;
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			throw new InvalidJwtAuthenticationException("Expired or invalid JWT token!");
		}
	}

	public TokenVO refreshToken(String refreshToken) {
		if (refreshToken.contains("Bearer "))
			refreshToken = refreshToken.substring("Bearer ".length());
		JWTVerifier jwtVerifier = JWT.require(this.algorithm).build();
		DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);
		String username = decodedJWT.getSubject();
		List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
		return this.createAccessToken(username, roles);
	}
}
