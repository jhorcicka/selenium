package cz.hk.kuba.selenium.tester;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebTester extends Tester {
    public WebTester(String baseUrl) {
        initConstants();
        this.baseUrl = baseUrl;
    }

    public WebTester() {
        initConstants();
        this.baseUrl = DEFAULT_URL;
    }

    private void initConstants() {
        USERNAME = "msvec5655";
        PASSWORD = "91gkg3";
        DEFAULT_URL = "http://www.trunk.jhorcicka.test.cz/run-12?ac=form&node=12";
        DEFAULT_TITLE = "FG: Web - run";
    }

    @Override
    public void login() {
        driver.get(baseUrl);
        WebElement username = driver.findElement(By.id("username"));
        username.sendKeys(USERNAME);
        WebElement password = driver.findElement(By.id("password"));
        password.sendKeys(PASSWORD);
        click("_subLogin");
    }

    @Override
    public void logout() {
        click(By.className("left"));
    }

    @Override
    protected void waitForPageToLoad() {
        WebElement pageFooter = (new WebDriverWait(driver, MAXIMUM_WAITING_TIME_IN_SECONDS)).until(
                new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(By.tagName("body"));
                    }
                });
    }
}
