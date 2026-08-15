package org.estore.eval.estore.ldbc.snb.util;

import java.io.Serializable;

public class Person implements Serializable {
  private long id;
  private String firstName;
  private String lastName;
  private String gender;
  private long birthday;
  private long creationDate;
  private String locationIP;
  private String browserUsed;
  private String language;
  private String email;
  private Place[] IS_LOCATED_IN;
  private Tag[] HAS_INTEREST;
  private Person[] KNOWS;
  private Comment[] LIKES1;
  private Post[] LIKES2;
  private Organisation[] STUDY_AT;
  private Organisation[] WORK_AT;

  public Person(
      long id,
      String firstName,
      String lastName,
      String gender,
      long birthday,
      long creationDate,
      String locationIP,
      String browserUsed,
      String language,
      String email) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
    this.birthday = birthday;
    this.creationDate = creationDate;
    this.locationIP = locationIP;
    this.browserUsed = browserUsed;
    this.language = language;
    this.email = email;
  }

  public void setIsLocatedIn(Object[] IS_LOCATED_IN) {
    this.IS_LOCATED_IN = new Place[IS_LOCATED_IN.length];
    for (int j = 0; j < IS_LOCATED_IN.length; j++) {
      this.IS_LOCATED_IN[j] = (Place) IS_LOCATED_IN[j];
    }
  }

  public void setHasInterest(Object[] HAS_INTEREST) {
    this.HAS_INTEREST = new Tag[HAS_INTEREST.length];
    for (int j = 0; j < HAS_INTEREST.length; j++) {
      this.HAS_INTEREST[j] = (Tag) HAS_INTEREST[j];
    }
  }

  public void setKnows(Object[] KNOWS) {
    this.KNOWS = new Person[KNOWS.length];
    for (int j = 0; j < KNOWS.length; j++) {
      this.KNOWS[j] = (Person) KNOWS[j];
    }
  }

  public void setLikes1(Object[] LIKES1) {
    this.LIKES1 = new Comment[LIKES1.length];
    for (int j = 0; j < LIKES1.length; j++) {
      this.LIKES1[j] = (Comment) LIKES1[j];
    }
  }

  public void setLikes2(Object[] LIKES2) {
    this.LIKES2 = new Post[LIKES2.length];
    for (int j = 0; j < LIKES2.length; j++) {
      this.LIKES2[j] = (Post) LIKES2[j];
    }
  }

  public void setStudyAt(Object[] STUDY_AT) {
    this.STUDY_AT = new Organisation[STUDY_AT.length];
    for (int j = 0; j < STUDY_AT.length; j++) {
      this.STUDY_AT[j] = (Organisation) STUDY_AT[j];
    }
  }

  public void setWorkAt(Object[] WORK_AT) {
    this.WORK_AT = new Organisation[WORK_AT.length];
    for (int j = 0; j < WORK_AT.length; j++) {
      this.WORK_AT[j] = (Organisation) WORK_AT[j];
    }
  }
}
