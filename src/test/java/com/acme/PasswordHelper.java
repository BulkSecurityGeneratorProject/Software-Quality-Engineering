package com.acme;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.acme.Util.*;

public class PasswordHelper {
    private final LoginHelper _loginHelper;

    private static final String ADMIN_USERNAME = "admin@acme.com";
    private static final String ADMIN_PASSWORD = "K-10ficile";

    private final WebDriver _driver;
    private final String _rootUrl;

    public PasswordHelper(
            @NotNull LoginHelper loginHelper,
            @NotNull WebDriver driver,
            @NotNull String rootUrl
    ) {
        _loginHelper = loginHelper;
        _driver = driver;
        _rootUrl = rootUrl;
    }

    public boolean doesPasswordExist() throws InterruptedException {
        givenOnAcmePassPage();
        return exists(_driver, By.xpath("//input[@type='password']"));
    }

    public void goToNextPage() {
        // Will not find anything if there is no next page.
        _driver.findElement(By.xpath("//li[@class='next']/a")).click();
    }

    public PasswordCreationModalHelper openPasswordCreationModal() throws InterruptedException {
        givenOnAcmePassPage();
        _driver.findElement(By.xpath("//button[@ui-sref='acme-pass.new']")).click();
        return new PasswordCreationModalHelper();
    }

    public void createPassword(String url, String login, String password) throws InterruptedException {
        PasswordCreationModalHelper modal = openPasswordCreationModal();

        modal.findSiteElement().sendKeys(url);
        modal.findLoginElement().sendKeys(login);
        modal.findPasswordElement().sendKeys(password);

        Thread.sleep(500); // wait for the modal to load.
        confirmModal(_driver);
    }

    public void givenAPasswordExists() throws InterruptedException {
        givenOnAcmePassPage();

        if (!doesPasswordExist()) {
            createPassword("acme.com", "admin", "password");
        }
    }

    public void givenOnAcmePassPage() throws InterruptedException {
        if (!_driver.getCurrentUrl().equals(_rootUrl + "acme-pass")) {
            if (!_loginHelper.isLoggedIn()) {
                _loginHelper.loginWith(ADMIN_USERNAME, ADMIN_PASSWORD);
            } else {
                _driver.navigate().to(_rootUrl + "acme-pass");
            }
        }
    }

    public ArrayList<Password> getPasswordsOnPage() {
        //select rows
        ArrayList<Password> passwords = new ArrayList<>();
        List<WebElement> passwordRows = _driver.findElements(By.xpath("//tr[td//button[@ui-sref='acme-pass.edit({id:acmePass.id})']]"));

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

    public void deletePassword(Password password) {
        password.element.findElement(By.xpath(".//button[@ui-sref='acme-pass.delete({id:acmePass.id})']")).click();
        confirmModal(_driver);
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

    public class PasswordCreationModalHelper {
        public WebElement findSiteElement() {
            return _driver.findElement(By.xpath("//input[@ng-model='vm.acmePass.site']"));
        }

        public WebElement findLoginElement() {
            return _driver.findElement(By.xpath("//input[@ng-model='vm.acmePass.login']"));
        }

        public WebElement findPasswordElement() {
            return _driver.findElement(By.xpath("//input[@ng-model='vm.acmePass.password']"));
        }
    }
}
