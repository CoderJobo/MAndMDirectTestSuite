package tests;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

import core.DriverFactory;
import core.ExcelDataProvider;
import core.ITestData;
import core.JSONDataProvider;
import core.TestConfig;

public class TestBase {
	
	private WebDriver driver = null;
	private ITestData testData = null;

	@BeforeSuite
	public void initialiseSuite() throws Exception {
		String env = System.getenv("env");
		TestConfig.load(env);
		TestConfig.addProperty("browser", System.getenv("browser"));
		TestConfig.addProperty("env",env );
	}
	
	@BeforeClass
	public void initialiseDriver() {
		System.out.println("Running @BeforeClass...");
		driver = new DriverFactory().getWebDriver(TestConfig.getProperty("browser"));
	}
	
	@DataProvider(name="getDataFromDataSource")
	public Object[][] getDataFromDataSource(Method testCase) throws Exception{
		System.out.println("Running @DataProvider...");
		File testDataFileLocation = new File("src/test/resources/testdata/");
		List<HashMap<String, String>> extractedData = null;
		String dataSource = TestConfig.getProperty("dataSource");
		System.out.println("Data source = " + dataSource);
		String envName = TestConfig.getProperty("env").toUpperCase();
		System.out.println("Environment = " + envName);
		
		if(dataSource.equalsIgnoreCase("excel")) {
			System.out.println("Data source is " + dataSource + ", envName is " + envName);
			this.testData = new ExcelDataProvider(testDataFileLocation.getAbsolutePath() + "\\TestData.xlsx", envName);
		}
		else if(dataSource.equalsIgnoreCase("json")) {
			this.testData = new JSONDataProvider(testDataFileLocation + "/data." + envName + ".json");
		}
		else {
			throw new Exception("Invalid data source specified: " + dataSource);
		}
		System.out.println("Test case name = " + testCase.getName());
		extractedData = this.testData.retrieveDataFromDataSource(testCase.getName());
		System.out.println("extractedData: " + extractedData);
		System.out.println("Returning from @DataProvider\n");
		return this.createDataProvider(extractedData);
	}
	
	private Object[][] createDataProvider(Object dataSet){
		int numRows = ((ArrayList)dataSet).size();
		Object[][] dataArray = new Object[numRows][2];
		int dimension = 0;
		for(int row = 0; row < numRows; row++) {
			dataArray[dimension][0] = row + 1;
			System.out.print(dataArray[dimension][0] + " ");
			dataArray[dimension][1] = ((ArrayList)dataSet).get(row);
			System.out.println(dataArray[dimension][1]);
			dimension++;
		}
		System.out.println("Printing dataArray...");
		for(int i = 0; i < dataArray.length; i++) {
			for(int j = 0; j < dataArray[0].length; j++) {
				System.out.println(dataArray[i][j]);
			}
		}
			
		return dataArray;
	}
	
	public WebDriver getDriver() {
		return driver;
	}
	
	@BeforeMethod
	public void launchApp() {
		driver.get(TestConfig.getProperty("appBaseURL"));
	}
	
	@AfterClass
	public void cleanUp() {
		System.out.println("Running @AfterClass...");
		if(driver != null) {
			driver.quit();
		}
	}
}
