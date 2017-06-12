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
public class ACMEPassGeneratePasswordTests extends ACMEPassTestBase {

    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    private String browser;
    private String username;
    private String password;

    @Test
    public void createPasswordGoldenPath() throws InterruptedException {

        Thread.sleep(1000);
        WebElement button = driver.findElement(By.cssSelector("button.btn.btn-primary"));
        Thread.sleep(1000);

        button.click();

        Thread.sleep(1000);

        createPassword("site.com", "teslogin", "dduude");

        //The 'save' button
        driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).click();

        WebElement password = driver.findElement(By.xpath("//input[@type='password']"));
        Assert.assertNotNull(password);
        // This doesn't currently work, since the "delete" functionality is broken.
        driver.findElement(By.xpath("//button[2]")).click();
        driver.findElement(By.cssSelector("button.btn.btn-danger")).click();
    }


    @Parameterized.Parameters
    public static Collection<Object[]> users() {
        return Arrays.asList( new Object[][] {
                { "firefox", "frank.paul@acme.com", "starwars"},    // Manager
//                { "firefox", "jo.thomas@acme.com",  "mustang" },    // Employee
//                { "firefox", "admin@acme.com",      "K-10ficile" }, // Admin
//                { "firefox",  "frank.paul@acme.com", "starwars"},    // Manager
//                { "firefox",  "jo.thomas@acme.com",  "mustang" },    // Employee
//                { "firefox",  "admin@acme.com",      "K-10ficile" }, // Admin
        });
    }

    public ACMEPassGeneratePasswordTests(String browser, String username, String password) {
        this.browser = browser;
        this.username = username;
        this.password = password;
    }

    @Before
    public void setUp() throws Exception {
//        driver = new FirefoxDriver();
        url = "http://localhost:8080/#/";
        driver = getDriver(browser);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        loginWith(username, password);
    }
    @Test
    public void createPasswordWithlessThanThreeCharacterSiteNameFails() throws InterruptedException{
        Thread.sleep(1000);
        WebElement button = driver.findElement(By.cssSelector("button.btn.btn-primary"));
        Thread.sleep(1000);

        button.click();

        Thread.sleep(1000);

        createPassword("tw", "teslogin", "dduude");
        Assert.assertFalse(driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).isEnabled());
    }

    @Test
    public void createPasswordWithEmptyFieldFails() throws InterruptedException{
        Thread.sleep(1000);
        WebElement button = driver.findElement(By.cssSelector("button.btn.btn-primary"));
        Thread.sleep(1000);

        button.click();

        Thread.sleep(1000);

        createPassword("tw", null, "dduude");
        Assert.assertFalse(driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).isEnabled());

        createPassword(null, "someValue", "dduude");
        Assert.assertFalse(driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).isEnabled());

        createPassword("someSite", "loginVal", null);
        Assert.assertFalse(driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).isEnabled());

    }

    @Test
    public void generatePasswordGoldenPath() throws InterruptedException{
        Thread.sleep(1000);
        WebElement button = driver.findElement(By.cssSelector("button.btn.btn-primary"));
        Thread.sleep(1000);

        button.click();

        Thread.sleep(1000);

        createPassword("tw", "testlogin", null);
        Assert.assertFalse(driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).isEnabled());

        driver.findElement(By.cssSelector("div.modal-body > div.form-group.clearfix > div.col-lg-2 > button.btn.btn-primary")).click();

        //TODO: Finish the default parameter passed, check.

    }

    public void createPassword(String site, String login, String password){

        driver.findElement(By.id("field_site")).clear();
        driver.findElement(By.id("field_site")).sendKeys(site);
        driver.findElement(By.id("field_login")).clear();
        driver.findElement(By.id("field_login")).sendKeys(login);
        driver.findElement(By.id("field_password")).clear();
        if(password != null)
            driver.findElement(By.id("field_password")).sendKeys(password);
    }

    @After
    public void tearDown() throws Exception {
        // Cleanup: remove the acmepass entry we created.

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
