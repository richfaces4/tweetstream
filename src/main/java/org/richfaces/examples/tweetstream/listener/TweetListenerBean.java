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
package org.richfaces.examples.tweetstream.listener;

import org.richfaces.examples.tweetstream.cache.CacheBuilder;
import org.richfaces.examples.tweetstream.cache.InfinispanCacheBuilder;
import org.richfaces.examples.tweetstream.model.SimpleTweet;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** @author <a href="mailto:whales@redhat.com">Wesley Hales</a> */

@ManagedBean
public class TweetListenerBean implements StatusListener, Serializable
{

   @Inject
   org.slf4j.Logger log;

   @Inject
   CacheBuilder cacheBuilder;

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
      SimpleTweet simpleTweet = new SimpleTweet();
       simpleTweet.setText(status.getText());
       simpleTweet.setId(status.getId());
       simpleTweet.setProfileImageURL(status.getUser().getProfileImageURL().toString());
       simpleTweet.setScreenName(status.getUser().getScreenName());
       if(status.getRetweetedStatus() != null){
          simpleTweet.setRetweet(status.getRetweetedStatus().isRetweet());
       }

       List<SimpleTweet> tweets = new ArrayList<SimpleTweet>();
       //if(cacheBuilder.getCache().containsKey("simpletweets")){
       tweets = (List)cacheBuilder.getCache().get("simpletweets");
       System.out.println("-------tweets.size(): "+ tweets.size());
       //}
       tweets.add(simpleTweet);
       cacheBuilder.getCache().put("simpletweets" , tweets);
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
