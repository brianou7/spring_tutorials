package com.ias.springboot.app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ias.springboot.app.dao.IClient;
import com.ias.springboot.app.models.Client;

@Service
public class ClientServiceImp implements IClientService {
	
	@Autowired
	private IClient client_dao;

	@Override
	@Transactional(readOnly=true)
	public List<Client> find_all() {
		return (List<Client>) client_dao.findAll();
	}
	
	@Override
	@Transactional(readOnly=true)
	public Client find_one(Long id) {
		return client_dao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void save(Client client) {
		client_dao.save(client);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		client_dao.deleteById(id);
	}

	@Override
	public Page<Client> find_all(Pageable pageable) {
		return client_dao.findAll(pageable);
	}

}
