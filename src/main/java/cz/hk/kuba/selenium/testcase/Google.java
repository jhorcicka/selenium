package cz.hk.kuba.selenium.testcase;

import org.openqa.selenium.UnhandledAlertException;

import cz.hk.kuba.selenium.main.D;
import cz.hk.kuba.selenium.tester.WebTester;

public class Google extends WebTester {
    public Google(String url) {
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
            String url = "http://www.google.com";
            D.d(url);
            goTo(url);
        }
        catch (UnhandledAlertException ex) {
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public void clean() {
    }
}