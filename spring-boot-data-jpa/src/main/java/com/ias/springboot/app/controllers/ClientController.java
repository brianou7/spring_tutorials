package com.ias.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
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
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private IClientService client_service;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	private final static String LIST_URL = "/list";

	@Secured("ROLE_USER")
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
	
	@PreAuthorize("hasRole('ROLE_USER')")
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
	
	@RequestMapping(value= {LIST_URL, "/"}, method=RequestMethod.GET)
	public String list(@RequestParam(name="page", defaultValue="0") int page, Model model,
			Authentication authentication, HttpServletRequest request) {
		if (authentication != null) {
			logger.info("Hello authenticated user, your username is: ".concat(authentication.getName()));
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null) {
			logger.info("Using static way SecurityContextHolder.getContext().getAuthentication(): Authenticated user, username: ".concat(auth.getName()));
		}
		
		if(hasRole("ROLE_ADMIN")) {
			logger.info("Hello ".concat(auth.getName()).concat(" have access!"));
		}else {
			logger.info("Hello ".concat(auth.getName()).concat(" do not have access!"));
		}
		
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "");
		
		if (securityContext.isUserInRole("ROLE_ADMIN")) {
			logger.info("Other way using SecurityContextHolderAwareRequestWrapper: Hello username: ".concat(auth.getName()).concat(" you have access!"));
		}else {
			logger.info("Other way using SecurityContextHolderAwareRequestWrapper: Hello username: ".concat(auth.getName()).concat(" you don't have access!"));
		}
		
		if (request.isUserInRole("ROLE_ADMIN")) {
			logger.info("Other way using HttpServletRequest: Hello username: ".concat(auth.getName()).concat(" you have access!"));
		}else {
			logger.info("Other way using HttpServletRequest: Hello username: ".concat(auth.getName()).concat(" you don't have access!"));
		}
		
		Pageable pageRequest = PageRequest.of(page, 4);
		Page<Client> clients = client_service.find_all(pageRequest);
		PageRender<Client> pageRender = new PageRender<Client>(LIST_URL, clients);
		
		model.addAttribute("title", "Clients list");
		model.addAttribute("clients", clients);
		model.addAttribute("page", pageRender);
		return "client_list";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/save", method=RequestMethod.GET)
	public String create(Map<String, Object> model) {
		Client client = new Client();
		model.put("client", client);
		model.put("title",  "Save client form");
		return "client_save";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	
	private boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		
		if (context == null) {
			return false;
		}
		
		Authentication auth = context.getAuthentication();
		
		if (auth == null) {
			return false;
		}
		
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		return authorities.contains(new SimpleGrantedAuthority(role));
		
		/*for (GrantedAuthority authority: authorities) {
			if(role.equals(authority.getAuthority())) {
				logger.info("Hello ".concat(auth.getName()).concat(" your role is: ").concat(authority.getAuthority()));
				return true;
			}
		}
		
		return false;*/
	}
}
