package com.finance.velocity.DAO.entity;

import java.time.Instant;

public class Transaction {
	private String id;
	private String customerId;
	private Double loadAmount;
	private Instant time;
	
	
	public Transaction(String id2, String customer_id, Double loadAmount, Instant time) {
		super();
		this.id = id2;
		this.customerId = customer_id;
		this.loadAmount = loadAmount;
		this.time = time;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Double getLoadAmount() {
		return loadAmount;
	}
	public void setLoadAmount(Double loadAmount) {
		this.loadAmount = loadAmount;
	}
	public Instant getTime() {
		return time;
	}
	public void setTime(Instant time) {
		this.time = time;
	}
	
	
}
