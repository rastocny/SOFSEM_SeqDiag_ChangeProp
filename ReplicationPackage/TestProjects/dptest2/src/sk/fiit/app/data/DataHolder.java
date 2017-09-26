package sk.fiit.app.data;

import java.util.ArrayList;
import java.util.List;

import sk.fiit.app.entity.Customer;
import sk.fiit.app.entity.Order;

public class DataHolder {

	private static DataHolder INSTANCE;
	private List<Customer> customers;
	private List<Order> orders;
	
	private DataHolder() {
		orders = new ArrayList<>();
		customers = new ArrayList<>();
	}
	
	public int numberOfClients() {
		return customers.size();
	}
	
 	public static DataHolder getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DataHolder();
		}
		return INSTANCE;
	}
	
	public List<Customer> getCustomers() {
		return this.customers;
	}
	
	public List<Order> getOrders() {
		return this.orders;
	}
	
	public void addCustomer(Customer cust) {
		this.customers.add(cust);
	}
	
	public void addOrder(Order ord) {
		String orderInfo = ord.getOrderInfo();
		if(orderInfo != "") {
			this.orders.add(ord);
		}
	}
	
	public Customer searchCustomerByName(String name) {
		int customerSize;
		Customer customer = null;
		customerSize = customers.size() - 1;
		for(int i = 0; i<customerSize; i++) {
			String lastName = "";
			Customer cust = null;
			cust = customers.get(i);
			
			name = cust.getLastName();
			if(lastName == name) {
				customer = cust;
			}
		}
		return customer;
	}
}
