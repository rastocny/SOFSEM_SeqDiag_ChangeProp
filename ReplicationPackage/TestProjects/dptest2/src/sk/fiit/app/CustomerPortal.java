package sk.fiit.app;

import sk.fiit.app.data.DataHolder;
import sk.fiit.app.entity.Customer;

public class CustomerPortal {
    
    public void shop() {
    	DataHolder dataHolder = DataHolder.getInstance();  	
    	Customer customer = dataHolder.searchCustomerByName("Mlyncar");
    	customer.shop("MacBook");
    }
}
