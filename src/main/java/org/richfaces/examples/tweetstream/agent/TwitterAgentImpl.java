package org.richfaces.examples.tweetstream.agent;

import org.richfaces.examples.tweetstream.model.Hashtag;
import org.richfaces.examples.tweetstream.model.Tweeter;
import org.richfaces.examples.tweetstream.source.TwitterSource;
import twitter4j.Tweet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
@Named("twitterAgent")
@RequestScoped
public class TwitterAgentImpl implements TwitterAgent {
    private String searchTerm = "#richfaces";
    private Tweet selectedTweet;
    private  List<Tweet> tweets;
    private List<Tweeter> topTweeters;
    private List<Hashtag> topTHashtags;

    @Inject
    private TwitterSource source;

    @PostConstruct
    public String updateTweets(){
        tweets = source.getTweets(searchTerm);
        topTweeters = source.getTopTweeters(searchTerm);
        topTHashtags = source.getTopHashtags(searchTerm);
        return null;
    }

    @Override
    public String getSearchTerm() {
        return searchTerm;
    }

    @Override
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Override
    public List<Tweet> getTweets() {
        return tweets;
    }

    @Override
    public List<Tweeter> getTweeters() {
        return topTweeters;
    }

    @Override
    public List<Hashtag> getHashtags() {
        return topTHashtags;
    }

    public Tweet getSelectedTweet() {
        return selectedTweet;
    }

    public void setSelectedTweet(Tweet selectedTweet) {
        this.selectedTweet = selectedTweet;
    }
}
