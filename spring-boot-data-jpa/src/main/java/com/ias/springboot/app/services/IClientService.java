package com.ias.springboot.app.services;

import java.util.List;

import com.ias.springboot.app.models.Client;

public interface IClientService {

public List<Client> find_all();
	
	public void save(Client client);
	
	public Client find_one(Long id);

	public void delete(Long id);
	
}
