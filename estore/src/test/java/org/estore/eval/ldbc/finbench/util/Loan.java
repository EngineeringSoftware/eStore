package org.estore.eval.ldbc.finbench.util;

import java.io.Serializable;

public class Loan implements Serializable {
    private long loanId;
    private double loanAmount;
    private double balance;
    private String createTime;
    private String loanUsage;
    private double interestRate;
    private Account[] Deposit;

    public Loan(
            long loanId,
            double loanAmount,
            double balance,
            String createTime,
            String loanUsage,
            double interestRate) {
        this.loanId = loanId;
        this.loanAmount = loanAmount;
        this.balance = balance;
        this.createTime = createTime;
        this.loanUsage = loanUsage;
        this.interestRate = interestRate;
    }

    public Loan() {}

    public void setDeposit(Object[] Deposit) {
        this.Deposit = new Account[Deposit.length];
        for (int j = 0; j < Deposit.length; j++) {
            this.Deposit[j] = (Account) Deposit[j];
        }
    }
}
