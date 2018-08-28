package com.ias.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ias.springboot.app.models.Client;
import com.ias.springboot.app.services.IClientService;
import com.ias.springboot.app.services.IUploadFileService;
import com.ias.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("client")
public class ClientController {
	
	@Autowired
	private IClientService client_service;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	private final static String LIST_URL = "/list";

	@RequestMapping(value="/uploads/{filename:.+}")
	public ResponseEntity<Resource> viewPhoto(@PathVariable String filename){
		Resource resource = null;
		
		try {
			resource = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename \"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	@RequestMapping(value="/view/{id}")
	public String view(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Client client = client_service.fetchClientByIdWithBill(id);// client_service.find_one(id);
		
		if (client == null) {
			flash.addFlashAttribute("error", "The client does not exist in the database!");
			return "redirect:/list";
		}
		
		model.put("client", client);
		model.put("title", "Client detail: " + client.getFirst_name());
		return "client_detail";
	}
	@RequestMapping(value=LIST_URL, method=RequestMethod.GET)
	public String list(@RequestParam(name="page", defaultValue="0") int page, Model model) {
		Pageable pageRequest = new PageRequest(page, 4);
		Page<Client> clients = client_service.find_all(pageRequest);
		PageRender<Client> pageRender = new PageRender<Client>(LIST_URL, clients);
		
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
	public String save(@Valid Client client, BindingResult result, Model model, @RequestParam("file") MultipartFile photo, RedirectAttributes flash, SessionStatus status) {
		if(result.hasErrors()) {
			model.addAttribute("title", "Save client form");
			return "client_save";
		}
		
		if (!photo.isEmpty()) {
			if (client.getId() != null
					&& client.getId() > 0
					&& client.getPhoto() != null
					&& client.getPhoto().length() > 0) {
				uploadFileService.delete(client.getPhoto());
				
			}
			
			String uniqueFilename = null;
			
			try {
				uniqueFilename = uploadFileService.copy(photo);
				flash.addFlashAttribute("info", "Has upload correctly '" + uniqueFilename + "'");
				client.setPhoto(uniqueFilename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				flash.addFlashAttribute("error", "Has occured a problem, upload a valid image!");
				e.printStackTrace();
			}
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
			Client client = client_service.find_one(id);
			client_service.delete(id);
			flash.addFlashAttribute("success", "Client deleted successfully!");
			
			if (uploadFileService.delete(client.getPhoto())) {
				flash.addFlashAttribute("info", "Photo " + client.getPhoto() + " deleted successfully");
			}
		}
		
		return "redirect:/list";
	}
}
