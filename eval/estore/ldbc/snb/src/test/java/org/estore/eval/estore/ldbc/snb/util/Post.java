package org.estore.eval.estore.ldbc.snb.util;

import java.io.Serializable;

public class Post implements Serializable {
  private long id;
  private long creationDate;
  private String locationIP;
  private String browserUsed;
  private String language;
  private String content;
  private int length;
  private Person[] HAS_CREATOR;
  private Tag[] HAS_TAG;
  private Place[] IS_LOCATED_IN;

  public Post(
      long id,
      long creationDate,
      String locationIP,
      String browserUsed,
      String language,
      String content,
      int length) {
    this.id = id;
    this.creationDate = creationDate;
    this.locationIP = locationIP;
    this.browserUsed = browserUsed;
    this.language = language;
    this.content = content;
    this.length = length;
  }

  public void setHasCreator(Object[] HAS_CREATOR) {
    this.HAS_CREATOR = new Person[HAS_CREATOR.length];
    for (int j = 0; j < HAS_CREATOR.length; j++) {
      this.HAS_CREATOR[j] = (Person) HAS_CREATOR[j];
    }
  }

  public void setHasTag(Object[] HAS_TAG) {
    this.HAS_TAG = new Tag[HAS_TAG.length];
    for (int j = 0; j < HAS_TAG.length; j++) {
      this.HAS_TAG[j] = (Tag) HAS_TAG[j];
    }
  }

  public void setIsLocatedIn(Object[] IS_LOCATED_IN) {
    this.IS_LOCATED_IN = new Place[IS_LOCATED_IN.length];
    for (int j = 0; j < IS_LOCATED_IN.length; j++) {
      this.IS_LOCATED_IN[j] = (Place) IS_LOCATED_IN[j];
    }
  }
}
