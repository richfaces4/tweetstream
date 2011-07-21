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
package org.richfaces.examples.tweetstream.dataserver.listeners;

import org.jboss.logging.Logger;
import org.richfaces.examples.tweetstream.dataserver.cache.CacheBuilder;
import org.richfaces.examples.tweetstream.dataserver.cache.SimpleCacheBuilder;
import org.richfaces.examples.tweetstream.dataserver.jms.PublishController;
import org.richfaces.examples.tweetstream.domain.HashTag;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.Tweeter;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;
import org.richfaces.examples.tweetstream.util.TwitterAggregateUtil;
import twitter4j.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public class TweetStreamListener implements StatusListener, Serializable {

   @Inject
   Logger log;

   @Inject
   CacheBuilder cacheBuilder;

   private  String[] tracks;

   PublishController pubControl = new PublishController();

   @PostConstruct
    public void init()
    {
        tracks = readKeys("twittertracks.properties");
    }

   //TODO Update track support to work with context param
   //If this changes must also update TwitterSourceLocal

   private static TwitterStream twitterStream;

   public void startTwitterStream() {
      FilterQuery filterQuery = new FilterQuery();
      filterQuery.track(tracks);

      twitterStream = new TwitterStreamFactory().getInstance();
      twitterStream.addListener(this);
      twitterStream.filter(filterQuery);
   }


   public void onStatus(Status status) {

      //Create a tweet from the status updates.
      Tweet tweet = createTweet(status);

      //Pull the current aggregate data out of of cache
      TwitterAggregate tweetAggregate = (TwitterAggregate) cacheBuilder.getCache().get("tweetaggregate");

      //Update the aggregate
      tweetAggregate = TwitterAggregateUtil.updateTwitterAggregate(tweet, tweetAggregate);

      //put back in the cache
      cacheBuilder.getCache().put("tweetaggregate", tweetAggregate);

      pubControl.publishView(tweetAggregate);
   }

   private Tweet createTweet(Status status) {
      //create a new tweet from the status update
      Tweet tweet = new Tweet();
      tweet.setText(status.getText());
      tweet.setId(status.getUser().getId());
      tweet.setProfileImageUrl(status.getUser().getProfileImageURL().toString());
      tweet.setScreenName(status.getUser().getScreenName());
      if (status.getRetweetedStatus() != null) {
         tweet.setRetweet(status.getRetweetedStatus().isRetweet());
      }

      //process hashtags in the tweet
      HashtagEntity[] hashtagEntities = status.getHashtagEntities();
      if (hashtagEntities != null){
         ArrayList<String> curTags = new ArrayList<String>(hashtagEntities.length);

         for (HashtagEntity tw4jTag: hashtagEntities){
            curTags.add(tw4jTag.getText());
         }

         tweet.setHashTags(curTags.toArray(new String[0]));
      }else {
         tweet.setHashTags(new String[0]);
      }
      return tweet;
   }

   private String[] readKeys(final String name)
    {
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name) ;
        if (is != null)
        {
            final Properties properties = new Properties() ;
            try
            {
                properties.load(is) ;
                return properties.keySet().toArray(new String[0]) ;
            }
            catch (final IOException ioe)
            {
                log.warn("Unexpected exception loading tracks, ignoring", ioe) ;
            }
        }
        return null ;
    }

   public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
      System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
   }

   public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
      System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
   }

   public void onScrubGeo(long userId, long upToStatusId) {
      System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
   }

   public void onException(Exception ex) {
      ex.printStackTrace();
   }


}
