package org.estore.eval.ldbc.finbench.util;

import java.io.Serializable;

public class Company implements Serializable {
    private long companyId;
    private String companyName;
    private boolean isBlocked;
    private String createTime;
    private String country;
    private String city;
    private String business;
    private String description;
    private String url;
    private Loan[] Apply;
    private Company[] Guarantee;
    private Company[] Invest;
    private Account[] Own;

    public Company(
            long companyId,
            String companyName,
            boolean isBlocked,
            String createTime,
            String country,
            String city,
            String business,
            String description,
            String url) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.isBlocked = isBlocked;
        this.createTime = createTime;
        this.country = country;
        this.city = city;
        this.business = business;
        this.description = description;
        this.url = url;
    }

    public Company() {}

    public void setGuarantee(Object[] Guarantee) {
        this.Guarantee = new Company[Guarantee.length];
        for (int j = 0; j < Guarantee.length; j++) {
            this.Guarantee[j] = (Company) Guarantee[j];
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

    public void setApply(Object[] Apply) {
        this.Apply = new Loan[Apply.length];
        for (int j = 0; j < Apply.length; j++) {
            this.Apply[j] = (Loan) Apply[j];
        }
    }
}
