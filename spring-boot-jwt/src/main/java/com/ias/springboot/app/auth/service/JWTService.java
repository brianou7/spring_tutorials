package com.ias.springboot.app.auth.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ias.springboot.app.auth.SimpleGranteAuthorityMixin;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTService implements IJWTService {
	
	public static final String SECRET_KEY = Base64Utils.encodeToString("Anyone.Secure.Key.With.Many.Charactheres.123456.And.A.Bit.More.Beacause.It.is.not.enougth".getBytes()) ;
	
	public static final long EXPIRATION_DATE = 3600000L * 4; // Hours
	
	public static final String TOKEN_PREFIX = "Bearer ";
	
	public static final String HEADER_KEY = "Authorization";

	@Override
	public String create(Authentication auth) throws JsonProcessingException {
		String username = auth.getName();
		Collection<? extends GrantedAuthority> roles = auth.getAuthorities();

		Claims claims = Jwts.claims();
		claims.put("authorities", new ObjectMapper().writeValueAsString(roles));

		String token = Jwts.builder().setClaims(claims).setSubject(username).signWith(Keys.hmacShaKeyFor(
				SECRET_KEY.getBytes()),
				SignatureAlgorithm.HS512).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE))
				.compact();
		return token;
	}

	@Override
	public boolean validate(String token) {
		try {
			getClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public Claims getClaims(String token) {
		return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(
				SECRET_KEY.getBytes()))
				.parseClaimsJws(resolve(token)).getBody();
	}

	@Override
	public String getUsername(String token) {
		return getClaims(token).getSubject();
	}

	@Override
	public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException {
		Collection<? extends GrantedAuthority> authorities = Arrays.asList(new ObjectMapper()
				.addMixIn(SimpleGrantedAuthority.class, SimpleGranteAuthorityMixin.class)
				.readValue(getClaims(token).get("authorities").toString().getBytes(), SimpleGrantedAuthority[].class));
		return authorities;
	}

	@Override
	public String resolve(String token) {
		if (token != null && token.startsWith(TOKEN_PREFIX)) {
			return token.replaceAll(TOKEN_PREFIX, "");
		}
		
		return null;
	}

}
