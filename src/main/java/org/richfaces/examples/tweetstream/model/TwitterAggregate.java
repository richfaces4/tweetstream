package org.richfaces.examples.tweetstream.model;

import twitter4j.Tweet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the aggregated data require to power the tweetstream
 * application.  This data once assembled after an update event
 * will be pushed to all of the clients which will intern update
 * their view.
 *
 * All of these values from the tweetstream application is read only.
 * We will not be making any changes, simply pushing the the client
 * to show the current status
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public class TwitterAggregate implements Serializable{

    /**
     * The current twitter stream filter, i.e. "#jboss" or "#jbwvote" etc...
     * From the tweetstream app this i
     */
    private String filter;

    /**
     * The top N ( likely 25 ) tweets in date order.
     */
    private List<SimpleTweet> tweets;

    /**
     * The top 10 most active tweeters
     */
    private List<Tweeter> topTweeters;

    /**
     * The top 10 most used sub-tags.
     */
    private List<Hashtag> topHashtags;


    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

   public List<SimpleTweet> getTweets()
   {
      return tweets;
   }

   public void setTweets(List<SimpleTweet> tweets)
   {
      this.tweets = tweets;
   }

   public List<Tweeter> getTopTweeters()
   {
      return topTweeters;
   }

   public void setTopTweeters(List<Tweeter> topTweeters)
   {
      this.topTweeters = topTweeters;
   }

   public List<Hashtag> getTopHashtags()
   {
      return topHashtags;
   }

   public void setTopHashtags(List<Hashtag> topHashtags)
   {
      this.topHashtags = topHashtags;
   }
}
