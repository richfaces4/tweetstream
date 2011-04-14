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
package org.richfaces.examples.tweetstream.ui.view;

import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.Tweeter;

import java.util.List;

/** @author <a href="mailto:whales@redhat.com">Wesley Hales</a> */
public class Region
{

   public static enum RegionType {TOP_TWEETERS, TWEETS, TWEET_DETAIL}

   private String regionType;

   private List<Tweeter> tweeters;

   private twitter4j.Tweet tweet;

   private List<Tweet> tweets;

   public String getRegionType()
   {
      return regionType;
   }

   public void setRegionType(String regionType)
   {
      this.regionType = regionType;
   }

   public List<Tweeter> getTweeters()
   {
      return tweeters;
   }

   public void setTweeters(List<Tweeter> tweeters)
   {
      this.tweeters = tweeters;
   }

   public twitter4j.Tweet getTweet()
   {
      return tweet;
   }

   public void setTweet(twitter4j.Tweet tweet)
   {
      this.tweet = tweet;
   }

   public List<Tweet> getTweets()
   {
      return tweets;
   }

   public void setTweets(List<Tweet> tweets)
   {
      this.tweets = tweets;
   }
}
