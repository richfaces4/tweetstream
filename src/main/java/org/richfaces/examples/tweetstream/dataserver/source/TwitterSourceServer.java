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

import org.jboss.jbw2011.keynote.demo.model.*;
import org.jboss.jbw2011.keynote.demo.persistence.PersistenceServiceBean;
import org.richfaces.examples.tweetstream.domain.*;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.Tweeter;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.New;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
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

  @Inject
  Logger log;

  @Inject
  private PersistenceServiceBean persistenceServiceBean;

  private TwitterAggregate twitterAggregate;

  private long lastSearch = -1l;

  @PostConstruct
  private void init() {
    //First go fetch update data
    fetchContent();


    //TODO Trigger polling of server, which will push updates.


    log.info("Initialization of twitter source server complete");
  }

  @Override
  public String getSearchTerm() {
    return twitterAggregate.getFilter();
  }

  public List<Tweet> getTweets() {
    return twitterAggregate.getTweets();
  }

  public List<Tweeter> getTopTweeters() {
    return twitterAggregate.getTopTweeters();
  }

  public List<HashTag> getTopHashtags() {
    return twitterAggregate.getTopHashTags();
  }

  @Override
  public TwitterAggregate getTwitterAggregate() {
    return twitterAggregate;
  }

  @Override
  public void fetchContent() {
    //Check if updating data is required
    if (performSearch()){
      transformTwitterAggregate();
    }


  }

  private void transformTwitterAggregate() {
    TweetAggregate serverAggregate = persistenceServiceBean.getAggregate();

    twitterAggregate.setFilter(serverAggregate.getFilter());

    List<org.jboss.jbw2011.keynote.demo.model.Tweet> svrTweets = serverAggregate.getTweets();
    List<Tweet> tweets = new ArrayList<Tweet>();

    for (org.jboss.jbw2011.keynote.demo.model.Tweet svrTweet : svrTweets) {
      Tweet tweet = new Tweet();
      tweet.setText(svrTweet.getMessage());
      tweet.setScreenName(svrTweet.getScreenName());
      tweet.setProfileImageUrl(svrTweet.getProfileImageURL());
      tweet.setId(svrTweet.getUserId());
      tweet.setHashTags(svrTweet.getHashtagsAsArray());
      tweets.add(tweet);
    }

    twitterAggregate.setTweets(tweets);

    List<Hashtag> svrHashtags = serverAggregate.getTopHashtags();
    List<HashTag> hashTags = new ArrayList<HashTag>();

    for (Hashtag svrHashtag : svrHashtags) {
      HashTag tag = new HashTag();
      tag.setHashtag(svrHashtag.getHashtag());
      tag.setCount(svrHashtag.getCount());
      hashTags.add(tag);
    }

    twitterAggregate.setTopHashTags(hashTags);

    List<org.jboss.jbw2011.keynote.demo.model.Tweeter> svrTweeters = serverAggregate.getTopTweeters();
    List<Tweeter> tweeters = new ArrayList<Tweeter>();

    for (org.jboss.jbw2011.keynote.demo.model.Tweeter svrTweeter : svrTweeters) {
      Tweeter tweeter = new Tweeter();
      tweeter.setUser(svrTweeter.getUser());
      tweeter.setUserId(svrTweeter.getUserId());
      tweeter.setProfileImgUrl(svrTweeter.getProfileImgUrl());
      tweeter.setTweetCount(svrTweeter.getTweetCount());
      tweeters.add(tweeter);
    }

    twitterAggregate.setTopTweeters(tweeters);

  }

  private boolean performSearch() {
    if (lastSearch > 0) {
      long current = new Date().getTime();
      if (current - lastSearch > 5000) {
        log.debug("****** Enough time past - fetching new data--" + current + "-" + lastSearch + "=" + (current - lastSearch));
        lastSearch = current;
        return true;
      } else {
        log.debug("****** NOT enough time past - NOT fetching new data--" + current + "-" + lastSearch + "=" + (current - lastSearch));
        return false;
      }
    } else {
      lastSearch = new Date().getTime();
      log.debug("****** First time through - fetching new data");
      return true;
    }
  }


}
