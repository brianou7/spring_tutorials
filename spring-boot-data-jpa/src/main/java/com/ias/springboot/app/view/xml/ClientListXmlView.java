package com.ias.springboot.app.view.xml;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

import com.ias.springboot.app.models.Client;

@Component("client_list.xml")
public class ClientListXmlView extends MarshallingView{
	
	@Autowired
	public ClientListXmlView(Jaxb2Marshaller marshaller) {
		super(marshaller);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Page<Client> clients = (Page<Client>) model.get("clients");
		
		model.remove("title");
		model.remove("page");
		model.remove("clients");
		model.put("clients", new ClientList(clients.getContent()));
		super.renderMergedOutputModel(model, request, response);
	}

	
}
