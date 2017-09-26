package sk.fiit.app.test;

import sk.fiit.app.data.DataHolder;
import sk.fiit.app.entity.Customer;
import sk.fiit.app.entity.Order;

public class ShopTestCreator {

	public void addDummyData() {
		DataHolder dataHolder = DataHolder.getInstance();
		Customer customer1 = new Customer("Andrej","Mlyncar","23");
		
		dataHolder.addCustomer(customer1);
		
		dataHolder.addCustomer(new Customer("Ferko","Mrkvicka","21"));
		
		Customer customer2 = new Customer("Janko","Hrasko","41");
		dataHolder.addCustomer(customer2);
		
		dataHolder.addOrder(new Order(customer1, "TV", "1"));
		dataHolder.addOrder(new Order(customer1, "PC", "2"));
		dataHolder.addOrder(new Order(customer2, "Iphone", "3"));	
		
	}
}
