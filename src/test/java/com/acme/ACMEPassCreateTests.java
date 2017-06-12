package com.acme;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.acme.Util.generateRandomString;
import static com.acme.Util.isModalSubmitButtonEnabled;
import static org.junit.Assert.assertTrue;

public class ACMEPassCreateTests extends ACMEPassTestBase {
    private PasswordHelper _passwordHelper;
    private Random _random;
    private WebDriver _driver;

    @Before
    public void setUp() {
        String rootUrl = "http://localhost:8080/#/";

        _driver = getDriver("firefox");
        _driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        _passwordHelper = new PasswordHelper(new LoginHelper(_driver, rootUrl), _driver, rootUrl);
        _random = new Random();
    }

    @After
    public void tearDown() {
        _driver.close();
    }

    @Test
    public void CanCreateAPassword() throws Exception {
        String site = generateRandomString(_random, 32);
        String login = generateRandomString(_random, 32);
        String password = generateRandomString(_random, 32);
        _passwordHelper.createPassword(site, login, password);

        assertTrue(_passwordHelper.passwordEntryExists(site, login, password));
    }

    @Test
    public void CanNotCreateAPasswordWithoutASite() throws Exception {
        PasswordHelper.PasswordCreationModalHelper modal = _passwordHelper.openPasswordCreationModal();

        modal.findLoginElement().sendKeys(generateRandomString(_random, 32));
        modal.findPasswordElement().sendKeys(generateRandomString(_random, 32));

        assertTrue(!isModalSubmitButtonEnabled(_driver));
        _driver.findElement(By.xpath("//p[@ng-show='editForm.site.$error.required' and @aria-hidden='false']"));
    }

    @Test
    public void CanNotCreateAPasswordWithASiteNameTooShort() throws Exception {
        PasswordHelper.PasswordCreationModalHelper modal = _passwordHelper.openPasswordCreationModal();

        modal.findSiteElement().sendKeys(generateRandomString(_random, 2));
        modal.findLoginElement().sendKeys(generateRandomString(_random, 32));
        modal.findPasswordElement().sendKeys(generateRandomString(_random, 32));

        assertTrue(!isModalSubmitButtonEnabled(_driver));
        _driver.findElement(By.xpath("//p[@ng-show='editForm.site.$error.minlength' and @aria-hidden='false']"));
    }

    @Test
    public void CanNotCreateAPasswordWithoutALogin() throws Exception {
        PasswordHelper.PasswordCreationModalHelper modal = _passwordHelper.openPasswordCreationModal();

        modal.findSiteElement().sendKeys(generateRandomString(_random, 32));
        modal.findPasswordElement().sendKeys(generateRandomString(_random, 32));

        assertTrue(!isModalSubmitButtonEnabled(_driver));
        _driver.findElement(By.xpath("//p[@ng-show='editForm.login.$error.required' and @aria-hidden='false']"));
    }

    @Test
    public void CanNotCreateAPasswordWithoutAPassword() throws Exception {
        PasswordHelper.PasswordCreationModalHelper modal = _passwordHelper.openPasswordCreationModal();

        modal.findSiteElement().sendKeys(generateRandomString(_random, 32));
        modal.findLoginElement().sendKeys(generateRandomString(_random, 32));

        assertTrue(!isModalSubmitButtonEnabled(_driver));
        _driver.findElement(By.xpath("//p[@ng-show='editForm.password.$error.required' and @aria-hidden='false']"));
    }
}
