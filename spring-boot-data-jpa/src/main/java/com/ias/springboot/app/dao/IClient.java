package com.ias.springboot.app.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ias.springboot.app.models.Client;

public interface IClient extends PagingAndSortingRepository<Client, Long> {

	@Query("select c from Client c left join fetch c.bills b where c.id = ?1")
	public Client fetchClientByIdWithBill(Long id);
}
