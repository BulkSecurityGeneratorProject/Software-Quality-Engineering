package com.acme;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ACMEPassSortingTests extends ACMEPassTestBase {

    //private WebDriver driver;
    //private String url;
    private String browser;
    private String user;
    private String password;
    private ArrayList<String> ids;
    private ArrayList<String> sites;
    private ArrayList<String> logins;
    private ArrayList<String> createdDates;
    private ArrayList<String> lastModifiedDates;

    @Before
    public void setUp() throws Exception {
        url = "http://localhost:8080/#/";
        ids = new ArrayList<>();
        sites = new ArrayList<>();
        logins = new ArrayList<>();
        createdDates = new ArrayList<>();
        lastModifiedDates = new ArrayList<>();

        driver = getDriver(browser);

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
                { "firefox", "frank.paul@acme.com", "starwars"},    // Manager
                { "firefox", "jo.thomas@acme.com",  "mustang" },    // Employee
                { "firefox", "admin@acme.com",      "K-10ficile" }, // Admin
                { "chrome",  "frank.paul@acme.com", "starwars"},    // Manager
                { "chrome",  "jo.thomas@acme.com",  "mustang" },    // Employee
                { "chrome",  "admin@acme.com",      "K-10ficile" }, // Admin
        });
    }

    @Test
    public void sortingTest() throws InterruptedException {
        testSort(ids, 1);
        testSort(sites, 2);
        testSort(logins, 4);
        testSort(createdDates, 5);
        testSort(lastModifiedDates, 6);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    private void testSort(List<String> list, int id) {
        // Click id to sort in ascending
        if (id != 1)
            driver.findElement(By.xpath("//table/thead/tr/th[" + id + "]")).click();

        // Check image for descending class="glyphicon glyphicon-sort-by-attributes-alt"
        WebElement sortedIcon = driver.findElement(By.className("glyphicon-sort-by-attributes-alt"));
        Assert.assertNotNull(sortedIcon);

        // Check sorted order
        // TODO: FIX ICON, IT'S REVERSED
        // TODO: DISABLE SORT BY PASSWORD <-- security risk
        WebElement baseTable = driver.findElement(By.className("jh-table"));
        List<WebElement> tableIds = baseTable.findElements(By.tagName("tr"));
        list = loadItemsFromRow(id-1, tableIds);
        if (list.isEmpty()) return;
        Assert.assertTrue(isSortedAscending(list));

        // Click id to sort in descending
        driver.findElement(By.xpath("//table/thead/tr/th[" + id + "]")).click();

        // Check image for ascending class="glyphicon glyphicon-sort-by-attributes"
        sortedIcon = driver.findElement(By.className("glyphicon-sort-by-attributes"));
        Assert.assertNotNull(sortedIcon);

        // Check sorted order
        baseTable = driver.findElement(By.className("jh-table"));
        tableIds = baseTable.findElements(By.tagName("tr"));
        list = loadItemsFromRow(id-1, tableIds);
        Assert.assertTrue(!list.isEmpty());
        Assert.assertTrue(isSortedDescending(list));
    }

    private ArrayList<String> loadItemsFromRow(int index, List<WebElement> rows) {
        ArrayList<String> items = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<WebElement> rowItems = rows.get(i).findElements(By.xpath("//tr[" + i + "]/td"));
            items.add(rowItems.get(index).getText());
        }
        return items;
    }

    private boolean isSortedDescending(List<String> items) {
        for (int i = 0; i < items.size() - 1; i++) {
            if (items.get(i).compareTo(items.get(i+1)) < 0 ) return false;
        }
        return true;
    }

    private boolean isSortedAscending(List<String> items) {
        for (int i = 0; i < items.size() - 1; i++) {
            if (items.get(i).compareTo(items.get(i+1)) > 0 ) return false;
        }
        return true;
    }
}
