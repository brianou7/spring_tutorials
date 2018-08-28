package com.ias.springboot.app.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ias.springboot.app.models.Bill;
import com.ias.springboot.app.models.BillItem;
import com.ias.springboot.app.models.Client;
import com.ias.springboot.app.models.Product;
import com.ias.springboot.app.services.IClientService;

@Secured("ROLE_ADMIN")
@Controller
@RequestMapping("/bill")
@SessionAttributes("bill")
public class BillController {
	
	@Autowired
	private IClientService clientService;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@GetMapping("/view/{id}")
	public String view(@PathVariable(value="id") Long id, Model model, RedirectAttributes flash) {
		Bill bill = clientService.fetchBillByIdWithClientWithBillItemWithProduct(id); // clientService.findBillById(id);
		
		if (bill == null) {
			flash.addAttribute("error", "The bill does not exist on database!");
			return "redirect:/list";
		}
		
		model.addAttribute("title", "Bill: ".concat(bill.getDescription()));
		model.addAttribute("bill", bill);
		return "bill/view";
	}

	@GetMapping("/form/{clientId}")
	public String create(@PathVariable(value="clientId") Long clientId,
			Map<String, Object> model,
			RedirectAttributes flash) {
		Client client = clientService.find_one(clientId);
		
		if(client == null && clientId > 0) {
			flash.addFlashAttribute("error", "The client does not exist on database!");
			return "redirect:/list";
		}
		
		Bill bill = new Bill();
		bill.setClient(client);
		model.put("bill", bill);
		model.put("title", "Create bill");
		
		return "bill/form";
	}
	
	@GetMapping(value="/load-products/{name}", produces= {"application/json"})
	public @ResponseBody List<Product> loadProduct(@PathVariable String name){
		return clientService.findByName(name);
	}
	
	@PostMapping(value="/form")
	public String save(@Valid Bill bill,
			BindingResult result, Model model,
			@RequestParam(name="item_id[]", required=false) Long[] itemId,
			@RequestParam(name="amount[]", required=false) Integer[] amount,
			RedirectAttributes flash, 
			SessionStatus status) {
		
		if (result.hasErrors()) {
			model.addAttribute("title", "Create bill");
			return "bill/form";
		}
		
		if(itemId == null || itemId.length == 0) {
			model.addAttribute("title", "Create bill");
			model.addAttribute("error", "The bill must have items");
			return "bill/form";
		}
		
		for(int i = 0; i <= itemId.length - 1; i++) {
			Product product = clientService.findProductById(itemId[i]);
			
			BillItem item = new BillItem();
			item.setAmount(amount[i]);
			item.setProduct(product);	
			bill.addItem(item);
			
			log.info("ID: " + itemId[i].toString() + ", amount: " + amount[i].toString());
		}
		log.info("ADADADA");
		clientService.saveBill(bill);
		log.info("ADADADA");
		status.setComplete();
		flash.addFlashAttribute("success", "Bill created successfully");
		
		return "redirect:/view/" + bill.getClient().getId();
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable(value="id") Long id,
			RedirectAttributes flash) {
		Bill bill = clientService.findBillById(id);
		
		if (bill != null) {
			clientService.deleteBill(id);
			flash.addFlashAttribute("success", "Bill deleted successfully");
			return "redirect:/view/" + bill.getClient().getId();
		}
		flash.addFlashAttribute("error", "The bill does not exist on database, can not delete");
		return "redirect:/list";
	}
}
