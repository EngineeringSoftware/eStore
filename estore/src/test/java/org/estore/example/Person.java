package org.estore.example;

public class Person {
    public String name;
    public int age;
    public Person friend;
    public Person friend2;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age, Person friend) {
        this.name = name;
        this.age = age;
        this.friend = friend;
    }

    @Override
    public String toString() {
        return name + " (" + age + ")";
    }
}
