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
package org.richfaces.examples.tweetstream.dataserver.jms;

import org.richfaces.application.push.TopicKey;
import org.richfaces.application.push.TopicsContext;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */

@Singleton
public class PublishController implements Serializable {
  @Inject
  org.slf4j.Logger log;

  private transient TopicsContext topicsContext;

  private TopicsContext getTopicsContext() {
    if (topicsContext == null) {
      topicsContext = TopicsContext.lookup();
    }
    return topicsContext;
  }

  public void publishView(TwitterAggregate twitterAggregate) {


    TwitterAggregate ta = new TwitterAggregate();
    String tweetString = "";
    //TODO - simple hack just to diaplay data...needs work
    if (ta.getTweets() != null) {
      for (Tweet tweet : ta.getTweets()) {
        tweetString += tweet.getText();
      }
    }

    try {
      //push the updated view object
      //TODO - looks like we need to convert the List of tweets to a javascript array, so it can be properly iterated
      //TODO - and formatted on the client side.
      //JAY Agree - we need to format the content to JSON most likely and update the UI sections

      getTopicsContext().publish(new TopicKey("twitter", "twitter_aggregate"), MessageFormat.format("{0} <br/>", tweetString));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }


}
