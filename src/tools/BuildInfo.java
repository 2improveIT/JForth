package tools;

public class BuildInfo {
    private static final String BUILD_NUMBER = "2711";
    private static final String BUILD_DATE = "09/27/2021 11:22:56 PM";

    public static final String buildInfo = "JForth, Build: " + BUILD_NUMBER + ", " + BUILD_DATE
            + " -- " + System.getProperty("java.version");

}
