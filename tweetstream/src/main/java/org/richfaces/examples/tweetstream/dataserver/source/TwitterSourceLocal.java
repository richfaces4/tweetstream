/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.richfaces.examples.tweetstream.dataserver.source;

import org.jboss.logging.Logger;
import org.richfaces.examples.tweetstream.dataserver.cache.InfinispanCacheBuilder;
import org.richfaces.examples.tweetstream.dataserver.listeners.CacheUpdateListener;
import org.richfaces.examples.tweetstream.dataserver.listeners.TweetStreamListener;
import org.richfaces.examples.tweetstream.domain.HashTag;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.Tweeter;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An implementation of the TwitterSource interface that will generate twitter content locally, not relying on outside
 * sources.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
@TwitterLocal
@ApplicationScoped
public class TwitterSourceLocal implements TwitterSource
{

   @Inject
   Logger log;

   @Inject
   InfinispanCacheBuilder cacheBuilder;

   @Inject
   TweetStreamListener tweetListener;

   Long lastSearch = -1l;


   private TwitterAggregate twitterAggregate;


   public void init()
   {
      //TODO Wrap in what ever try/catch is needed
      fetchContent();

      // add the listener that checks hi new data has been added.
      cacheBuilder.getCache().addListener(new CacheUpdateListener());

      //Populate cache with seed data from this class
      cacheBuilder.getCache().put("tweetaggregate", twitterAggregate);

      //Start the twitter streaming
      tweetListener.startTwitterStream();


      log.info("Initialization of twitter source local complete");
   }

   @Override
   public String getSearchTerm()
   {
      return twitterAggregate.getFilter();
   }

   public List<Tweet> getTweets()
   {
      return twitterAggregate.getTweets();
   }

   public List<Tweeter> getTopTweeters()
   {
      return twitterAggregate.getTopTweeters();
   }

   public List<HashTag> getTopHashtags()
   {
      return twitterAggregate.getTopHashTags();
   }

   @Override
   public TwitterAggregate getTwitterAggregate()
   {
      return twitterAggregate;
   }

   @Override
   public void fetchContent()
   {
      //TODO Check if updating content is needed
      //If not skip - can be called on every page load
      if (performSearch())
      {


         twitterAggregate = new TwitterAggregate();

         //Load the base search term from context param
         String searchTerm = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("org.richfaces.examples.tweetstream.searchTermBase");

         if (searchTerm == null)
         {
            searchTerm = "";
            log.warn("Default initial twitter filter term not found in context params");
         }

         twitterAggregate.setFilter(searchTerm);

         //Load the twitter search
         List<Tweet> tweets = new ArrayList<Tweet>();
         List<Tweeter> tweeters = new ArrayList<Tweeter>();
         Tweeter tweeter = null;

         Twitter twitter = new TwitterFactory().getInstance();
         List<twitter4j.Tweet> t4jTweets = null;
         try
         {
            QueryResult result = twitter.search(new Query(searchTerm));
            t4jTweets = result.getTweets();
            for (twitter4j.Tweet t4jTweet : t4jTweets)
            {
               log.info("@" + t4jTweet.getFromUser() + " - " + t4jTweet.getText());
               //Create a local tweet object from the t4j
               Tweet tweet = new Tweet();
               tweet.setText(t4jTweet.getText());
               tweet.setId(t4jTweet.getFromUserId());
               tweet.setProfileImageUrl(t4jTweet.getProfileImageUrl().toString());
               tweet.setScreenName(t4jTweet.getFromUser());
               //TODO fill in any other required data

               //quick krap code to calculate top tweeters
               for (Tweet atweet : tweets){
                  //if we already have the user in our tweets list...
                  if(atweet.getScreenName().equals(tweet.getScreenName())){
                     //loop through the tweeters to get compare ids so we can increment by 1
                     for(Tweeter atweeter : tweeters){
                        if(atweeter.getUserId() == atweet.getId()){
                           //increment tweet count
                           atweeter.setTweetCount(atweeter.getTweetCount() + 1);
                        }
                     }
                  }else{
                     tweeter = new Tweeter();
                     tweeter.setProfileImgUrl(t4jTweet.getProfileImageUrl().toString());
                     tweeter.setTweetCount(1);
                     tweeter.setUser(t4jTweet.getFromUser());
                     tweeter.setUserId(t4jTweet.getFromUserId());
                  }

               }

               tweeters.add(tweeter);

               tweets.add(tweet);
            }
         }
         catch (TwitterException te)
         {
            te.printStackTrace();
            log.info("Failed to search tweets: " + te.getMessage());
         }

         twitterAggregate.setTweets(tweets);


         twitterAggregate.setTopTweeters(tweeters);

         //Load TopTags
         List<HashTag> hashTags = new ArrayList<HashTag>();

         HashTag hashTag = null;
         for (int i = 0; i < 10; i++)
         {
            hashTag = new HashTag();
            hashTag.setHashtag("#richfaces_" + i);
            hashTag.setCount(1000 - (5 * i));
            hashTags.add(hashTag);
         }

         twitterAggregate.setTopHashTags(hashTags);
      }

   }

   @Override
   public void refreshList()
   {
      TwitterAggregate tweetAggregate = (TwitterAggregate)cacheBuilder.getCache().get("tweetaggregate");
      twitterAggregate.setTweets(tweetAggregate.getTweets());
   }

   private boolean performSearch()
   {
      if (lastSearch > 0)
      {
         long current = new Date().getTime();
         if (current - lastSearch > 5000)
         {
            log.debug("****** Enough time past - fetching new data--" + current + "-" + lastSearch + "=" + (current - lastSearch));
            lastSearch = current;
            return true;
         }
         else
         {
            log.debug("****** NOT enough time past - NOT fetching new data--" + current + "-" + lastSearch + "=" + (current - lastSearch));
            return false;
         }
      }
      else
      {
         lastSearch = new Date().getTime();
         log.debug("****** First time through - fetching new data");
         return true;
      }
   }


}
