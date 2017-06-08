package com.acme;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.concurrent.TimeUnit;
import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
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

	

        driver = new RemoteWebDriver(new URL("http://localhost:22000"), DesiredCapabilities.chrome());
        url = "http://www.google.com";
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void headerIsCorrect() throws Exception {
	System.out.println("Running!");
        driver.get(url);

	Thread.sleep(10000);

        assertEquals("ACME Security System ", driver.findElement(By.tagName("h1")).getText());
        //driver.findElement(By.cssSelector("a[ui-sref=\"technology\"")).click();
        //WebElement header = driver.findElement(By.cssSelector("div.container > h1"));

        //assertEquals("Technology", header.getText());
        // ERROR: Caught exception [unknown command []]
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
