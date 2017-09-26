package sk.fiit.app.entity;

import sk.fiit.app.register.OrderRegister;

public class Customer {

	private String name;
	private String lastName;
	private String age; //refactor to birthdate
	
	public Customer() {
		
	}
	
	public Customer(String firstName, String lastName, String age) {
		this.age = age;
		this.name = firstName;
		this.lastName = lastName;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getAge() {
		return age;
	}
	
	public void setAge(String age) {
		this.age = age;
	}
	
	public void shop(String orderInfo) {
		OrderRegister register = new OrderRegister();
		register.registerOrder(lastName, orderInfo);
	}
	
}
