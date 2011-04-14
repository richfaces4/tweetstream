package org.richfaces.examples.tweetstream.domain;


import java.io.Serializable;

/**
 * @author jbalunas@redhat.com
 */
public class Hashtag implements Serializable {

    private String hashtag;
    private int count;


    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
