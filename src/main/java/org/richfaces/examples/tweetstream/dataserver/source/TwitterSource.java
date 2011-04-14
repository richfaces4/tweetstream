package org.richfaces.examples.tweetstream.dataserver.source;

import org.richfaces.examples.tweetstream.domain.Hashtag;
import org.richfaces.examples.tweetstream.domain.Tweeter;
import twitter4j.Tweet;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing the source of the twitter data.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public interface TwitterSource extends Serializable {
    List<Tweet> getTweets(String searchTerm);

    List<Tweeter> getTopTweeters(String searchTerm);

    List<Hashtag> getTopHashtags(String searchTerm);
}
