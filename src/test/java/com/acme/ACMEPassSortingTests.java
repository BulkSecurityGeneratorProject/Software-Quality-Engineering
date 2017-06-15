package com.acme;

import com.google.common.collect.Ordering;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/*
 ACMEPassSortingTests
 Tests sorting functionality by ID, Site, Login, Create Date, Last Modified Date
 Test assumes that users already exist with multiple pages.
 Test passes if and only if the sorted columns are sorted.
 Test fails if and only if the sorted columns are not sorted.
 */

@RunWith(Parameterized.class)
public class ACMEPassSortingTests extends ACMEPassTestBase {

    private String browser;
    private String user;
    private String password;

    private final int ID_INDEX = 1;
    private final int SITE_INDEX = 2;
    private final int PASSWORD_INDEX = 3;
    private final int LOGIN_INDEX = 4;
    private final int CREATE_DATE_INDEX = 5;
    private final int MODIFIED_DATE_INDEX = 6;


    @Before
    public void setUp() throws Exception {
        url = "http://localhost:8080/#/";

        driver = getDriver(browser);

        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        loginWith(user, password);
    }

    public ACMEPassSortingTests(String browser, String user, String password) {
        this.browser = browser;
        this.user = user;
        this.password = password;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> users() {
        return Arrays.asList( new Object[][] {
                //{ "firefox", "frank.paul@acme.com", "starwars"},    // Manager
                { "firefox", "jo.thomas@acme.com",  "mustang" },    // Employee
                //{ "firefox", "admin@acme.com",      "K-10ficile" }, // Admin
        });
    }

    @Test
    public void sortingTest() throws InterruptedException {
        PasswordHelper helper = new PasswordHelper(new LoginHelper(driver, url), driver, url);
        if (!helper.aPasswordExists()) {
            helper.createPassword("acme.com", "admin", "password");
            helper.createPassword("bacme.com", "badmin", "qassword");
        }

        testSort(ID_INDEX);
        testSort(SITE_INDEX);
        testSort(PASSWORD_INDEX);
        testSort(LOGIN_INDEX);
        testSort(CREATE_DATE_INDEX);
        testSort(MODIFIED_DATE_INDEX);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    private void testSort(int id) {
        // Ensure in ascending order mode
        driver.findElement(By.xpath("//table/thead/tr/th[" + id + "]")).click();
        try {
            driver.findElement(By.className("glyphicon-sort-by-attributes"));
        } catch (NoSuchElementException e) {
            driver.findElement(By.xpath("//table/thead/tr/th[" + id + "]")).click();
        }

        // Check image for descending class="glyphicon glyphicon-sort-by-attributes"
        WebElement sortedIcon = driver.findElement(By.className("glyphicon-sort-by-attributes"));
        Assert.assertNotNull(sortedIcon);

        // Check sorted order
        WebElement baseTable = driver.findElement(By.className("jh-table"));
        List<WebElement> tableRows = baseTable.findElements(By.tagName("tr"));
        List<String> list = loadItemsFromRow(id - 1, tableRows);

        if (list.isEmpty()) return;
        Assert.assertTrue(isSortedAscending(list, id));

        // Click id to sort in descending
        driver.findElement(By.xpath("//table/thead/tr/th[" + id + "]")).click();

        // Check image for ascending class="glyphicon glyphicon-sort-by-attributes-alt"
        sortedIcon = driver.findElement(By.className("glyphicon-sort-by-attributes-alt"));
        Assert.assertNotNull(sortedIcon);

        // Check sorted order
        baseTable = driver.findElement(By.className("jh-table"));
        tableRows = baseTable.findElements(By.tagName("tr"));
        list = loadItemsFromRow(id-1, tableRows);

        if (list.isEmpty()) return;
        Assert.assertTrue(isSortedDescending(list, id));
    }

    private ArrayList<String> loadItemsFromRow(int index, List<WebElement> rows) {
        ArrayList<String> items = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<WebElement> rowItems = rows.get(i).findElements(By.xpath("//tr[" + i + "]/td"));
            if (index != PASSWORD_INDEX) {
                items.add(rowItems.get(index).getText());
            } else {
                items.add(rowItems.get(index).findElement(By.xpath(".//input")).getAttribute("value"));
            }
        }
        return items;
    }

    private boolean isSortedDescending(List<String> items, int id) {
        if (id != ID_INDEX) {
            return (Ordering.natural().reverse().isOrdered(items));
        } else {
            List<Integer> ids = new ArrayList<>();
            items.forEach(s -> {
                ids.add(Integer.parseInt(s));
            });
            return (Ordering.natural().reverse().isOrdered(ids));
        }
    }

    private boolean isSortedAscending(List<String> items, int id) {
        if (id != ID_INDEX) {
            return (Ordering.natural().isOrdered(items));
        } else {
            List<Integer> ids = new ArrayList<>();
            items.forEach(s -> {
                ids.add(Integer.parseInt(s));
            });
            return (Ordering.natural().isOrdered(ids));
        }
    }
}
