package tools;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2829";
    private static final String BUILD_DATE = "10/08/2021 01:14:56 PM";

    public static final String buildInfo = "JForth, Build: " + BUILD_NUMBER + ", " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
