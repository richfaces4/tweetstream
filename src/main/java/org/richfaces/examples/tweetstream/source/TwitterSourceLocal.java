package org.richfaces.examples.tweetstream.source;

import org.richfaces.examples.tweetstream.cache.CacheBuilder;
import org.richfaces.examples.tweetstream.cache.InfinispanCacheBuilder;
import org.richfaces.examples.tweetstream.listener.TweetListenerBean;
import org.richfaces.examples.tweetstream.listener.ViewBuilderListener;
import org.richfaces.examples.tweetstream.model.Hashtag;
import org.richfaces.examples.tweetstream.model.SimpleTweet;
import org.richfaces.examples.tweetstream.model.Tweeter;
import twitter4j.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
@ApplicationScoped
public class TwitterSourceLocal implements TwitterSource {

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

       List<SimpleTweet> tweetList = new ArrayList<SimpleTweet>();

       try
       {
         //populate historical data
          Twitter twitter = new TwitterFactory().getInstance();
          QueryResult result = twitter.search(new Query(searchTermBase));

           cacheBuilder.getCache().addListener(new ViewBuilderListener());

            for (Tweet tweet : result.getTweets()) {

                SimpleTweet simpleTweet = new SimpleTweet();
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

    public List<Tweet> getTweets(String searchTerm) {

        searchTerm = searchTermBase + " " + searchTerm;
        log.info("Current search term set to : \"" + searchTerm + "\"");

        Twitter twitter = new TwitterFactory().getInstance();

        List<Tweet> tweets = null;
        try {
            QueryResult result = twitter.search(new Query(searchTerm));
            tweets = result.getTweets();
            for (Tweet tweet : tweets) {
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
        for (int i=0; i<50;i++){
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
        for (int i=0; i<50;i++){
            hashtag = new Hashtag();
            hashtag.setHashtag("#richfaces_" + i);
            hashtag.setCount(1000-(5*i));
            hashtags.add(hashtag);
        }

        return hashtags;
    }


}
