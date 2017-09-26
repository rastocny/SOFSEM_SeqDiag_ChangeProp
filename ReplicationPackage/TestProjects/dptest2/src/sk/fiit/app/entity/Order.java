package sk.fiit.app.entity;

import java.util.Date;

public class Order {

	private Customer customer;
	private Date orderDate;
	private String orderInfo;
	private final String id;
	private boolean isExecuted;
	
	public Order(String id) {
		this.orderDate = new Date();
		this.id = id;
		this.isExecuted = false;
	}
	
	public Order(Customer customer2, String string, String id) {
		super();
		this.id = id;
		this.isExecuted = false;
		this.orderInfo = string;
		this.customer = customer2;
	}

	public Customer getCustomer() {
		return customer;
	}
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public Date getOrderDate() {
		return orderDate;
	}
	
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	public String getOrderInfo() {
		return orderInfo;
	}
	
	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}
	
	public String getId() {
		return this.id;
	}
	
	public boolean isExecuted() {
		return isExecuted;
	}
	
	public void execute() {
		isExecuted = true;
	}
}
