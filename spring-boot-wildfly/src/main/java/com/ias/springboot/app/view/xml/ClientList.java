package com.ias.springboot.app.view.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ias.springboot.app.models.Client;

@XmlRootElement(name="Clients")
public class ClientList {

	@XmlElement(name="Client")
	public List<Client> clients;

	public ClientList() {
	}

	public ClientList(List<Client> clients) {
		this.clients = clients;
	}

	public List<Client> getClients() {
		return clients;
	}

}
