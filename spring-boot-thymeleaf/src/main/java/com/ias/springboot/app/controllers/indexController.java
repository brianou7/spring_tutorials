package com.ias.springboot.app.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexController {
	
	@Value("${application.controllers.message}")
	private String message;

	@GetMapping("/")
	public String hello(Model model) {
		model.addAttribute("message", this.message);
		return "Hello";
	}
}
