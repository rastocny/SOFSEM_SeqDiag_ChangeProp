package sk.fiit.app.register;

import sk.fiit.app.data.DataHolder;
import sk.fiit.app.entity.Customer;

public class CustomerRegister {

	public void registerCustomer(String name, String lastName, String age) {
		
		Customer customer = new Customer();
		customer.setName(name);
		customer.setAge(age);
		customer.setLastName(lastName);	
		
		DataHolder dataHolder = DataHolder.getInstance();
		//add fragment if exists
		dataHolder.addCustomer(customer);
		
	}
}
