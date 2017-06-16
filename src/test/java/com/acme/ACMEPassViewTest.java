package com.acme;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.acme.Util.generateRandomString;
import static org.junit.Assert.assertTrue;

public class ACMEPassViewTest extends ACMEPassTestBase {
    private PasswordHelper passwordHelper;
    private Random random;
    private WebDriver driver;
    private String url;

    @Before
    public void setUp() throws Exception {
        url = "http://localhost:8080/#/";

        driver = getDriver("firefox");

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        passwordHelper = new PasswordHelper(new LoginHelper(driver, url), driver, url);
        random = new Random();
    }

    @Test
    public void sortingTest() throws InterruptedException {
        if (!passwordHelper.aPasswordExists()) {
            passwordHelper.createPassword("acme.com", "admin", "password");
            passwordHelper.createPassword("bacme.com", "badmin", "qassword");
        }

        String site = generateRandomString(random, 32);
        String login = generateRandomString(random, 32);
        String password = generateRandomString(random, 32);
        passwordHelper.createPassword(site, login, password);

        assertTrue(passwordHelper.passwordEntryExists(site, login, password));
    }

    @After
    public void tearDown() {
        driver.close();
    }
}
