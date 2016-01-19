package cz.hk.kuba.selenium.testcase;

import org.openqa.selenium.UnhandledAlertException;

import cz.hk.kuba.selenium.main.D;
import cz.hk.kuba.selenium.tester.WebTester;

public class DCWebsite extends WebTester {
    public DCWebsite(String url) {
        super(url);
    }

    public void init() {
    }

    @Override
    public void login() {
    }

    @Override
    public void logout() {
    }

    public boolean test() {
        try {
            goTo(baseUrl);
            String[] menuItems = {
                    "DataCleaner",
                    "News",
                    "Documentation",
                    "Download",
                    "Product",
                    "Community",
                    "Contact",
                    "Account",
                    "Sign" // "Sign in" or "Sing out"
            };

            for (String text : menuItems) {
                checkPresence(text);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public void clean() {
    }
}