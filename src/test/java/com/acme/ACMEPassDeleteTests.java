package com.acme;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.acme.Util.dismissModal;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ACMEPassDeleteTests extends ACMEPassTestBase {
    private PasswordHelper _passwordHelper;
    private WebDriver _driver;
    private Random _random;

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
    public void CanDeleteAPassword() throws Exception {
        _passwordHelper.givenAPasswordExists();

        _passwordHelper.givenOnFirstAcmePassPage();
        List<PasswordHelper.Password> passwords = _passwordHelper.getPasswordsOnPage();
        PasswordHelper.Password randomPassword = passwords.get(_random.nextInt(passwords.size()));
        _passwordHelper.deletePassword(randomPassword);

        assertFalse(_passwordHelper.passwordEntryExists(randomPassword.site, randomPassword.login, randomPassword.password));
    }

    @Test
    public void CanCancelDeletingAPassword() throws Exception {
        _passwordHelper.givenAPasswordExists();
        _passwordHelper.givenOnFirstAcmePassPage();

        List<PasswordHelper.Password> passwords = _passwordHelper.getPasswordsOnPage();
        PasswordHelper.Password randomPassword = passwords.get(_random.nextInt(passwords.size()));
        _passwordHelper.openDeletePasswordModal(randomPassword);
        dismissModal(_driver);

        assertTrue(_passwordHelper.passwordEntryExists(randomPassword.site, randomPassword.login, randomPassword.password));
    }
}
