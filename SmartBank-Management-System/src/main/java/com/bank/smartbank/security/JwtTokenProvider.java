package com.bank.smartbank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final long jwtExpirationMs;

	public JwtTokenProvider(
			@Value("${jwt.secret}") String jwtSecret,
			@Value("${jwt.expiration:86400000}") long jwtExpirationMs) {
		this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		this.jwtExpirationMs = jwtExpirationMs;
	}

	public String generateToken(Long userId, String email, String role) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder()
				.subject(String.valueOf(userId))
				.claim("email", email)
				.claim("role", role)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(secretKey)
				.compact();
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = parseClaims(token);
		return Long.parseLong(claims.getSubject());
	}

	public String getEmailFromToken(String token) {
		Claims claims = parseClaims(token);
		return claims.get("emails", String.class);
	}

	public String getRoleFromToken(String token) {
		Claims claims = parseClaims(token);
		return claims.get("role", String.class);
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			System.out.println("Invalid JWT token: " + e.getMessage());
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
	}
}
