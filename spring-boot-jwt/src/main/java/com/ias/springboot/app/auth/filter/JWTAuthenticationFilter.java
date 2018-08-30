package com.ias.springboot.app.auth.filter;

import java.io.IOException;
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
import org.springframework.security.core.userdetails.User ;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ias.springboot.app.auth.service.JWTService;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authManager;
	
	private JWTService jwtService;

	public JWTAuthenticationFilter(AuthenticationManager authManager, JWTService jwtService) {
		this.authManager = authManager;
		this.jwtService = jwtService;
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
		String token = jwtService.create(authResult);
		response.addHeader(JWTService.HEADER_KEY, JWTService.TOKEN_PREFIX.concat(token));
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("token", token);
		body.put("user", (User) authResult.getPrincipal());
		body.put("message", String.format("Hello %s, you have successfully logged in!", authResult.getName()));
		
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
