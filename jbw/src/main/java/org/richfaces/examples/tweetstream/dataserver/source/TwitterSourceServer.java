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


//import org.jboss.jbw2011.keynote.demo.model.TweetAggregate;
import org.jboss.jbw2011.keynote.demo.model.TweetAggregate;
import org.jboss.logging.Logger;
import org.jboss.seam.solder.core.Requires;

import org.richfaces.examples.tweetstream.dataserver.listeners.ServerContentListener;
import org.richfaces.examples.tweetstream.dataserver.service.TweetStreamPersistenceService;
import org.richfaces.examples.tweetstream.dataserver.util.TweetAggregateConverter;
import org.richfaces.examples.tweetstream.domain.HashTag;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.Tweeter;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * An implementation of the twitter source interfaces that will pull the initial content from the containers Cache
 * manager.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */
@TwitterServer
@ApplicationScoped
@Requires("org.jboss.jbw2011.keynote.demo.model.TweetAggregate")
public class TwitterSourceServer implements TwitterSource
{

   @Inject
   Logger log;


   @Inject
   TweetStreamPersistenceService persistenceService;

  @Inject
  ServerContentListener serverContentListener;

   private TwitterAggregate twitterAggregate;

   private long lastSearch = -1l;


   public void init()
   {
      log.info("Initialization of twitter source server started");

      //First go fetch update data
      fetchContent();

      //Trigger polling of server, which will push updates to clients
      serverContentListener.startServerListener();

      log.info("Initialization of twitter source server complete");
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
      //Check if updating data is required
      if (performSearch())
      {
         TweetAggregate serverAggregate = persistenceService.getAggregate();
         twitterAggregate = TweetAggregateConverter.convertTwitterAggregate(serverAggregate);
      }

   }

   private boolean performSearch()
   {
      if (lastSearch > 0)
      {
         long current = new Date().getTime();
         if (current - lastSearch > 1000)
         {
            lastSearch = current;
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         lastSearch = new Date().getTime();
         return true;
      }
   }


}
