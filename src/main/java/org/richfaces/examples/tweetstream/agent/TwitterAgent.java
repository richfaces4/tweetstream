package org.richfaces.examples.tweetstream.agent;

import org.richfaces.examples.tweetstream.model.Hashtag;
import org.richfaces.examples.tweetstream.model.Tweeter;
import twitter4j.Tweet;

import java.util.List;

/**
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public interface TwitterAgent {
    String getSearchTerm();

    void setSearchTerm(String searchTerm);

    Tweet getSelectedTweet();

    void setSelectedTweet(Tweet selectedTweet);

    List<Tweet> getTweets();

    List<Tweeter> getTweeters();

    List<Hashtag> getHashtags();
}
