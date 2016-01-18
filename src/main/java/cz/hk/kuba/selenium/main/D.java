package cz.hk.kuba.selenium.main;

public class D {
    private static final String DEBUG = "DEBUG";
    private static boolean debuggingOn = true;

    public static void d(Object... parameters) {
        if (!D.debuggingOn) {
            return;
        }

        System.out.println(D.DEBUG + ": ");

        for (Object parameter : parameters) {
            System.out.println("  " + parameter);
        }
    }
}
