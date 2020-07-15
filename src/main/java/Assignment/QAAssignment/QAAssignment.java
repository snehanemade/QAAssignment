package Assignment.QAAssignment;

import static org.testng.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class QAAssignment {
	
	WebDriver driver;
	Car car = new Car();
	List<String> brandInputList = new ArrayList<String>(Arrays.asList("Seat","Renault","Peugeot","Dacia","Citroën","Škoda"));
	List<String> modelInputList = new ArrayList<String>(Arrays.asList("Toledo","Laguna Grandtour","308 SW","Solenza","C4 Coupé","Favorit"));
	
	@BeforeMethod
	public void setUp() {
		System.setProperty("webdriver.chrome.driver", "E:\\Selenium\\New\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("http://localhost:3000");
		
	}
	
	@Test
	public void verifyPageLayout() {
		String title = driver.getTitle();
		assertEquals(title, "React App");
		Select brand = new Select(driver.findElement(By.id("S1")));
		Assert.assertEquals("- Select Option -", brand.getFirstSelectedOption().getText());
		Assert.assertEquals(driver.findElement(By.id("S2")).getAttribute("disabled"),"true");		
		Assert.assertEquals(driver.findElement(By.id("B")).getAttribute("disabled"),"true");
	}
	
	@Test
	public void verifySearchByBrandAndModelTest() throws InterruptedException {
		
		for (int i=0;i<brandInputList.size();i++) {
			Select brand = new Select(driver.findElement(By.id("S1")));
			brand.selectByVisibleText(brandInputList.get(i));
			String branchSelected = brand.getFirstSelectedOption().getText();
			
			Select model = new Select(driver.findElement(By.id("S2")));
			model.selectByVisibleText(modelInputList.get(i));
			String modelSelected = model.getFirstSelectedOption().getText();
			
			driver.findElement(By.id("B")).click();
			
			Alert alert = driver.switchTo().alert();
			String alertMessage = alert.getText();
			String expectedMessage = "{\"model\":\""+modelSelected+"\",\"brand\":\""+branchSelected+"\",\"keyword\":\"\"}";
			assertEquals(alertMessage, expectedMessage);
			alert.accept();
		}
	}
	
	@Test
	public void verifySearchByBrandModelKeywordTest() throws InterruptedException {
		List<String> keywordInput = new ArrayList<String>(Arrays.asList("campo","fast car","Racing","fastest","fast car","fast car"));					
		for (int i=0;i<brandInputList.size();i++) {
			Select brand = new Select(driver.findElement(By.id("S1")));
			brand.selectByVisibleText(brandInputList.get(i));
			String branchSelected = brand.getFirstSelectedOption().getText();
			
			Select model = new Select(driver.findElement(By.id("S2")));
			model.selectByVisibleText(modelInputList.get(i));
			String modelSelected = model.getFirstSelectedOption().getText();
			
			WebElement keyword = driver.findElement(By.id("T"));
			keyword.clear();
			keyword.sendKeys(keywordInput.get(i));
			
			driver.findElement(By.id("B")).click();
			
			Alert alert = driver.switchTo().alert();
			String alertMessage = alert.getText();
			String expectedMessage = "{\"model\":\""+modelSelected+"\",\"brand\":\""+branchSelected+"\",\"keyword\":\""+keywordInput.get(i)+"\"}";
			assertEquals(alertMessage, expectedMessage);
			alert.accept();
		}
	}
	
	@Test void verifySearchByKeyword () {
		List<String> keywordInput = new ArrayList<String>(Arrays.asList("campo","fast car","Racing","fastest","fast car","fast car"));					
		WebElement keyword = driver.findElement(By.id("T"));
		
		for(int i=0;i<keywordInput.size();i++) {
			keyword.clear();
			keyword.sendKeys(keywordInput.get(i));
			driver.findElement(By.id("B")).click();
			Alert alert = driver.switchTo().alert();
			String alertMessage = alert.getText();
			String expectedMessage = "{\"model\":\"\",\"brand\":\"\",\"keyword\":\""+keywordInput.get(i)+"\"}";
			assertEquals(alertMessage, expectedMessage);
			alert.accept();
		}
	}
	
	@Test
	public void verifyModelOptionsListTest() {
		
		Response response = RestAssured.get("http://localhost:3000/cars");
		List<String> brandList = response.jsonPath().getList("brand");
		List<Object> models = response.jsonPath().getList("models");

		brandList.add("- Select Option -");
		
		Select brand = new Select(driver.findElement(By.id("S1")));
		List<WebElement> brandOptions = brand.getOptions();
		List<String> actualBrandOptions = new ArrayList<String>();
		for(WebElement e : brandOptions) {
			actualBrandOptions.add(e.getText()); 
		}
		Assert.assertNotNull(brandList);
		Assert.assertNotNull(actualBrandOptions);
		Assert.assertTrue(brandList.containsAll(actualBrandOptions));
		Assert.assertTrue(brandList.size() == actualBrandOptions.size());
	}
	
	@Test
	public void verifyBrandModelOptionsListTest() {
		Response response = RestAssured.get("http://localhost:3000/cars");
		
		Select brand = new Select(driver.findElement(By.id("S1")));
		List<WebElement> brandOptions = brand.getOptions();
			
		Gson gson = new Gson();
		List<Car> cars = gson.fromJson(response.getBody().asString(), new TypeToken<List<Car>>(){}.getType());
		
		for(int i=0;i<brandOptions.size();i++) {
			if(brandOptions.get(i).getText().equals("- Select Option -")) {
				brandOptions.remove(i);
			}
			
			brand.selectByIndex(i);
			String brandSelected = brand.getFirstSelectedOption().getText();
			
			Select model = new Select(driver.findElement(By.id("S2")));
			List<WebElement> brandModels = model.getOptions();
			
			List<String> actualModelsOptions = new ArrayList<String>();
			for(WebElement e : brandModels) {
				if(!(e.getText().equals("- Select Option -"))) {
					actualModelsOptions.add(e.getText());
				}
			}
			
			Assert.assertTrue(brandOptions.size() == cars.size());
			
			for(int j=0;j<actualModelsOptions.size();j++) {
				if(brandSelected.equals(cars.get(j).getBrand())) {
					Assert.assertTrue(actualModelsOptions.containsAll(cars.get(j).getModels()));
					Assert.assertTrue(actualModelsOptions.size() == cars.get(j).getModels().size());
					break;
				}
			}
		}	
	}
	
	@AfterMethod
	public void tearDown() {
		driver.quit();
	}
}

class Car {
		String brand;
		ArrayList<String> models;
		public String getBrand() {
			return brand;
		}
		public void setBrand(String brand) {
			this.brand = brand;
		}
		public ArrayList<String> getModels() {
			return models;
		}
		public void setModel(ArrayList<String> models) {
			this.models = models;
		}
		
}