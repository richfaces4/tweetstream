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

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.logging.Logger;
import org.richfaces.application.push.TopicKey;
import org.richfaces.application.push.TopicsContext;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */

public class PublishController implements Serializable
{
   Logger log = Logger.getLogger(PublishController.class);


   private transient TopicsContext topicsContext;
   private Boolean postContent = null;

   private TopicsContext getTopicsContext()
   {
      if (topicsContext == null)
      {
         topicsContext = TopicsContext.lookup();
      }
      return topicsContext;
   }

   public void publishView(TwitterAggregate twitterAggregate)
   {
      try
      {
         String tweetString = "";

         //Initialize postContent
         initPostContent();

         if ((twitterAggregate != null) && (postContent))
            tweetString = twitterAggregate.toJSON();

         log.debug("Pushing Message : " + tweetString);
         getTopicsContext().publish(new TopicKey("twitter", "content"), tweetString);
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
   }

   private void initPostContent(){

      if (postContent == null){
         String postContentStr = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("tweetstream.postContent");
         postContent = new Boolean(postContentStr);
      }
   }


}
