package org.estore.eval;

public final class EvalUtil {
    private EvalUtil() {}

    public static void printQueryTime(String queryName, long startNanos) {
        String profileOpt = System.getProperty("profile");
        if (profileOpt != null && profileOpt.equals("true")) {
            System.out.println(queryName);
            System.out.println(
                    "Time: " + ((float) (System.nanoTime() - startNanos)) / 1_000_000.0 + "ms");
        }
    }
}
