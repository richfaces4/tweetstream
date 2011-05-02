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
import org.richfaces.examples.tweetstream.domain.HashTag;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.Tweeter;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;
import org.richfaces.examples.tweetstream.util.TwitterAggregateUtil;
import twitter4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
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

   //TODO Update track support to work with context param
   //If this changes must also update TwitterSourceLocal
   private static final String[] TRACK = {"java", "jboss", "richfaces", "jbw", "judcon"};
   private static TwitterStream twitterStream;

   public void startTwitterStream() {
      FilterQuery filterQuery = new FilterQuery();
      filterQuery.track(TRACK);

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

//   private TwitterAggregate updateTwitterAggregate(Tweet tweet, TwitterAggregate tweetAggregate){
//      //add the latest tweet to the aggregate tweet list
//      List<Tweet> tweets = tweetAggregate.getTweets();
//      tweets.add(0,tweet);
//
//      //clip to top 25
//      if (tweets.size() > 25) {
//         tweets = tweets.subList(0, 24);
//         tweetAggregate.setTweets(tweets);
//      }
//
//      //Add or increment the tweeter in the top tweeter list
//      List<Tweeter> tweeters = tweetAggregate.getTopTweeters();
//
//      Tweeter tweeter = new Tweeter();
//      tweeter.setUserId(tweet.getId());
//
//      int tweeteridx = tweeters.indexOf(tweeter);
//
//      if (tweeteridx == -1 ){
//         //tweeter is not in the list yet - so set count to 1 and add
//         tweeter.setUser(tweet.getScreenName());
//         tweeter.setProfileImgUrl(tweet.getProfileImageUrl());
//         tweeter.setTweetCount(1);
//         tweeters.add(tweeter);
//      }else{
//         //tweeter is in list - so get the tweet and increment the count
//         tweeters.get(tweeteridx).setTweetCount(tweeters.get(tweeteridx).getTweetCount() + 1);
//      }
//
//      //sort the list
//      Collections.sort(tweeters);
//
//      //Add or increment any hash tags
//      String[] tags = tweet.getHashTags();
//      List<HashTag> topTags =  tweetAggregate.getTopHashTags();
//
//      for (String tagStr : tags){
//         HashTag tag = new HashTag();
//         tag.setHashtag(tagStr);
//         int tagIdx = topTags.indexOf(tag);
//
//         if (tagIdx == -1 ){
//            //tag not tracked add it
//            tag.setCount(1);
//            topTags.add(tag);
//         }else{
//            //tag existing, increment count
//            topTags.get(tagIdx).setCount(topTags.get(tagIdx).getCount() + 1);
//         }
//      }
//
//      //sort the list
//      Collections.sort(topTags);
//
//      return tweetAggregate;
//
//   }

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
