package org.estore.eval.ldbc.snb.util;

import java.io.Serializable;

public class TagClass implements Serializable {
    private long id;
    private String name;
    private String url;
    private TagClass[] IS_SUBCLASS_OF;

    public TagClass(long id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public void setIsSubclassOf(Object[] IS_SUBCLASS_OF) {
        this.IS_SUBCLASS_OF = new TagClass[IS_SUBCLASS_OF.length];
        for (int j = 0; j < IS_SUBCLASS_OF.length; j++) {
            this.IS_SUBCLASS_OF[j] = (TagClass) IS_SUBCLASS_OF[j];
        }
    }
}
