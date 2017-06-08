package com.acme;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.concurrent.TimeUnit;
import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestSelenium {
    private WebDriver driver;
    private String url;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        //ChromeDriverService service = new ChromeDriverService.Builder()
	//        .usingDriverExecutable(new File("chromedriver"))
        //    .usingAnyFreePort()
        //    .build();

        //service.start();

        // driver = new HtmlUnitDriver(true);
        driver = new RemoteWebDriver(new URL("http://localhost:22000"), DesiredCapabilities.chrome());
        url = "http://localhost:8080/#/";
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	    driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        }

    @Test
    public void UserCanSignIn() throws Exception {
        driver.get(url);

        driver.findElement(By.linkText("Sign in")).click();
	    driver.findElement(By.id("username")).sendKeys("tom.hayes@acme.com");
    	driver.findElement(By.id("password")).sendKeys("superman");
	    assertTrue(driver.findElement(By.id("account-menu")).isDisplayed());
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }
}
