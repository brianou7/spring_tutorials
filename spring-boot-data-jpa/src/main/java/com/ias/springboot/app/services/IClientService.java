package com.ias.springboot.app.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ias.springboot.app.models.Client;

public interface IClientService {

	public List<Client> find_all();

	public Page<Client> find_all(Pageable pageable);
	
	public void save(Client client);
	
	public Client find_one(Long id);

	public void delete(Long id);
	
}
