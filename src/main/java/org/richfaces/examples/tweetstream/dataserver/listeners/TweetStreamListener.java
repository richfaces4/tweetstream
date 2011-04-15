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

import org.richfaces.examples.tweetstream.dataserver.cache.CacheBuilder;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;
import twitter4j.*;
import org.jboss.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public class TweetStreamListener implements StatusListener, Serializable
{

   @Inject
   Logger log;

   @Inject
   CacheBuilder cacheBuilder;

   //TODO Update track support to work with context param
   private static final String[] TRACK = {"java","jboss","richfaces"};
   private static TwitterStream twitterStream;


   public void startTwitterStream() {
      FilterQuery filterQuery = new FilterQuery();
      filterQuery.track(TRACK);

      twitterStream = new TwitterStreamFactory().getInstance();
      twitterStream.addListener(this);
      twitterStream.filter(filterQuery);

   }


   public void onStatus(Status status)
   {
      System.out.println("-------status: "+ status.getText());
      Tweet tweet = new Tweet();
       tweet.setText(status.getText());
       tweet.setId(status.getId());
       tweet.setProfileImageUrl(status.getUser().getProfileImageURL().toString());
       tweet.setScreenName(status.getUser().getScreenName());
       if(status.getRetweetedStatus() != null){
          tweet.setRetweet(status.getRetweetedStatus().isRetweet());
       }

       TwitterAggregate tweetAggregate = (TwitterAggregate)cacheBuilder.getCache().get("tweetaggregate");

       List<Tweet> tweets = tweetAggregate.getTweets();
       tweets.add(tweet);
       System.out.println("-------tweets.size(): "+ tweets.size());

       //clip to top 50
       if (tweets.size() > 50){
        tweets = tweets.subList(0,49);
       }

       //TODO process top X here

       //put back in the cache
       cacheBuilder.getCache().put("tweetaggregate" , tweetAggregate);
   }

   public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice)
   {
      System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
   }

   public void onTrackLimitationNotice(int numberOfLimitedStatuses)
   {
      System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
   }

   public void onScrubGeo(long userId, long upToStatusId)
   {
      System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
   }

   public void onException(Exception ex)
   {
      ex.printStackTrace();
   }


}
