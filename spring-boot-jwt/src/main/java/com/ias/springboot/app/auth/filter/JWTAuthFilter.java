package com.ias.springboot.app.auth.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User ;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTAuthFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authManager;

	public JWTAuthFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login", "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		// String username = request.getParameter("username");
		// String password = request.getParameter("password");
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		
		if (username != null && password != null) {
			logger.info("Username from request parameter (form-data): ".concat(username));
			logger.info("Password from request parameter (form-data): ".concat(password));
		} else {
			com.ias.springboot.app.models.User user = null;
			
			try {
				user = new ObjectMapper().readValue(request.getInputStream(), com.ias.springboot.app.models.User.class);
				username = user.getUsername();
				password = user.getPassword();

				logger.info("Username from request InputStream (raw): ".concat(username));
				logger.info("Password from request InputStream (raw): ".concat(password));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		username = username.trim();
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

		return authManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String username = ((User) authResult.getPrincipal()).getUsername();
		// String username = authResult.getName();
		Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
		
		Claims claims = Jwts.claims();
		claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
		
		String token = Jwts.builder()
				.setClaims(claims)
				.setSubject(username)
				.signWith(Keys.hmacShaKeyFor("Anyone.Secure.Key.With.Many.Charactheres.123456.And.A.Bit.More.Beacause.It.is.not.enougth".getBytes()), SignatureAlgorithm.HS512)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 3600000L * 1)) // Hours
				.compact();
		response.addHeader("Authorization", "Bearer ".concat(token));
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("token", token);
		body.put("user", (User) authResult.getPrincipal());
		body.put("message", String.format("Hello %s, you have successfully logged in!", username));
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(200);
		response.setContentType("application/json");
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("message", "Authentication error: username or password invalid!");
		body.put("error", failed.getMessage());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
	}
		
}
