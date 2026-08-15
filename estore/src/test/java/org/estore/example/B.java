package org.estore.example;

public class B {

    long field1;
    String field2;
    double field3;
    C c;

    public B() {
        field1 = 10L;
        field2 = "Hello World";
        field3 = 20.0;
        c = new C();
    }

    public B(long field1) {
        this.field1 = field1;
        field2 = "Hello World";
        field3 = 20.0;
        c = new C();
    }
}
