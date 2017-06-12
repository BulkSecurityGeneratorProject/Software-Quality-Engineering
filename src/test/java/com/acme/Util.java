package com.acme;

import org.openqa.selenium.*;

import java.util.Random;

@SuppressWarnings("WeakerAccess")
public class Util {
    private static final long TIMEOUT = 5000;
    private static final long POLL = 100;

    public static boolean exists(WebDriver driver, By by) {
        try {
            driver.findElement(by);
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    public static void waitUntilGone(WebDriver driver, By by) {
        long start = System.currentTimeMillis();

        while (true) {
            if (!exists(driver, by)) {
                return;
            }

            if (System.currentTimeMillis() > start + TIMEOUT) {
                throw new TimeoutException();
            }
            try {
                Thread.sleep(POLL);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void waitUntilModalGone(WebDriver driver) {
        waitUntilGone(driver, By.xpath("//body[@class='modal-open']"));
    }

    public static WebElement getModalSubmitButton(WebDriver driver) {
        return driver.findElement(By.xpath("//div[@class='modal-footer'][last()]/button[@type='submit']"));
    }

    public static boolean isModalSubmitButtonEnabled(WebDriver driver) {
        return getModalSubmitButton(driver).getAttribute("disabled") == null;
    }

    public static void confirmModal(WebDriver driver) {
        getModalSubmitButton(driver).click();
        waitUntilModalGone(driver);
    }

    public static String generateRandomString(Random random, int size) {
        StringBuilder builder = new StringBuilder();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < size; i++) {
            builder.append(chars.charAt(random.nextInt(chars.length())));
        }

        return builder.toString();
    }
}
