package com.ias.springboot.app.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ias.springboot.app.models.Product;

public interface IProduct extends CrudRepository<Product, Long>{

	@Query("select p from Product p where p.name like %?1%")
	public List<Product> findByName(String name);
	
	public List<Product> findByNameLikeIgnoreCase(String name);
	
}
