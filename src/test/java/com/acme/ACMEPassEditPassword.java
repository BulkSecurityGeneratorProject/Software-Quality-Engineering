package com.acme;

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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ACMEPassEditPassword extends ACMEPassTestBase {

    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    private String browser;
    private String username;
    private String password;
    private Random random;

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

    public ACMEPassEditPassword(String browser, String username, String password) {
        this.browser = browser;
        this.username = username;
        this.password = password;
    }

    @Before
    public void setUp() throws Exception {
        url = "http://localhost:8080/#/";
        driver = getDriver(browser);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
        loginWith(username, password);
    }

    @Test
    public void editPasswordGoldenPath() throws InterruptedException{

        WebElement button = driver.findElement(By.cssSelector("button.btn.btn-primary"));
        button.click();
        createPassword("site.com", "test_login", "dduude");

        //The 'save' button
        driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).click();

        WebElement password = driver.findElement(By.xpath("//input[@type='password']"));
        Assert.assertNotNull(password);

        ArrayList<Password> passwordRows = getPasswordList();
        passwordRows.get(0).element.findElement(By.xpath(".//button[@ui-sref='acme-pass.edit({id:acmePass.id})']")).click();
        createPassword("edit","modifiedLogin","modified");

        driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).click();
        Thread.sleep(5000);
        List<WebElement> modified = driver.findElements(By.xpath("//table/tbody/tr[1]/td"));
        assertEquals("edit", modified.get(1).getText());
        assertEquals("modifiedLogin", modified.get(2).getText());
        //TODO: VERIFY PASSWORD CHANGE
    }

    @Test
    public void editPasswordWithlessThanThreeCharactersSiteFails() throws InterruptedException{
        WebElement button = driver.findElement(By.cssSelector("button.btn.btn-primary"));
        button.click();
        createPassword("site.com", "test_login", "dduude");

        //The 'save' button
        driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).click();

        WebElement password = driver.findElement(By.xpath("//input[@type='password']"));
        Assert.assertNotNull(password);

        ArrayList<Password> passwords = new ArrayList<>();
        List<WebElement> passwordRows = driver.findElements(By.xpath("//tr[td//button[@ui-sref='acme-pass.edit({id:acmePass.id})']]"));
        passwordRows.get(0).findElement(By.xpath(".//button[@ui-sref='acme-pass.edit({id:acmePass.id})']")).click();
        createPassword("sr","modifiedLogin","modified");

        Assert.assertFalse(driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).isEnabled());
    }

    @Test
    public void editPasswordWithAnyEmptyFiledsFails() throws InterruptedException{
        WebElement button = driver.findElement(By.cssSelector("button.btn.btn-primary"));
        button.click();
        createPassword("site.com", "test_login", "dduude");

        //The 'save' button
        driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).click();

        WebElement password = driver.findElement(By.xpath("//input[@type='password']"));
        Assert.assertNotNull(password);

        ArrayList<Password> passwords = new ArrayList<>();
        List<WebElement> passwordRows = driver.findElements(By.xpath("//tr[td//button[@ui-sref='acme-pass.edit({id:acmePass.id})']]"));
        passwordRows.get(0).findElement(By.xpath(".//button[@ui-sref='acme-pass.edit({id:acmePass.id})']")).click();

        createPassword("teeeeiw", null, "dduude");
        Assert.assertFalse(driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).isEnabled());

        createPassword(null, "someValue", "dduude");
        Assert.assertFalse(driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).isEnabled());

        createPassword("someSite", "loginVal", null);
        Assert.assertFalse(driver.findElement(By.cssSelector("div.modal-footer > button.btn.btn-primary")).isEnabled());
    }

    public ArrayList<Password> getPasswordList() {
        //select rows
        ArrayList<Password> passwords = new ArrayList<>();
        List<WebElement> passwordRows = driver.findElements(By.xpath("//tr[td//button[@ui-sref='acme-pass.edit({id:acmePass.id})']]"));

        for (WebElement passwordRow : passwordRows) {
            String id = passwordRow.findElement(By.xpath("td[position() = 1]")).getText();
            String site = passwordRow.findElement(By.xpath("td[position() = 2]")).getText();
            String login = passwordRow.findElement(By.xpath("td[position() = 3]")).getText();
            String password = passwordRow.findElement(By.xpath("td[position() = 4]//input")).getAttribute("value");
            String createdDate = passwordRow.findElement(By.xpath("td[position() = 5]")).getText();
            String lastModifiedDate = passwordRow.findElement(By.xpath("td[position() = 6]")).getText();
            passwords.add(new Password(passwordRow, id, site, login, password, createdDate, lastModifiedDate));
        }

        return passwords;
    }

    public class Password {
        final WebElement element;
        final String id;
        final String site;
        final String login;
        final String password;
        final String createdDate;
        final String lastModifiedDate;

        public Password(
                @NotNull WebElement element,
                @NotNull String id,
                @NotNull String site,
                @NotNull String login,
                @NotNull String password,
                @NotNull String createdDate,
                @NotNull String lastModifiedDate
        ) {
            this.element = element;
            this.id = id;
            this.site = site;
            this.login = login;
            this.password = password;
            this.createdDate = createdDate;
            this.lastModifiedDate = lastModifiedDate;
        }

        @Override
        public String toString() {
            return "{Password [id]:" + id
                    + " [site]:" + site
                    + " [login]:" + login
                    + " [password]:" + password
                    + " [createdDate]:" + createdDate
                    + " [lastModifiedDate]:" + lastModifiedDate + "}";
        }
    }


    private ArrayList<String> loadItemsFromRow(int index, List<WebElement> rows) {
        ArrayList<String> items = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<WebElement> rowItems = rows.get(i).findElements(By.xpath("//tr[" + i + "]/td"));
            items.add(rowItems.get(index).getText());
        }
        return items;
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
//
//    private String closeAlertAndGetItsText() {
//        try {
//            Alert alert = driver.switchTo().alert();
//            String alertText = alert.getText();
//            if (acceptNextAlert) {
//                alert.accept();
//            } else {
//                alert.dismiss();
//            }
//            return alertText;
//        } finally {
//            acceptNextAlert = true;
//        }
//    }
}
