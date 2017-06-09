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
import static org.junit.Assert.fail;

public class MoreTests {
    private WebDriver driver;
    private String url;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        //System.setProperty("webdriver.gecko.driver", "D:\\Libraries\\Google Drive\\School\\Summer17\\SENG 426\\geckodriver-v0.16.1-win64");

        driver = new FirefoxDriver();
        url = "http://localhost:8080/#/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void headerIsCorrect() throws Exception {
        driver.get(url);

        System.out.println(driver.getPageSource());
        WebElement element = driver.findElement(By.tagName("h1"));
        assertEquals("ACME SECURITY SYSTEM", element.getText());
    }

    @Test
    public void canClickTechnology() throws Exception {
        driver.get(url);

        //Thread.sleep(1000);
        //System.out.println(driver.getPageSource());

        WebElement element = driver.findElement(By.xpath("//a[@ui-sref='technology']"));
        System.out.println(element.getText());
        element.click();

        Thread.sleep(500);

        List<WebElement> headers = driver.findElements(By.tagName("h1"));

        boolean passed = false;

        for (WebElement header : headers) {
            if ("Technology".equals(header.getText())) {
                passed = true;
            }
        }

        assertEquals(true, passed);
    }

    // Specific ACMEPass related tests.

    @Test
    public void login() throws InterruptedException {
        driver.get(url);

        // Sign in button
        driver.findElement(By.id("login")).click();
        Thread.sleep(500);

        // Enter credentials
        driver.findElement(By.id("username")).sendKeys("jo.thomas@acme.com");
        driver.findElement(By.id("password")).sendKeys("mustang");
        List<WebElement> buttons = driver.findElements(By.tagName("button"));

        WebElement submit = null;
        for(WebElement button : buttons) {
            System.out.println(button.getText());
            if ("Sign in".equals(button.getText())) {
                submit = button;
            }
        }

        submit.click();

        Thread.sleep(500);

        // Check that the AcmePass link exists on the banner.
        WebElement acmepass = driver.findElement(By.xpath("//a[@ui-sref='acme-pass']"));
        Assert.assertNotNull(acmepass);
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
