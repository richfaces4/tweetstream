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
package org.richfaces.examples.tweetstream.ui;

import org.richfaces.application.push.MessageException;
import org.richfaces.application.push.TopicKey;
import org.richfaces.application.push.TopicsContext;
import org.richfaces.examples.tweetstream.model.view.View;
import twitter4j.FilterQuery;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Date;

/** @author <a href="mailto:whales@redhat.com">Wesley Hales</a> */
public class ViewPushBean
{
   @Inject
   org.slf4j.Logger log;

   private transient TopicsContext topicsContext;

   public void connect()
   {
        //will happen on page load probably
   }

   private TopicsContext getTopicsContext() {
        if (topicsContext == null) {
            topicsContext = TopicsContext.lookup();
        }
        return topicsContext;
   }

   public void publishView(View view){

        try {
           //push the updated view object
            //getTopicsContext().publish(new TopicKey("twitter","tweet_status"),
            //MessageFormat.format("{0,time,medium} {1}: {2}", new Date(), status.getUser().getScreenName(), status.getText()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
   }


}