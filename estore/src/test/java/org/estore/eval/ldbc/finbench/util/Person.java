package org.estore.eval.ldbc.finbench.util;

import java.io.Serializable;

public class Person implements Serializable {
    private long personId;
    private String personName;
    private boolean isBlocked;
    private String createTime;
    private String gender;
    private String birthday;
    private String country;
    private String city;
    private Loan[] Apply;
    private Person[] Guarantee;
    private Company[] Invest;
    private Account[] Own;

    public Person(
            long personId,
            String personName,
            boolean isBlocked,
            String createTime,
            String gender,
            String birthday,
            String country,
            String city) {
        this.personId = personId;
        this.personName = personName;
        this.isBlocked = isBlocked;
        this.createTime = createTime;
        this.gender = gender;
        this.birthday = birthday;
        this.country = country;
        this.city = city;
    }

    public Person() {}

    public void setApply(Object[] Apply) {
        this.Apply = new Loan[Apply.length];
        for (int j = 0; j < Apply.length; j++) {
            this.Apply[j] = (Loan) Apply[j];
        }
    }

    public void setGuarantee(Object[] Guarantee) {
        this.Guarantee = new Person[Guarantee.length];
        for (int j = 0; j < Guarantee.length; j++) {
            this.Guarantee[j] = (Person) Guarantee[j];
        }
    }

    public void setInvest(Object[] Invest) {
        this.Invest = new Company[Invest.length];
        for (int j = 0; j < Invest.length; j++) {
            this.Invest[j] = (Company) Invest[j];
        }
    }

    public void setOwn(Object[] Own) {
        this.Own = new Account[Own.length];
        for (int j = 0; j < Own.length; j++) {
            this.Own[j] = (Account) Own[j];
        }
    }
}
