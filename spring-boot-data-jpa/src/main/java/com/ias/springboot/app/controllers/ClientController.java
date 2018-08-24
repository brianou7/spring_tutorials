package com.ias.springboot.app.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ias.springboot.app.models.Client;
import com.ias.springboot.app.services.IClientService;
import com.ias.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("client")
public class ClientController {
	
	@Autowired
	private IClientService client_service;
	private static final String url = "/list";

	@RequestMapping(value=url, method=RequestMethod.GET)
	public String list(@RequestParam(name="page", defaultValue="0") int page, Model model) {
		Pageable pageRequest = new PageRequest(page, 4);
		Page<Client> clients = client_service.find_all(pageRequest);
		PageRender<Client> pageRender = new PageRender<Client>(url, clients);
		
		model.addAttribute("title", "Clients list");
		model.addAttribute("clients", clients);
		model.addAttribute("page", pageRender);
		return "client_list";
	}
	
	@RequestMapping(value="/save", method=RequestMethod.GET)
	public String create(Map<String, Object> model) {
		Client client = new Client();
		model.put("client", client);
		model.put("title",  "Save client form");
		return "client_save";
	}
	
	@RequestMapping(value="/save/{id}")
	public String update(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Client client = null;
		
		if (id > 0) {
			client = client_service.find_one(id);
			
			if (client == null) {
				flash.addFlashAttribute("error", "The client ID does not exists in the database!");
				return "redirect:/list";
			}
		}else {
			flash.addFlashAttribute("error", "The client ID can not be zero!");
			return "redirect:/list";
		}
		
		model.put("client", client);
		model.put("title", "Update client form");
		return "client_save";
	}
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public String save(@Valid Client client, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {
		
		if(result.hasErrors()) {
			model.addAttribute("title", "Save client form");
			return "client_save";
		}
		client_service.save(client);
		status.setComplete();
		String flashMessage = (client.getId() != null) ? "Client updated successfully!": "Client created successfully";
		flash.addFlashAttribute("success", flashMessage);
		return "redirect:list";
	}
	
	@RequestMapping(value="/delete/{id}")
	public String delete(@PathVariable(value="id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			client_service.delete(id);
		}
		
		flash.addFlashAttribute("success", "Client deleted successfully!");
		return "redirect:/list";
	}
}
