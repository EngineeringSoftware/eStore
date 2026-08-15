package org.estore.eval.estore.ldbc.snb.util;

import java.io.Serializable;

public class Forum implements Serializable {
  private long id;
  private String title;
  private long creationDate;
  private Post[] CONTAINER_OF;
  private Person[] HAS_MEMBER;
  private Person[] HAS_MODERATOR;
  private Tag[] HAS_TAG;

  public Forum(long id, String title, long creationDate) {
    this.id = id;
    this.title = title;
    this.creationDate = creationDate;
  }

  public void setHasMember(Object[] HAS_MEMBER) {
    this.HAS_MEMBER = new Person[HAS_MEMBER.length];
    for (int j = 0; j < HAS_MEMBER.length; j++) {
      this.HAS_MEMBER[j] = (Person) HAS_MEMBER[j];
    }
  }

  public void setHasModerator(Object[] HAS_MODERATOR) {
    this.HAS_MODERATOR = new Person[HAS_MODERATOR.length];
    for (int j = 0; j < HAS_MODERATOR.length; j++) {
      this.HAS_MODERATOR[j] = (Person) HAS_MODERATOR[j];
    }
  }

  public void setHasTag(Object[] HAS_TAG) {
    this.HAS_TAG = new Tag[HAS_TAG.length];
    for (int j = 0; j < HAS_TAG.length; j++) {
      this.HAS_TAG[j] = (Tag) HAS_TAG[j];
    }
  }

  public void setContainerOf(Object[] CONTAINER_OF) {
    this.CONTAINER_OF = new Post[CONTAINER_OF.length];
    for (int j = 0; j < CONTAINER_OF.length; j++) {
      this.CONTAINER_OF[j] = (Post) CONTAINER_OF[j];
    }
  }
}
