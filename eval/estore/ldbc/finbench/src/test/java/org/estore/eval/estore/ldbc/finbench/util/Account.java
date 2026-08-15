package org.estore.eval.estore.ldbc.finbench.util;

import java.io.Serializable;

public class Account implements Serializable {
  private long accountId;
  private String createTime;
  private boolean isBlocked;
  private String accountType;
  private String nickname;
  private String phonenum;
  private String email;
  private String freqLoginType;
  private long lastLoginTime;
  private String accountLevel;
  private Loan[] Repay;
  private Account[] Transfer;
  private Account[] Withdraw;

  public Account(
      long accountId,
      String createTime,
      boolean isBlocked,
      String accountType,
      String nickname,
      String phonenum,
      String email,
      String freqLoginType,
      long lastLoginTime,
      String accountLevel) {
    this.accountId = accountId;
    this.createTime = createTime;
    this.isBlocked = isBlocked;
    this.accountType = accountType;
    this.nickname = nickname;
    this.phonenum = phonenum;
    this.email = email;
    this.freqLoginType = freqLoginType;
    this.lastLoginTime = lastLoginTime;
    this.accountLevel = accountLevel;
  }

  public Account() {}

  public void setTransfer(Object[] Transfer) {
    this.Transfer = new Account[Transfer.length];
    for (int j = 0; j < Transfer.length; j++) {
      this.Transfer[j] = (Account) Transfer[j];
    }
  }

  public void setWithdraw(Object[] Withdraw) {
    this.Withdraw = new Account[Withdraw.length];
    for (int j = 0; j < Withdraw.length; j++) {
      this.Withdraw[j] = (Account) Withdraw[j];
    }
  }

  public void setRepay(Object[] Repay) {
    this.Repay = new Loan[Repay.length];
    for (int j = 0; j < Repay.length; j++) {
      this.Repay[j] = (Loan) Repay[j];
    }
  }
}
