package main.test;

public class Test {
    public static void assertEqualInt(int i1, int i2) {
        if (i1 != i2) {
            System.out.println("Assertion failed!\n" + i1 + " != " + i2);
            new Throwable().printStackTrace(System.out);
            System.exit(0);
        }
    }

    public static void assertDifferentInt(int i1, int i2) {
        if (i1 == i2) {
            System.out.println("Assertion failed!\n" + i1 + " == " + i2);
            new Throwable().printStackTrace(System.out);
            System.exit(0);
        }
    }

    public static void assertDifferentString(String s1, String s2) {
        if (s1.equals(s2)) {
            System.out.println("Assertion failed!\n" + s1 + " == " + s2);
            new Throwable().printStackTrace(System.out);
            System.exit(0);
        }
    }
}
