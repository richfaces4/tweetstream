package org.richfaces.examples.tweetstream.source;

import org.richfaces.examples.tweetstream.model.Hashtag;
import org.richfaces.examples.tweetstream.model.Tweeter;
import twitter4j.Tweet;

import java.util.List;

/**
 * Interface representing the source of the twitter data.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public interface TwitterSource {
    List<Tweet> getTweets(String searchTerm);

    List<Tweeter> getTopTweeters(String searchTerm);

    List<Hashtag> getTopHashtags(String searchTerm);
}
