package org.estore.eval.estore.ldbc.snb.util;

import java.io.Serializable;

public class Organisation implements Serializable {
  private long id;
  private String type;
  private String name;
  private String url;
  private Place[] IS_LOCATED_IN;

  public Organisation(long id, String type, String name, String url) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.url = url;
  }

  public void setIsLocatedIn(Object[] IS_LOCATED_IN) {
    this.IS_LOCATED_IN = new Place[IS_LOCATED_IN.length];
    for (int j = 0; j < IS_LOCATED_IN.length; j++) {
      this.IS_LOCATED_IN[j] = (Place) IS_LOCATED_IN[j];
    }
  }
}
