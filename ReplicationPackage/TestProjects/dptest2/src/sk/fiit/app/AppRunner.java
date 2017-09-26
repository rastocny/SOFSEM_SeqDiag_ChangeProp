package sk.fiit.app;

import sk.fiit.app.test.ShopTestCreator;

public class AppRunner {
	
    public static void main(String[] args) {    
        
    	ShopTestCreator creator = new ShopTestCreator();   	
    	creator.addDummyData();
    	
    	EmployeePortal portal = new EmployeePortal();
    	portal.enterPortal();

    	CustomerPortal customerPortal = new CustomerPortal();   	
    	customerPortal.shop();
    }
   
}
