package sk.fiit.app;

import sk.fiit.app.login.PortalLogin;
import sk.fiit.app.manager.ShopManager;

public class EmployeePortal {
	
	private ShopManager shopManager;
	
	public void enterPortal() {
		PortalLogin login = new PortalLogin();
		ShopManager shopManager = new ShopManager();
    	shopManager = login.login("a.mlyncar@gmail.com", "Password1");
    	this.shopManager = shopManager;
    	manageOrders();
    	accessOrders();
    	//invariantMethod();
	}
	
    public void manageOrders() {
    	shopManager.executeOrder("2");
    	shopManager.displayAllOrders();
    }
    
    public void accessOrders() {
    	shopManager.displayAllOrders();
    }
    
    public  void invariantMethod() {
    	
    }
}
