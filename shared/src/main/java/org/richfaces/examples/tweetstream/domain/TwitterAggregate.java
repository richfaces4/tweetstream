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
package org.richfaces.examples.tweetstream.domain;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Represents the aggregated data require to power the tweetstream application.  This data once assembled after an
 * update event will be pushed to all of the clients which will intern update their view.
 * <p/>
 * All of these values from the tweetstream application is read only. We will not be making any changes, simply pushing
 * the the client to show the current status
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public class TwitterAggregate implements Serializable {

  /**
   * The current twitter stream filter, i.e. "#jboss" or "#jbwvote" etc... From the tweetstream app this i
   */
  private String filter;

  /**
   * The top N ( likely 25 ) tweets in date order.
   */
  private List<Tweet> tweets;

  /**
   * The top 10 most active tweeters
   */
  private List<Tweeter> topTweeters;

  /**
   * The top 10 most used sub-tags.
   */
  private List<HashTag> topHashTags;


  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public List<Tweet> getTweets() {
    return tweets;
  }

  public void setTweets(List<Tweet> tweets) {
    this.tweets = tweets;
  }

  public List<Tweeter> getTopTweeters() {
    return topTweeters;
  }

  public void setTopTweeters(List<Tweeter> topTweeters) {
    this.topTweeters = topTweeters;
  }

  public List<HashTag> getTopHashTags() {
    return topHashTags;
  }

  public void setTopHashTags(List<HashTag> topHashTags) {
    this.topHashTags = topHashTags;
  }

  public String toJSON() {
    String json = "{\"aggregate\":[";

    json += tweetsToJSON() + ",";
    json += tweeterToJSON()+ ",";
    json += hashTagsToJSON();

    json += "]}";

    return json;
  }

  public String tweetsToJSON() {
    String json = "{\"tweets\":[";

    if (tweets != null) {
      for (int i = 0; i < tweets.size(); i++) {
        Tweet tweet = tweets.get(i);

        json += "{\"id\":" + tweet.getId()
                + ",\"text\":\"" + StringEscapeUtils.escapeHtml(tweet.getText())
                + "\",\"profileImageURL\":\"" + StringEscapeUtils.escapeHtml(tweet.getProfileImageUrl())
                + "\",\"screenName\":\"" + tweet.getScreenName()
                + "\",\"retweet\":" + tweet.isRetweet() + "}";

        if (i + 1 != tweets.size()) {
          json += ",";
        }

      }
    }
    json += "]}";

    return json;
  }

  public String tweeterToJSON() {
    String json = "{\"tweeters\":[";

    if (topTweeters != null) {
      for (int i = 0; i < topTweeters.size(); i++) {
        Tweeter tweeter = topTweeters.get(i);

        json += "{\"user\":" + tweeter.getUser()
                + "\",\"profileImageURL\":\"" + StringEscapeUtils.escapeHtml(tweeter.getProfileImgUrl())
                + ",\"text\":\"" + tweeter.getTweetCount()
                + "}";

        if (i + 1 != topTweeters.size()) {
          json += ",";
        }

      }
    }
    json += "]}";

    return json;
  }

  public String hashTagsToJSON() {
    String json = "{\"tags\":[";

    if (topHashTags != null) {
      for (int i = 0; i < topHashTags.size(); i++) {
        HashTag hashtag = topHashTags.get(i);

        json += "{\"tag\":" + hashtag.getHashtag()
                + ",\"count\":\"" + hashtag.getCount()
                + "}";

        if (i + 1 != topHashTags.size()) {
          json += ",";
        }

      }
    }
    json += "]}";

    return json;
  }
}
