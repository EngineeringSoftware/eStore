package org.estore;

public class EstoreEdge {
    private Object referrerObject;
    private Object refereeObject;
    private String name;

    public EstoreEdge(Object referrerObject, Object refereeObject, String name) {
        this.referrerObject = referrerObject;
        this.refereeObject = refereeObject;
        this.name = name;
    }

    public Object getReferrerObject() {
        return referrerObject;
    }

    public Object getRefereeObject() {
        return refereeObject;
    }

    public String getName() {
        return name;
    }
}
