package com.acme;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/**
 * Created by Rhiannon on 2017-06-12.
 */
public class ACMEPassLoginTest extends ACMEPassTestBase {

    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        url = "http://localhost:8080/#/";
        driver = getDriver("firefox");
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void LoggedInUserCanAccess() throws InterruptedException {
        loginWith("frank.paul@acme.com", "starwars");

        // Check that we have reached the acmepass page
        Assert.assertEquals("http://localhost:8080/#/acme-pass", driver.getCurrentUrl());
    }

    @Test(expected= org.openqa.selenium.NoSuchElementException.class)
    public void NotLoggedInNoLink() {
        driver.get(url);
        // Don't log in: verify that we can't reach the acmepass page using the link.
        WebElement acmepass = driver.findElement(By.xpath("//a[@ui-sref='acme-pass']"));
        Assert.fail();
    }

    @Test
    public void NotLoggedInNoAccess() {
        // Try directly accessing the acmepass url
        driver.get(url + "acme-pass");
        WebElement header = driver.findElement(By.cssSelector("div.col-md-12 > h1"));
        Assert.assertEquals("Error Page!", header.getText());
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
}
