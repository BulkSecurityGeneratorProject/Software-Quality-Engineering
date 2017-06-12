package com.acme;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;

/**
 * Created by Rhiannon on 2017-06-10.
 */
public abstract class ACMEPassTestBase {

    protected WebDriver driver;
    protected String url;

    public WebDriver getDriver(String browser) {
        if (browser.equals("firefox")) {
            WebDriver driver = new FirefoxDriver();
            driver.manage().window().maximize();
            return driver;
        } else {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            return new ChromeDriver(options);
        }
    }

    public void loginWith(String username, String password) throws InterruptedException {
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
                if ("Sign in".equals(button.getText())) {
                    submit = button;
                }
            }

            submit.click();

            Thread.sleep(500);
        }

        // Check that the AcmePass link exists on the banner.
        WebElement acmepass = driver.findElement(By.xpath("//a[@ui-sref='acme-pass']"));
        Assert.assertNotNull(acmepass);

        acmepass.click();
        Thread.sleep(500);
    }

}
