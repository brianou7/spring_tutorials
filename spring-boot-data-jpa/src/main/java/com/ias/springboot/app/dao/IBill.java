package com.ias.springboot.app.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ias.springboot.app.models.Bill;

public interface IBill extends CrudRepository<Bill, Long> {

	@Query("select b from Bill b join fetch b.client c join fetch b.items bi join fetch bi.product p where b.id = ?1")
	public Bill fetchByIdWithClientWithBillItemWithProduct(Long id);
}
