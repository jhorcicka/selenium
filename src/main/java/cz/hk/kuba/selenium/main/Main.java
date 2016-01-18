package cz.hk.kuba.selenium.main;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.UnhandledAlertException;

import cz.hk.kuba.selenium.db.Db;
import cz.hk.kuba.selenium.testcase.Google;
import cz.hk.kuba.selenium.tester.WebTester;

public class Main {
    public static void main(String[] args) {
        doWebTests();
        //testDbQueries();
        //testDb();
    }

    private static void doWebTests() {
        List<WebTester> testers = new ArrayList<>();
        testers.add(new Google("http://www.atlas.cz"));

        for (WebTester tester : testers) {
            try {
                tester.login();
                tester.test();
                tester.logout();
                tester.printResult();
                tester.closeBrowser();
            } catch (UnhandledAlertException ex) {
                tester.closeBrowser();
            }
        }
    }

    private static void testDbQueries() {
        Db db = Db.getInstance();
        db.connect();

        D.d("Count=" + db.getCount("test"));
        D.d("Last ID=" + db.getLastId("test"));

        db.disconnect();
    }

    private static void testDb() {
        Db db = Db.getInstance();
        db.connect();

        if (db.test()) {
            D.d("Test OK");
        } else {
            D.d("Problem with connection. ");
        }

        db.disconnect();
    }
}
