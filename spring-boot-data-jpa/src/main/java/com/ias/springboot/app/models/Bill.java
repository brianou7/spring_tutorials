package com.ias.springboot.app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="bills")
public class Bill implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private String description;
	private String observation;
	
	@Temporal(TemporalType.DATE)
	@Column(name="created_at")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date createdAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="client_id")
	private Client client;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="bill_id")
	private List<BillItem> items;
	
	public Bill() {
		this.items = new ArrayList<BillItem>();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getObservation() {
		return observation;
	}
	public void setObservation(String observation) {
		this.observation = observation;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	
	public List<BillItem> getItems() {
		return items;
	}
	
	public void setItems(List<BillItem> items) {
		this.items = items;
	}
	
	public void addItem(BillItem item) {
		this.items.add(item);
	}
	
	@Override
	public String toString() {
		return "Bill [id=" + id + ", description=" + description + ", observation=" + observation + ", createdAt="
				+ createdAt + ", client=" + client + "]";
	}

	@PrePersist public void pre_persist() {
		this.createdAt = new Date();
	}
	
	public Double getTotal() {
		Double total = 0.0;
		
		for (int i = 0; i < this.items.size(); i++) {
			total += this.items.get(i).calculateImport();
		}
		
		return total;
	}

	private static final long serialVersionUID = 1L;
}
