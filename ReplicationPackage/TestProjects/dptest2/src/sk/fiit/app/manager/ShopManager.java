package sk.fiit.app.manager;

import java.util.List;

import sk.fiit.app.data.DataHolder;
import sk.fiit.app.entity.Order;

public class ShopManager {

	
	public void displayAllOrders() {
		int size;
		DataHolder dataHolder = DataHolder.getInstance();
		size = dataHolder.numberOfClients();
		List<Order> orders = null;
		orders = dataHolder.getOrders();
		int orderSize = orders.size();
		for(int i = 0; i<orderSize; i++) {
			Order order = orders.get(i);
			String orderInfo = order.getOrderInfo();
			String customer = order.getCustomer().getLastName();
			System.out.println(orderInfo);
			System.out.println(customer);
		}
	}
	
	public void executeOrder(String id) {		
		DataHolder dataHolder = DataHolder.getInstance();
		int orderSize = dataHolder.getOrders().size();
		for(int i = 0; i<orderSize; i++) {
			Order order = dataHolder.getOrders().get(i);
			String currentId;
			currentId = order.getId();
			if(currentId == id) {
				order.execute();
			}
		} 	
	}
	
}
