package com.acme;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ACMEPassViewPasswordTests extends ACMEPassTestBase {

    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    private String browser;
    private String username;
    private String password;

    @Parameterized.Parameters
    public static Collection<Object[]> users() {
        return Arrays.asList( new Object[][] {
                { "firefox", "frank.paul@acme.com", "starwars"},    // Manager
                { "firefox", "jo.thomas@acme.com",  "mustang" },    // Employee
                { "firefox", "admin@acme.com",      "K-10ficile" }, // Admin
                { "chrome",  "frank.paul@acme.com", "starwars"},    // Manager
                { "chrome",  "jo.thomas@acme.com",  "mustang" },    // Employee
                { "chrome",  "admin@acme.com",      "K-10ficile" }, // Admin
        });
    }

    public ACMEPassViewPasswordTests(String browser, String username, String password) {
        this.browser = browser;
        this.username = username;
        this.password = password;
    }

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        url = "http://localhost:8080/#/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver = getDriver(browser);

        loginWith(username, password);
    }

    @Test
    public void testMakePasswordVisible() throws InterruptedException {

        // Assume no acmepass entries yet
        // Create an entry
        Thread.sleep(1000);
        WebElement button = driver.findElement(By.cssSelector("button.btn.btn-primary"));
        Thread.sleep(1000);

        button.click();

        Thread.sleep(1000);

        driver.findElement(By.id("field_site")).clear();
        driver.findElement(By.id("field_site")).sendKeys("some_site.com");
        driver.findElement(By.id("field_login")).clear();
        driver.findElement(By.id("field_login")).sendKeys("test_login");
        driver.findElement(By.id("field_password")).clear();
        driver.findElement(By.id("field_password")).sendKeys("test_password");
        driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).click();

        Thread.sleep(500);
        // Password starts out hidden
        WebElement password = driver.findElement(By.xpath("//input[@type='password']"));
        Assert.assertNotNull(password);
        // Click button to toggle visibility
        driver.findElement(By.cssSelector("span.glyphicon.glyphicon-eye-open")).click();

        // Password field should have type value of "text" now
        password = driver.findElement(By.xpath("//input[@type='text']"));
        Assert.assertNotNull(password);

        // Toggle back
        driver.findElement(By.cssSelector("span.glyphicon.glyphicon-eye-open")).click();
        password = driver.findElement(By.xpath("//input[@type='password']"));
        Assert.assertNotNull(password);

        // Cleanup: remove the acmepass entry we created.
        // This doesn't currently work, since the "delete" functionality is broken.
        driver.findElement(By.xpath("//button[2]")).click();
        driver.findElement(By.cssSelector("button.btn.btn-danger")).click();
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
