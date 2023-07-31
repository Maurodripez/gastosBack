package com.mauricio.gastos.securityjwt;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtils {

	@Value("${jwt.secret.key}")
	private String secretkey;

	@Value("${jwt.time.expiration}")
	private String timeExpiration;
	
	
	// Generar token de acceso
	public String generateAccessToken(String userName) {
		return Jwts.builder()
				.setSubject(userName)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(timeExpiration)))
				.signWith(getSignatureKey(),SignatureAlgorithm.HS256)
				.compact();
	}

	//Generar token para verificar correo
	public String generateTokenValidate(String userName) {
		return Jwts.builder()
				.setSubject(userName)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(String.valueOf(24 * 60 * 60 * 1000))))
				.signWith(getSignatureKey(),SignatureAlgorithm.HS256)
				.compact();
	}
	
	// Validar el token de acceso
	public boolean isTokenValid(String token) {
		try {
			Jwts.parserBuilder()
			.setSigningKey(getSignatureKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
			return true;
		} catch (Exception e) {
			log.info("Token invalido:" + e.getMessage());
			return false;
		}
	}
	
	//Obtener username
	public String getUsernameFromToken(String token) {
		return getClaim(token, Claims::getSubject);
	}
	
	//obtener un solo claim
	public <T> T getClaim(String token,Function<Claims, T> claimsTFunction) {
		Claims claims = extractAllClaims(token);
		return claimsTFunction.apply(claims);
	}
	
	//Obtener todos los claims
	public Claims extractAllClaims(String token) {
		return 	Jwts.parserBuilder()
				.setSigningKey(getSignatureKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	//Obtener firma del token
	public Key getSignatureKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretkey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	// Obtener una clave segura
	//se genera una clave segura siempre
	@SuppressWarnings("unused")
	private Key getSecureKey() {
	    return Keys.secretKeyFor(SignatureAlgorithm.HS256);
	}
}
