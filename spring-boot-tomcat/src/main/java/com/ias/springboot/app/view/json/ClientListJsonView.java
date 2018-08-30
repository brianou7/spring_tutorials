package com.ias.springboot.app.view.json;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.ias.springboot.app.models.Client;

@SuppressWarnings("unchecked")
@Component("client_list.json")
public class ClientListJsonView extends MappingJackson2JsonView{

	@Override
	protected Object filterModel(Map<String, Object> model) {
		Page<Client> clients = (Page<Client>) model.get("clients");
		
		model.remove("clients");
		model.remove("title");
		model.remove("page");
		model.put("clients", clients.getContent());
		return super.filterModel(model);
	}

}
