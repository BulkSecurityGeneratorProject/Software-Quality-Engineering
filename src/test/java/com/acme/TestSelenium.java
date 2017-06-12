package com.acme;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
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
//        System.setProperty("webdriver.gecko.driver", "/home/tanjulia/Desktop/geckodriver"); // 64 Bit Linux

//       driver = new RemoteWebDriver(new URL("http://localhost:22000"), DesiredCapabilities.chrome());
        url = "http://localhost:8080/#/";
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	    driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
    }

    @Test
    public void userCanSignIn() throws Exception {
        driver.get(url);

        // Sign in button
        driver.findElement(By.id("login")).click();

        // Enter credentials
        driver.findElement(By.id("username")).sendKeys("frank.paul@acme.com");
        driver.findElement(By.id("password")).sendKeys("starwars");
        List<WebElement> buttons = driver.findElements(By.tagName("button"));

        WebElement submit = null;
        for(WebElement button : buttons) {
            System.out.println(button.getText());
            if ("Sign in".equals(button.getText())) {
                submit = button;
            }
        }

        submit.click();

        // Check that the AcmePass link exists on the banner.
        WebElement acmepass = driver.findElement(By.xpath("//a[@ui-sref='acme-pass']"));
        Assert.assertNotNull(acmepass);
    }

    @Test
    public void userGoesToACMEpass() throws Exception{
        userCanSignIn();
        driver.findElement(By.xpath("//a[@ui-sref='acme-pass']")).click();
        assertEquals(driver.getTitle(), "ACMEPasses");
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
