package org.estore.eval.estore.ldbc.snb.util;

import java.io.Serializable;

public class Tag implements Serializable {
  private long id;
  private String name;
  private String url;
  private TagClass[] HAS_TYPE;

  public Tag(long id, String name, String url) {
    this.id = id;
    this.name = name;
    this.url = url;
  }

  public void setHasType(Object[] HAS_TYPE) {
    this.HAS_TYPE = new TagClass[HAS_TYPE.length];
    for (int j = 0; j < HAS_TYPE.length; j++) {
      this.HAS_TYPE[j] = (TagClass) HAS_TYPE[j];
    }
  }
}
