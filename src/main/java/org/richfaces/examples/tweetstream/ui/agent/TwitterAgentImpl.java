package org.richfaces.examples.tweetstream.ui.agent;

import org.richfaces.examples.tweetstream.dataserver.source.TwitterSource;
import org.richfaces.examples.tweetstream.domain.Hashtag;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.Tweeter;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Responsible for all client data.  The source injection is
 * key and is the point where we can swap between data sources.
 *
 * This class primarily loads initial content, and provides to
 * the UI.  The source is responsible for kicking off any listeners
 * and/or push updates.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
@Named("twitterAgent")
@SessionScoped
public class TwitterAgentImpl implements TwitterAgent, Serializable {
    private Tweet selectedTweet;

    @Inject
    private TwitterSource source;

    public void updateTweets(){
      source.fetchContent();
    }

    @Override
    public String getSearchTerm() {
        return source.getSearchTerm();
    }

    @Override
    public List<Tweet> getTweets() {
        return source.getTweets();
    }

    @Override
    public List<Tweeter> getTweeters() {
        return source.getTopTweeters();
    }

    @Override
    public List<Hashtag> getHashtags() {
        return source.getTopHashtags();
    }

    public Tweet getSelectedTweet() {
        return selectedTweet;
    }

    public void setSelectedTweet(Tweet selectedTweet) {
        this.selectedTweet = selectedTweet;
    }
}
