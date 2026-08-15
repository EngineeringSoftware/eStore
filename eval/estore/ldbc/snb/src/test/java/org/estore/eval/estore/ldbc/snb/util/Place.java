package org.estore.eval.estore.ldbc.snb.util;

import java.io.Serializable;

public class Place implements Serializable {
  private long id;
  private String name;
  private String url;
  private String type;
  private Place[] IS_PART_OF;

  public Place(long id, String name, String url, String type) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.type = type;
  }

  public void setIsPartOf(Object[] IS_PART_OF) {
    this.IS_PART_OF = new Place[IS_PART_OF.length];
    for (int j = 0; j < IS_PART_OF.length; j++) {
      this.IS_PART_OF[j] = (Place) IS_PART_OF[j];
    }
  }
}
