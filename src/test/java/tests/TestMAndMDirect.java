package tests;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import pages.MAndMDirectHome;
import pages.MAndMDirectLogin;
import pages.MAndMDirectWelcome;

public class TestMAndMDirect extends TestBase {
	
	private MAndMDirectHome home = null;
	private MAndMDirectLogin login = null;
	private MAndMDirectWelcome welcome = null;
	private String loginError = "You've entered an incorrect email address or password";
	
	
	@Test(dataProvider = "getDataFromDataSource")
	public void LoginToMAndMDirect(int itr, Map<String, String> data) throws Exception {
		System.out.println("Running itr " + itr);
		home = new MAndMDirectHome(getDriver());
		home.navigateToLoginPage();
		login = new MAndMDirectLogin(getDriver());
		System.out.println("Username = " + data.get("Username") + ", Password = " + data.get("Password"));
		login.performLogin(data.get("Username"), data.get("Password"));
		if(itr == 1) {
			welcome = new MAndMDirectWelcome(getDriver());
			System.out.println("Login was successful");
			Thread.sleep(2000);
			welcome.performLogout();
		}
		if(itr > 1) {
			Assert.assertEquals(home.verifyLoginError(loginError), true, "Account does not exist - error was not visible");
		}
	}

}
