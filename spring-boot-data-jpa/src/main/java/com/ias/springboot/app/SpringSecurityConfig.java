package com.ias.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ias.springboot.app.auth.handler.LoginSuccessHandler;

@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder build) throws Exception{
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);
		
		build.inMemoryAuthentication()
		.withUser(users.username("admin").password("admin").roles("ADMIN", "USER"))
		.withUser(users.username("andres").password("andres").roles("USER"));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/list").permitAll()
		//.antMatchers("/view/**").hasAnyRole("USER")
		//.antMatchers("/uploads/**").hasAnyRole("USER")
		//.antMatchers("/save/**").hasAnyRole("ADMIN")
		//.antMatchers("/delete/**").hasAnyRole("ADMIN")
		//.antMatchers("/bill/**").hasAnyRole("ADMIN")
		.anyRequest().authenticated()
		.and()
		.formLogin().successHandler(successHandler).loginPage("/login").permitAll()
		.and()
		.logout().permitAll()
		.and()
		.exceptionHandling().accessDeniedPage("/error403");
	}
	
	
}
