package com.acme;

import com.sun.corba.se.spi.ior.ObjectKey;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.acme.Util.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ACMEPassEditPassword extends ACMEPassTestBase {

    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    private String username;
    private String password;
    private PasswordHelper _passwordHelper;
    private Random _random;
    @Parameterized.Parameters
    public static Collection<Object[]> users() {
        return Arrays.asList( new Object[][] {
                {"frank.paul@acme.com", "starwars"},    // Manager
//                { "firefox", "jo.thomas@acme.com",  "mustang" },    // Employee
//                { "firefox", "admin@acme.com",      "K-10ficile" }, // Admin
//                { "firefox",  "frank.paul@acme.com", "starwars"},    // Manager
//                { "firefox",  "jo.thomas@acme.com",  "mustang" },    // Employee
//                { "firefox",  "admin@acme.com",      "K-10ficile" }, // Admin
        });
    }

    public ACMEPassEditPassword(String username, String password) {
        this.username = username;
        this.password = password;
        this._random = new Random();
    }

    @Before
    public void setUp() throws Exception {
        url = "http://localhost:8080/#/";
        driver = getDriver("firefox");
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
        _passwordHelper = new  PasswordHelper(new LoginHelper(driver, url),driver, url);

        loginWith(username, password);
    }

    @Test
    public void editPasswordGoldenPath() throws InterruptedException{
        String site = generateRandomString(_random, 32);
        String login = generateRandomString(_random, 32);
        String password = generateRandomString(_random, 32);

        PasswordHelper.Password randomPassword = getARandomPasswordToEdit();

        String id = randomPassword.id;

        editPassword(site,login,password);
        findModalSaveButton().click();
        PasswordHelper.Password editedPassword = getPasswordWithId(id).get();
        assertEquals(editedPassword.site, site);
        assertEquals(editedPassword.login, login);
        assertEquals(editedPassword.password, password);
    }

    private PasswordHelper.Password getARandomPasswordToEdit() throws InterruptedException{
        _passwordHelper.givenAPasswordExists();
        _passwordHelper.givenOnFirstAcmePassPage();
        List<PasswordHelper.Password> passwords = _passwordHelper.getPasswordsOnPage();
        PasswordHelper.Password randomPassword = passwords.get(_random.nextInt(passwords.size()));

        findEditButton(randomPassword.element).click();
        return randomPassword;
    }

    @Test
    public void editPasswordWithlessThanThreeCharacterSiteNameFails() throws InterruptedException{
        getARandomPasswordToEdit();
        editPassword(generateRandomString(_random, 2),generateRandomString(_random, 32),generateRandomString(_random, 32));
        Assert.assertFalse(findModalSaveButton().isEnabled());
    }

    @Test
    public void editPasswordWithEmptySiteFieldFails() throws InterruptedException{
        PasswordHelper.Password randomPassword = getARandomPasswordToEdit();
        editPassword(null,generateRandomString(_random, 32),generateRandomString(_random, 32));
        Assert.assertFalse(findModalSaveButton().isEnabled());
    }

    @Test
    public void editPasswordWithEmptyLoginFieldFails() throws InterruptedException{
        getARandomPasswordToEdit();
        editPassword(generateRandomString(_random, 32),null,generateRandomString(_random, 32));
        Assert.assertFalse(findModalSaveButton().isEnabled());
    }

    @Test
    public void editPasswordWithEmptyPasswordFieldFails() throws InterruptedException{
        getARandomPasswordToEdit();
        editPassword(generateRandomString(_random, 32),generateRandomString(_random, 32),null);
        Assert.assertFalse(findModalSaveButton().isEnabled());
    }

    @Test
    public void editPasswordWithInputlengthGeneratesCorrectLength() throws InterruptedException{
        PasswordHelper.Password randomPassword = getARandomPasswordToEdit();
        editPassword(generateRandomString(_random, 32),generateRandomString(_random, 32),generateRandomString(_random, 32));

        String id = randomPassword.id;

        clickGenerateButtonFromCreate();
        int length = _random.nextInt(32);
        driver.findElement(By.xpath("//input[@type='number']")).clear();
        driver.findElement(By.xpath("//input[@type='number']")).sendKeys(Integer.toString(length));
        clickGenerateFromGenerateModal();
        String generatedPassword = getPasswordField().getAttribute("value");
        assertEquals(length, generatedPassword.length());

        findModalSaveButton().click();
        Thread.sleep(1000);
        findModalSaveButton().click();
        waitUntilModalGone(driver);
        PasswordHelper.Password editedPassword  = getPasswordWithId(id).get();
        assertEquals(editedPassword.password.length(), length);
    }

    @Test
    public void editPasswordGenerateOverridesUserInputInPasswordCreateModal() throws InterruptedException{
        String site = generateRandomString(_random, 32);
        String login = generateRandomString(_random, 32);
        PasswordHelper.Password randomPassword = getARandomPasswordToEdit();

        String previousPassword = getPasswordField().getAttribute("value");
        fillCreatePasswordModal(site,login);

        String id = randomPassword.id;

        clickGenerateButtonFromCreate();
        driver.findElement(By.xpath("//input[@type='number']")).clear();
        driver.findElement(By.xpath("//input[@type='number']")).sendKeys(Integer.toString(_random.nextInt(32)));
        clickGenerateFromGenerateModal();
        String generatedPassword = getPasswordField().getAttribute("value");

        findModalSaveButton().click();
        String overridenPassword = getPasswordField().getAttribute("value");
        assertNotEquals(previousPassword, overridenPassword);
        assertEquals(generatedPassword, overridenPassword);

        findModalSaveButton().click();
        waitUntilModalGone(driver);
        PasswordHelper.Password editedPassword  = getPasswordWithId(id).get();
        assertEquals(editedPassword.password, generatedPassword);
    }


    @Test
    public void editPasswordGenereateWithNonRepeatedCharactersDoesNotRepeatCharacters() throws InterruptedException{
        String site = generateRandomString(_random, 32);
        String login = generateRandomString(_random, 32);
        PasswordHelper.Password randomPassword = getARandomPasswordToEdit();

        fillCreatePasswordModal(site,login);

        String id = randomPassword.id;

        clickGenerateButtonFromCreate();

        driver.findElement(By.xpath("//input[@type='number']")).clear();
        driver.findElement(By.xpath("//input[@type='number']")).sendKeys(Integer.toString(_random.nextInt(32)));
        driver.findElement(By.xpath("//input[@type='checkbox']")).click();
        clickGenerateFromGenerateModal();

        findModalSaveButton().click();
        findModalSaveButton().click();
        waitUntilModalGone(driver);
        PasswordHelper.Password editedPassword  = getPasswordWithId(id).get();
        assertFalse(isCharRepeated(editedPassword.password));
    }

    @Test
    public void generatePasswordDoesNotAllowUserToManuallyInputGeneratedPassword() throws InterruptedException{
        getARandomPasswordToEdit();
        clickGenerateButtonFromCreate();
        assertFalse(getPasswordField().isEnabled());
    }


    private Optional<PasswordHelper.Password> getPasswordWithId(String id) throws InterruptedException{
        //goto first page.
        _passwordHelper.givenOnFirstAcmePassPage();
        List<PasswordHelper.Password> passwords;
        while (true) {
            passwords = _passwordHelper.getPasswordsOnPage();
            boolean foundPassword = passwords.stream().anyMatch((storedPassword) ->
                    Objects.equals(storedPassword.id, id)
            );
            if (foundPassword) {
                return passwords.parallelStream().filter((storedPassword) ->
                        Objects.equals(storedPassword.id, id)).findFirst();
            }

            try {
                _passwordHelper.goToNextPage();
            } catch (NoSuchElementException e) {
                // no more passwords to try
                return null;
            }
        }
    }
    private WebElement findEditButton(WebElement element){
        return element.findElement(By.xpath(".//button[@ui-sref='acme-pass.edit({id:acmePass.id})']"));
    }
    @Test
    public void editPasswordWithlessThanThreeCharactersSiteFails() throws InterruptedException{

    }

    @Test
    public void editPasswordWithAnyEmptyFiledsFails() throws InterruptedException{

    }

    public boolean isCharRepeated(String input) {
        for (int i = 1; i < input.length(); ++i){
            if ((input.charAt(i) - input.charAt(i - 1) == 0)) {
                return true;
            }
        }
        return false;
    }

    public void fillCreatePasswordModal(String site, String login){

        driver.findElement(By.xpath("//input[@ng-model='vm.acmePass.site']")).clear();
        driver.findElement(By.xpath("//input[@ng-model='vm.acmePass.site']")).sendKeys(site);

        driver.findElement(By.xpath("//input[@ng-model='vm.acmePass.login']")).clear();
        driver.findElement(By.xpath("//input[@ng-model='vm.acmePass.login']")).sendKeys(login);
    }

    public void editPassword(String site, String login, String password){

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
}
