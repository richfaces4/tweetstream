package org.richfaces.examples.tweetstream.dataserver.source;

import org.richfaces.examples.tweetstream.domain.Hashtag;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.Tweeter;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing the source of the twitter data.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public interface TwitterSource extends Serializable {

    public void fetchContent();

    public String getSearchTerm();

    public List<Tweet> getTweets();

    public List<Tweeter> getTopTweeters();

    public List<Hashtag> getTopHashtags();

    public TwitterAggregate getTwitterAggregate();
}
