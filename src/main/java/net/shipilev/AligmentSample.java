package net.shipilev;

import org.openjdk.jol.info.ClassLayout;

public class AligmentSample {

    public static void main(String... args) {
        System.out.println(ClassLayout.parseClass(C.class).toPrintable());
    }

    public static class C {
        boolean b;
        char c;
        long l;
    }

}
