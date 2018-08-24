package com.ias.springboot.app.dao;

import org.springframework.data.repository.CrudRepository;

import com.ias.springboot.app.models.Client;

public interface IClient extends CrudRepository<Client, Long> {
	
}
