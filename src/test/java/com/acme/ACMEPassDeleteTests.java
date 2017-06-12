package com.acme;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.acme.Util.confirmModal;
import static com.acme.Util.generateRandomString;
import static com.acme.Util.isModalSubmitButtonEnabled;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ACMEPassDeleteTests extends ACMEPassTestBase {
    private PasswordHelper _passwordHelper;
    private WebDriver _driver;
    private Random _random;

    @Before
    public void setUp() {
        _driver = getDriver("firefox");
        _driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        _passwordHelper = new PasswordHelper(new LoginHelper(_driver, "http://localhost:8080/#/"), _driver, "http://localhost:8080/#/");
        _random = new Random();
    }

    @After
    public void tearDown() {
        _driver.close();
    }

    @Test
    public void CanDeleteAPassword() throws Exception {
        _passwordHelper.givenOnAcmePassPage();
        _passwordHelper.givenAPasswordExists();

        List<PasswordHelper.Password> passwords = _passwordHelper.getPasswordsOnPage();
        PasswordHelper.Password randomPassword = passwords.get(_random.nextInt(passwords.size()));
        _passwordHelper.deletePassword(randomPassword);

        assertFalse(_passwordHelper.getPasswordsOnPage().stream().anyMatch((other) -> Objects.equals(other.id, randomPassword.id)));
    }

    @Test
    public void CanCreateAPassword() throws Exception {
        _passwordHelper.givenOnAcmePassPage();

        String site = generateRandomString(_random, 32);
        String login = generateRandomString(_random, 32);
        String password = generateRandomString(_random, 32);
        _passwordHelper.createPassword(site, login, password);

        while (true) {
            boolean foundNewPassword = _passwordHelper.getPasswordsOnPage().stream().anyMatch((storedPassword) ->
                    Objects.equals(storedPassword.site, site) &&
                            Objects.equals(storedPassword.login, login) &&
                            Objects.equals(storedPassword.password, password)
            );
            if (foundNewPassword) {
                break;
            }
            _passwordHelper.goToNextPage();
        }
    }

    @Test
    public void CanNotCreateAPasswordWithoutASite() throws Exception {
        _passwordHelper.givenOnAcmePassPage();
        PasswordHelper.PasswordCreationModalHelper modal = _passwordHelper.openPasswordCreationModal();

        modal.findLoginElement().sendKeys(generateRandomString(_random, 32));
        modal.findPasswordElement().sendKeys(generateRandomString(_random, 32));

        assertTrue(!isModalSubmitButtonEnabled(_driver));
        _driver.findElement(By.xpath("//p[@ng-show='editForm.site.$error.required' and @aria-hidden='false']"));
    }

    @Test
    public void CanNotCreateAPasswordWithASiteNameTooShort() throws Exception {
        _passwordHelper.givenOnAcmePassPage();
        PasswordHelper.PasswordCreationModalHelper modal = _passwordHelper.openPasswordCreationModal();

        modal.findSiteElement().sendKeys(generateRandomString(_random, 2));
        modal.findLoginElement().sendKeys(generateRandomString(_random, 32));
        modal.findPasswordElement().sendKeys(generateRandomString(_random, 32));

        assertTrue(!isModalSubmitButtonEnabled(_driver));
        _driver.findElement(By.xpath("//p[@ng-show='editForm.site.$error.minlength' and @aria-hidden='false']"));
    }

    @Test
    public void CanNotCreateAPasswordWithoutALogin() throws Exception {
        _passwordHelper.givenOnAcmePassPage();
        PasswordHelper.PasswordCreationModalHelper modal = _passwordHelper.openPasswordCreationModal();

        modal.findSiteElement().sendKeys(generateRandomString(_random, 32));
        modal.findPasswordElement().sendKeys(generateRandomString(_random, 32));

        assertTrue(!isModalSubmitButtonEnabled(_driver));
        _driver.findElement(By.xpath("//p[@ng-show='editForm.login.$error.required' and @aria-hidden='false']"));
    }

    @Test
    public void CanNotCreateAPasswordWithoutAPassword() throws Exception {
        _passwordHelper.givenOnAcmePassPage();
        PasswordHelper.PasswordCreationModalHelper modal = _passwordHelper.openPasswordCreationModal();

        modal.findSiteElement().sendKeys(generateRandomString(_random, 32));
        modal.findLoginElement().sendKeys(generateRandomString(_random, 32));

        assertTrue(!isModalSubmitButtonEnabled(_driver));
        _driver.findElement(By.xpath("//p[@ng-show='editForm.password.$error.required' and @aria-hidden='false']"));
    }

    @Test
    public void CanGenerateAPassword() throws Exception {

    }
}
