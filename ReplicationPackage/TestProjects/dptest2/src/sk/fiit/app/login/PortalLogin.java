package sk.fiit.app.login;

import sk.fiit.app.manager.ShopManager;

public class PortalLogin {

	public ShopManager login(String password, String email) {
		boolean loginResult;
		LoginInfo info = new LoginInfo();
		loginResult = validateLogin(email, info);
		boolean passResult = false;
		if(loginResult == true) {
			passResult = validatePassword(password, info);
		}
		if(passResult == true) {
			return new ShopManager();
		}
		return null;
	}
	
	private boolean validateLogin(String email, LoginInfo info) {
		String login;
		login = info.getLogin();
		boolean loginOk = false;
		if(login == email) {
			loginOk = true;
		}
		return loginOk;
	}
	
	private boolean validatePassword(String password, LoginInfo info) {
		String pass;
		pass = info.getPassword();
		boolean passOk = false;
		if(password == pass) {
			passOk = true;
		}
		return passOk;
	}
	
}
