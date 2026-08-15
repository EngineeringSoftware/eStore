package org.estore.eval.ldbc.snb.util;

import java.io.Serializable;

public class Comment implements Serializable {
    private long id;
    private long creationDate;
    private String locationIP;
    private String browserUsed;
    private String content;
    private int length;
    private Person[] HAS_CREATOR;
    private Place[] IS_LOCATED_IN;
    private Comment[] REPLY_OF1;
    private Post[] REPLY_OF2;
    private Tag[] HAS_TAG;

    public Comment(
            long id,
            long creationDate,
            String locationIP,
            String browserUsed,
            String content,
            int length) {
        this.id = id;
        this.creationDate = creationDate;
        this.locationIP = locationIP;
        this.browserUsed = browserUsed;
        this.content = content;
        this.length = length;
    }

    public void setHasCreator(Object[] HAS_CREATOR) {
        this.HAS_CREATOR = new Person[HAS_CREATOR.length];
        for (int j = 0; j < HAS_CREATOR.length; j++) {
            this.HAS_CREATOR[j] = (Person) HAS_CREATOR[j];
        }
    }

    public void setIsLocatedIn(Object[] IS_LOCATED_IN) {
        this.IS_LOCATED_IN = new Place[IS_LOCATED_IN.length];
        for (int j = 0; j < IS_LOCATED_IN.length; j++) {
            this.IS_LOCATED_IN[j] = (Place) IS_LOCATED_IN[j];
        }
    }

    public void setIsReplyOf1(Object[] REPLY_OF1) {
        this.REPLY_OF1 = new Comment[REPLY_OF1.length];
        for (int j = 0; j < REPLY_OF1.length; j++) {
            this.REPLY_OF1[j] = (Comment) REPLY_OF1[j];
        }
    }

    public void setIsReplyOf2(Object[] REPLY_OF2) {
        this.REPLY_OF2 = new Post[REPLY_OF2.length];
        for (int j = 0; j < REPLY_OF2.length; j++) {
            this.REPLY_OF2[j] = (Post) REPLY_OF2[j];
        }
    }

    public void setHasTag(Object[] HAS_TAG) {
        this.HAS_TAG = new Tag[HAS_TAG.length];
        for (int j = 0; j < HAS_TAG.length; j++) {
            this.HAS_TAG[j] = (Tag) HAS_TAG[j];
        }
    }
}
