package com.ias.springboot.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ias.springboot.app.services.IClientService;
import com.ias.springboot.app.view.xml.ClientList;

@RestController
@RequestMapping("/api/clients")
public class ClientRestController {
	
	@Autowired
	private IClientService clientService;

	@GetMapping(value="/list")
	public ClientList list() {
		return new ClientList(clientService.find_all());
	}

}
