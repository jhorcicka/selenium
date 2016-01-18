package cz.hk.kuba.selenium.tester;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cz.hk.kuba.selenium.main.D;

public abstract class Tester {
    protected final int MAXIMUM_WAITING_TIME_IN_SECONDS = 15;
    protected static String USERNAME = "";
    protected static String PASSWORD = "";
    protected static String DEFAULT_URL = "";
    protected static String DEFAULT_TITLE = "";
    protected static String PROFILE_DIRECTORY = "/home/kuba/.mozilla/firefox/i9u98u6u.default";
    protected WebDriver driver = null;
    protected String baseUrl = "";

    public Tester() {
        createDriver();
    }

    private void createDriver() {
        File profileDirectory = new File(PROFILE_DIRECTORY);

        if (profileDirectory.exists()) {
            FirefoxProfile profile = new FirefoxProfile(profileDirectory);
            driver = new FirefoxDriver(profile);
        } else {
            driver = new FirefoxDriver();
        }
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public List<Integer> getIds(String name) {
        if (name == null) {
            return getINTISIds();
        } else {
            return getWebIds(name);
        }
    }

    private List<Integer> getINTISIds() {
        List<Integer> ids = new ArrayList<>();
        String xpath = "//a[@tabindex='-1' and starts-with(@href, 'http://trunk.') and contains(@href, 'nahled')]";
        List<WebElement> foundElements = driver.findElements(By.xpath(xpath));

        for (WebElement element : foundElements) {
            String hrefValue = element.getAttribute("href");
            String paramName = "ac_id=";
            int fromIndex = hrefValue.indexOf(paramName) + paramName.length();
            int toIndex = hrefValue.length();
            String idString = hrefValue.substring(fromIndex, toIndex);
            Integer newId = Integer.parseInt(idString);
            ids.add(newId);
        }

        return ids;
    }

    private List<Integer> getWebIds(String name) {
        List<Integer> ids = new ArrayList<>();
        String xpath = "//*[@type='submit' and starts-with(@name, '" + name + "')]";
        List<WebElement> foundElements = driver.findElements(By.xpath(xpath));
        int fromIndex = name.length() + 1;

        for (WebElement element : foundElements) {
            String nameValue = element.getAttribute("name");
            int toIndex = nameValue.length() - 1;
            String idString = nameValue.substring(fromIndex, toIndex);
            Integer newId = Integer.parseInt(idString);
            ids.add(newId);
        }

        return ids;
    }

    public void submit(String text) {
        WebElement element = getElement(text);

        if (element != null) {
            element.submit();
        }
    }

    public void checkText(String text) throws Exception {
        String source = driver.getPageSource();
        boolean present = source.contains(text);

        if (present == false) {
            throw new Exception("Missing text: " + text);
        }
    }

    public boolean containsText(String text) {
        String source = driver.getPageSource();
        boolean present = source.contains(text);

        return present;
    }

    public void checkMissingText(String... texts) throws RuntimeException {
        for (String text : texts) {
            if (containsText(text)) {
                throw new RuntimeException(
                        String.format("Text '%s' was found on the page but it should not be there. ", text));
            }
        }
    }

    public void fill(String identificatorText, String value) {
        WebElement element = getElement(identificatorText);

        if (element != null) {
            try {
                element.sendKeys(value);
            } catch (ElementNotVisibleException notVisibleEx) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(String.format("document.getElementById('%s').value = '%s'", identificatorText, value));
            }
        }
    }

    public void setCheckbox(String identificatorText, String value) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<String> commands = new ArrayList();
        commands.add(String.format("document.getElementById('%s').checked = '%s'", identificatorText, value));
        commands.add(String.format(
                "var elements = document.getElementsByName('%s');"
                        + "for (var i = 0; i < elements.length; i++) {"
                        + "  elements[i].checked = '%s'"
                        + "}",
                identificatorText, value));

        for (String command : commands) {
            try {
                js.executeScript(command);
            } catch (WebDriverException ex) {
            }
        }
    }

    public void click(String text) {
        WebElement element = getElement(text);

        if (element != null) {
            element.click();
        }
    }

    public void click(By by) {
        WebElement element = driver.findElement(by);

        if (element != null) {
            element.click();
        }
    }

    public void waitAndClick(String text) {
        WebDriverWait wait = new WebDriverWait(driver, MAXIMUM_WAITING_TIME_IN_SECONDS);
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText(text)));
        click(text);
    }

    public void back() {
        driver.navigate().back();
    }

    public void home() {
        By homeLogoLink = By.className("logo-left");
        WebElement pageFooter = (new WebDriverWait(driver, MAXIMUM_WAITING_TIME_IN_SECONDS)).until(
                ExpectedConditions.elementToBeClickable(homeLogoLink));
        click(homeLogoLink);
    }

    private WebElement getElement(String text) {
        WebElement element = null;
        D.d("Tester.getElement(" + text + ")");

        List<By> attempts = new ArrayList();
        attempts.add(By.linkText(text));
        attempts.add(By.id(text));
        attempts.add(By.name(text));
        attempts.add(By.xpath("//*[@value='" + text + "']"));
        attempts.add(By.xpath("//*[(@type='button' or @type='submit') and text()='" + text + "']"));
        attempts.add(By.xpath("//*[contains(@href, '" + text + "')]"));
        attempts.add(By.partialLinkText(text));

        for (By by : attempts) {
            try {
                element = driver.findElement(by);
                waitForPageToLoad();
                break;
            } catch (WebDriverException noSuchElement) {
            }
        }

        return element;
    }

    public void checkMissing(String text) throws Exception {
        boolean missing = false;

        try {
            getElement(text);
        } catch (NoSuchElementException noSuchElement) {
            missing = true;
        }

        if (missing == false) {
            throw new Exception("Required element (" + text + ") was found (but it should not be there). ");
        }
    }

    public void checkPresence(String text) throws Exception {
        boolean present = true;

        try {
            getElement(text);
        } catch (NoSuchElementException noSuchElement) {
            present = false;
        }

        if (present == false) {
            throw new Exception("Required element (" + text + ") was not found on the page (but it should be there). ");
        }
    }

    public void goTo(String url) {
        D.d("Tester.goTo(" + url + ")");
        driver.get(url);
        waitForPageToLoad();
    }

    protected abstract void waitForPageToLoad();

    public void login() {
        driver.get(baseUrl);
        WebElement username = driver.findElement(By.id("username"));
        username.sendKeys(USERNAME);
        WebElement password = driver.findElement(By.id("password"));
        password.sendKeys(PASSWORD);
        password.submit();
    }

    public void logout() {
        goTo(baseUrl + "/logout.php");
    }

    public void closeBrowser() {
        driver.close();
    }

    public boolean testFunction() {
        driver.get(DEFAULT_URL);
        String title = driver.getTitle();

        return title.equals(DEFAULT_TITLE);
    }

    public void printResult() {
        System.out.println(String.format("Test '%s' was finished. ", getClass().toString()));
    }

    public void init() {
    }

    public boolean test() {
        return true;
    }

    public void clean() {
    }
}
