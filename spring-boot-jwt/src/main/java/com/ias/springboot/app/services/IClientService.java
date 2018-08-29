package com.ias.springboot.app.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ias.springboot.app.models.Bill;
import com.ias.springboot.app.models.Client;
import com.ias.springboot.app.models.Product;

public interface IClientService {

	public List<Client> find_all();

	public Page<Client> find_all(Pageable pageable);
	
	public void save(Client client);
	
	public Client find_one(Long id);
	
	public Client fetchClientByIdWithBill(Long id);

	public void delete(Long id);
	
	public List<Product> findByName(String name);
	
	public void saveBill(Bill bill);
	
	public Product findProductById(Long id);
	
	public Bill findBillById(Long id);
	
	public void deleteBill(Long id);
	
	public Bill fetchBillByIdWithClientWithBillItemWithProduct(Long id);
}
