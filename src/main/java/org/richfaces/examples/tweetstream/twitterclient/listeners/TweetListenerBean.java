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
package org.richfaces.examples.tweetstream.twitterclient.listeners;

import org.richfaces.application.push.MessageException;
import org.richfaces.application.push.TopicKey;
import org.richfaces.application.push.TopicsContext;
import org.richfaces.log.LogFactory;
import org.richfaces.log.Logger;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

/** @author <a href="mailto:whales@redhat.com">Wesley Hales</a> */

@ManagedBean
@SessionScoped
public class TweetListenerBean implements StatusListener, Serializable
{
   private transient TopicsContext topicsContext;
   private static final Logger LOGGER = LogFactory.getLogger(TweetListenerBean.class);
   private static final String SUBTOPIC_SEPARATOR = "_";
   private static final String[] TRACK = {"java","jboss","richfaces"};
   private TwitterStream twitterStream;

   {
      twitterStream = new TwitterStreamFactory().getInstance();
      twitterStream.addListener(this);
   }

   public void connect()
   {

      FilterQuery filterQuery = new FilterQuery();
      filterQuery.track(TRACK);
      twitterStream.filter(filterQuery);
   }

   private TopicsContext getTopicsContext() {
        if (topicsContext == null) {
            topicsContext = TopicsContext.lookup();
        }
        return topicsContext;
    }


   public void onStatus(Status status)
   {
      System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());

        try {
            getTopicsContext().publish(new TopicKey("twitter","tweet_status"),
            MessageFormat.format("{0,time,medium} {1}: {2}", new Date(), status.getUser().getScreenName(), status.getText()));
        } catch (MessageException e) {
            LOGGER.error(e.getMessage(), e);
        }
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
