package com.acme;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ACMEPassPaginationTests extends ACMEPassTestBase{

    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
	
	@Before
    public void setUp() throws Exception {
        url = "http://localhost:8080/#/";
        driver = getDriver("firefox");
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        
        loginWith("paul.robert@acme.com", "shadow");
    }
    
    @Test
    public void paginateForwardsWhen0Entries() throws Exception{
    	ensureCorrectNumberOfEntries(0);
    	
    	WebElement nextPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page + 1, $event)']"));
    	nextPageButton.click();
    	
    	Thread.sleep(500);

    	String info = getInfoString();
    	assertEquals("Showing 0 - 0 of 0 items.", info);
    }
    
    @Test
    public void paginateForwardsWhen20Entries() throws Exception{
    	ensureCorrectNumberOfEntries(20);
    	
    	WebElement nextPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page + 1, $event)']"));
    	nextPageButton.click();
    	
    	Thread.sleep(500);
    	
    	String info = getInfoString();
    	assertEquals("Showing 1 - 20 of 20 items.", info);
    }
    
    @Test
    public void paginateForwardsWhen21Entries() throws Exception{
        
        ensureCorrectNumberOfEntries(21);
    	
    	WebElement nextPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page + 1, $event)']"));
    	nextPageButton.click();
    	
    	Thread.sleep(500);
    	
    	String info = getInfoString();
    	assertEquals("Showing 21 - 21 of 21 items.", info);
    }
    
    @Test
    public void paginateForwardsWhen41Entries() throws Exception{
    	ensureCorrectNumberOfEntries(41);
    	
    	WebElement nextPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page + 1, $event)']"));
    	nextPageButton.click();
    	
    	Thread.sleep(500);
    	
    	nextPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page + 1, $event)']"));
    	nextPageButton.click();
    	
    	Thread.sleep(500);
    	
    	String info = getInfoString();
    	assertEquals("Showing 41 - 41 of 41 items.", info);
    }
    
    @Test
    public void paginateBackwardsWhen0Entries() throws Exception{
    	ensureCorrectNumberOfEntries(0);
    	
    	WebElement previousPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page - 1, $event)']"));
    	previousPageButton.click();
    	
    	Thread.sleep(500);
    	
    	String info = getInfoString();
    	assertEquals("Showing 0 - 0 of 0 items.", info);
    }
    
    @Test
    public void paginateBackwardsWhen20Entries() throws Exception{
    	ensureCorrectNumberOfEntries(20);
    	
    	WebElement previousPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page - 1, $event)']"));
    	previousPageButton.click();
    	
    	Thread.sleep(500);
    	
    	String info = getInfoString();
    	assertEquals("Showing 1 - 20 of 20 items.", info);
    }
    
    @Test
    public void paginateBackwardsWhen21Entries() throws Exception{
    	ensureCorrectNumberOfEntries(21);
    	
    	WebElement nextPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page + 1, $event)']"));
    	nextPageButton.click();
    	
    	Thread.sleep(1000);
    	
    	String info = getInfoString();
    	assertEquals("Showing 21 - 21 of 21 items.", info);
    	
    	Thread.sleep(1000);
    	
    	WebElement previousPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page - 1, $event)']"));
    	previousPageButton.click();
    	
    	Thread.sleep(1000);
    	
    	info = getInfoString();
    	assertEquals("Showing 1 - 20 of 21 items.", info);
    }
    
    @Test
    public void paginateBackwardsWhen41Entries() throws Exception{
    	ensureCorrectNumberOfEntries(41);
    	
    	WebElement nextPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page + 1, $event)']"));
    	nextPageButton.click();
    	
    	Thread.sleep(1000);
    	
    	nextPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page + 1, $event)']"));
    	nextPageButton.click();
    	
    	String info = getInfoString();
    	assertEquals("Showing 41 - 41 of 41 items.", info);
    	
    	Thread.sleep(1000);
    	
    	WebElement previousPageButton = driver.findElement(By.cssSelector("a[ng-click='selectPage(page - 1, $event)']"));
    	previousPageButton.click();
    	
    	Thread.sleep(1000);
    	
    	info = getInfoString();
    	assertEquals("Showing 21 - 40 of 41 items.", info);
    }
    
    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
    
    private void login(String username, String password) throws Exception{
		driver.get(url);
        if (!username.isEmpty() && !password.isEmpty()) {
            // Sign in button
            driver.findElement(By.id("login")).click();
            Thread.sleep(500);

            // Enter credentials
            driver.findElement(By.id("username")).sendKeys(username);
            driver.findElement(By.id("password")).sendKeys(password);
            List<WebElement> buttons = driver.findElements(By.tagName("button"));

            WebElement submit = null;
            for (WebElement button : buttons) {
                System.out.println(button.getText());
                if ("Sign in".equals(button.getText())) {
                    submit = button;
                }
            }

            submit.click();

            Thread.sleep(500);
        }
	}
	
	private int countEntries() throws Exception {
		List<WebElement> rows = driver.findElements(By.cssSelector("tr[ng-repeat='acmePass in vm.acmePasses track by acmePass.id']"));
		return rows.size();
	}
	
	private String getInfoString() throws Exception {
		WebElement info = driver.findElement(By.className("info"));  
        return info.getText();
	}
	
	private int getTotalNumberOfEntries() throws Exception {
        String str = getInfoString();
		String[] split = str.split("\\s+");
		
		return Integer.parseInt(split[5]);
	}
	
	private void ensureCorrectNumberOfEntries(int desiredNumber) throws Exception{
		int currentNumber = getTotalNumberOfEntries();
		int changeNeeded = desiredNumber - currentNumber;
		
		if (currentNumber < desiredNumber){
			addEntries(changeNeeded);
		}
		
		if (currentNumber > desiredNumber) {
			removeEntries(changeNeeded);
		}
		
		Thread.sleep(500);
		
	}
	
	private void addEntries(int numberOfEntries) throws Exception{
		for(int i=0; i<numberOfEntries; i++){
				 
			Thread.sleep(500);
	        WebElement button = driver.findElement(By.cssSelector("button.btn.btn-primary"));
	        button.click();
	
	        Thread.sleep(500);
	
	        driver.findElement(By.id("field_site")).clear();
	        driver.findElement(By.id("field_site")).sendKeys("some_site.com");
	        driver.findElement(By.id("field_login")).clear();
	        driver.findElement(By.id("field_login")).sendKeys("test_login");
	        driver.findElement(By.id("field_password")).clear();
	        driver.findElement(By.id("field_password")).sendKeys("test_password");
	        driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).click();	
			
		}
	}
	
	private void removeEntries(int numberOfEntries) throws Exception{
		List<WebElement> rows = driver.findElements(By.cssSelector("button[ui-sref='acme-pass.delete({id:acmePass.id})']"));
		numberOfEntries = rows.size() - 1;
		
		for(int i=0; i<numberOfEntries; i++){
			int indexNumber = rows.size() - 1;
			
			rows.get(indexNumber).click();
			
			Thread.sleep(500);
			
			WebElement submitButton = driver.findElement(By.cssSelector("button[ng-disabled='deleteForm.$invalid']"));
			submitButton.click();
			
			Thread.sleep(500);
			
			rows = driver.findElements(By.cssSelector("button[ui-sref='acme-pass.delete({id:acmePass.id})']"));
		}
		
		
	}
}