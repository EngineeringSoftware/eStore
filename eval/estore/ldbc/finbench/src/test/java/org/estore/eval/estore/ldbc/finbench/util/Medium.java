package org.estore.eval.estore.ldbc.finbench.util;

import java.io.Serializable;

public class Medium implements Serializable {
  private long mediumId;
  private String mediumType;
  private boolean isBlocked;
  private String createTime;
  private long lastLoginTime;
  private String riskLevel;
  private Account[] SignIn;

  public Medium(
      long mediumId,
      String mediumType,
      boolean isBlocked,
      String createTime,
      long lastLoginTime,
      String riskLevel) {
    this.mediumId = mediumId;
    this.mediumType = mediumType;
    this.isBlocked = isBlocked;
    this.createTime = createTime;
    this.lastLoginTime = lastLoginTime;
    this.riskLevel = riskLevel;
  }

  public Medium() {}

  public void setSignIn(Object[] SignIn) {
    this.SignIn = new Account[SignIn.length];
    for (int j = 0; j < SignIn.length; j++) {
      this.SignIn[j] = (Account) SignIn[j];
    }
  }
}
