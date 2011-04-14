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
package org.richfaces.examples.tweetstream.dataserver.listener;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.Event;
import org.richfaces.examples.tweetstream.dataserver.jms.PublishController;
import org.richfaces.examples.tweetstream.domain.Tweet;

import javax.inject.Inject;
import java.util.List;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 */

@Listener
public class ViewBuilderListener {

  @Inject
  org.slf4j.Logger log;




   //@CacheEntryVisited
   @CacheEntryModified
   @CacheEntryCreated
   public void handle(Event e) {
          //TODO - need to do some checking here or we will get duplicates
          //actually, we need a better cache update strategy globally
          List<Tweet> tweets = (List)e.getCache().get("simpletweets");
          PublishController viewPushBean = new PublishController();
          viewPushBean.publishView(tweets);

          System.out.println("-----e.getType()----" + e.getType());
          if(tweets != null)
          System.out.println("-----tweets.size()----" + tweets.size());
   }

        //TODO determine if the event is for new data

        //TODO pull, or query for the TwitterAggregate data
        // This can be Sanne's service, or something else

        //TODO publish this updated data model to the RichFaces push component's JMS topic
        //UI's will then receive this data, and update their views with it.

        //At this point it is pretty much just repeat either as a poll, or event driven process.


}
