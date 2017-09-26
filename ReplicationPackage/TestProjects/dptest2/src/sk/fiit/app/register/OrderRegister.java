package sk.fiit.app.register;

import java.util.UUID;

import sk.fiit.app.data.DataHolder;
import sk.fiit.app.entity.Customer;
import sk.fiit.app.entity.Order;

public class OrderRegister {

	public void registerOrder(String custName, String orderInfo) {
		String id = UUID.randomUUID().toString();
		
		Order order = new Order(id);
		DataHolder dataHolder = DataHolder.getInstance();
		
		Customer customer;
		customer = dataHolder.searchCustomerByName(custName);
		int orderSize;
		orderSize = dataHolder.numberOfClients();
		order.setCustomer(customer);
		order.setOrderInfo(orderInfo);	
		//fillData(order, customer, orderInfo);
		for(int i=0; i<orderSize; i++) {
			if(customer!=null) {
				dataHolder.addOrder(order);
				order.execute();
			}
		}

	}
	
	private void fillData(Order order, Customer customer, String orderInfo) {
		order.setCustomer(customer);
		order.setOrderInfo(orderInfo);	
	}
	
	public void daco() {
		//if(customer!=null) {
			//int orderSize;
			//orderSize = dataHolder.numberOfClients();
			//for(int i=0; i<orderSize; i++) {
				//order.setCustomer(customer);
			//	order.setOrderInfo(orderInfo);	

			//}
		//}
	}
}
