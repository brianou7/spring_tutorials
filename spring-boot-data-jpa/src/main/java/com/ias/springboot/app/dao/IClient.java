package com.ias.springboot.app.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.ias.springboot.app.models.Client;

public interface IClient extends PagingAndSortingRepository<Client, Long> {
	
}
