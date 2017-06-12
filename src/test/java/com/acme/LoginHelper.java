package com.acme;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.validation.constraints.NotNull;
import java.util.List;

public class LoginHelper {
    private final WebDriver _driver;
    private final String _rootUrl;

    public LoginHelper(@NotNull WebDriver driver, @NotNull String rootUrl) {
        _driver = driver;
        _rootUrl = rootUrl;
    }

    public boolean isLoggedIn() {
        try {
            _driver.findElement(By.id("account-menu")).click();
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    public void givenLoggedOut() {
        if (isLoggedIn()) {
            _driver.findElement(By.id("logout")).click();
        }
    }

    public void loginWith(String username, String password) throws InterruptedException {
        _driver.get(_rootUrl);

        givenLoggedOut();

        if (!username.isEmpty() && !password.isEmpty()) {
            // Sign in button
            _driver.findElement(By.id("login")).click();
            Thread.sleep(500);

            // Enter credentials
            _driver.findElement(By.id("username")).sendKeys(username);
            _driver.findElement(By.id("password")).sendKeys(password);
            List<WebElement> buttons = _driver.findElements(By.tagName("button"));

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
        WebElement acmepass = _driver.findElement(By.xpath("//a[@ui-sref='acme-pass']"));
        Assert.assertNotNull(acmepass);

        acmepass.click();
        Thread.sleep(500);
    }
}
