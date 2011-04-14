package org.richfaces.examples.tweetstream.dataserver.source;

import org.richfaces.examples.tweetstream.dataserver.cache.InfinispanCacheBuilder;
import org.richfaces.examples.tweetstream.domain.*;
import org.richfaces.examples.tweetstream.dataserver.listener.TweetListenerBean;
import org.richfaces.examples.tweetstream.dataserver.listener.ViewBuilderListener;
import twitter4j.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the twitter source interfaces that will
 * pull the initial content from the containers Cache manager.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
@ApplicationScoped
@Alternative
public class TwitterSourceServer implements TwitterSource {

   @Inject org.slf4j.Logger log;

   @Inject
   InfinispanCacheBuilder cacheBuilder;

   @Inject
   TweetListenerBean tweetListener;


   private String searchTermBase;

    @PostConstruct
    private void init(){
        //Load the base search term
        searchTermBase = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("org.richfaces.examples.tweetstream.searchTermBase");

        if (searchTermBase == null){
            searchTermBase = "";
        }

       List<org.richfaces.examples.tweetstream.domain.Tweet> tweetList = new ArrayList<org.richfaces.examples.tweetstream.domain.Tweet>();

       try
       {
         //populate historical data
          Twitter twitter = new TwitterFactory().getInstance();
          QueryResult result = twitter.search(new Query(searchTermBase));

           cacheBuilder.getCache().addListener(new ViewBuilderListener());

            for (twitter4j.Tweet tweet : result.getTweets()) {

                org.richfaces.examples.tweetstream.domain.Tweet simpleTweet = new org.richfaces.examples.tweetstream.domain.Tweet();
                simpleTweet.setText(tweet.getText());
                simpleTweet.setId(tweet.getId());
                simpleTweet.setProfileImageURL(tweet.getProfileImageUrl());
                simpleTweet.setScreenName(tweet.getFromUser());
                simpleTweet.setRetweet(false);
                tweetList.add(simpleTweet);
            }

          cacheBuilder.getCache().put("simpletweets", tweetList);
          System.out.println("-------cacheBuilder.getCache().--" + cacheBuilder.getCache().containsKey("simpletweets"));
          tweetListener.startTwitterStream();
       }
       catch (TwitterException e)
       {
          e.printStackTrace();
       }

       log.info("Base search term set to : " + searchTermBase);
    }

    public List<twitter4j.Tweet> getTweets(String searchTerm) {

        searchTerm = searchTermBase + " " + searchTerm;
        log.info("Current search term set to : \"" + searchTerm + "\"");

        Twitter twitter = new TwitterFactory().getInstance();

        List<twitter4j.Tweet> tweets = null;
        try {
            QueryResult result = twitter.search(new Query(searchTerm));
            tweets = result.getTweets();
            for (twitter4j.Tweet tweet : tweets) {
                log.info("@" + tweet.getFromUser() + " - " + tweet.getText());
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            log.info("Failed to search tweets: " + te.getMessage());
        }
        return tweets;
    }

    public List<Tweeter> getTopTweeters(String searchTerm) {
        List<Tweeter> tweeters = new ArrayList<Tweeter>();

        Tweeter tweeter = null;
        for (int i=0; i<10;i++){
            tweeter = new Tweeter();
            tweeter.setProfileImgUrl("http://twitter.com/account/profile_image/tech4j?hreflang=en");
            tweeter.setTweetCount(100 - (2 * i));
            tweeter.setUser("tech4j_" + i);
            tweeter.setUserId(32423444);
            tweeters.add(tweeter);
        }

        return tweeters;
    }

    public List<Hashtag> getTopHashtags(String searchTerm) {
        List<Hashtag> hashtags = new ArrayList<Hashtag>();

        Hashtag hashtag = null;
        for (int i=0; i<10;i++){
            hashtag = new Hashtag();
            hashtag.setHashtag("#richfaces_" + i);
            hashtag.setCount(1000-(5*i));
            hashtags.add(hashtag);
        }

        return hashtags;
    }


}
