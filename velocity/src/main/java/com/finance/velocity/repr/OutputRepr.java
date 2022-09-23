package com.finance.velocity.repr;

public class OutputRepr {
	private String id;
	private String customer_id;
	private Boolean accepted;
	
	public OutputRepr(String id, String customer_id) {
		super();
		this.id = id;
		this.customer_id = customer_id;
		this.accepted = false;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public Boolean getAccepted() {
		return accepted;
	}
	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}
	
	
}
