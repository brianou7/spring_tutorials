package com.ias.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	@GetMapping(value="/login")
	public String login(@RequestParam(value="error", required=false) String error,
			@RequestParam(value="logout", required=false) String logout,
			Model model, Principal principal, RedirectAttributes flash) {
		if(principal != null) {
			flash.addFlashAttribute("info", "You are already logged!");
			return "redirect:/";
		}
		
		if(error != null) {
			model.addAttribute("error", "Error during login: Username or password incorrect, please try again!");
		}
		
		if(logout != null) {
			model.addAttribute("success", "Session closed successfully!");
		}
		
		return "login";
	}
}
