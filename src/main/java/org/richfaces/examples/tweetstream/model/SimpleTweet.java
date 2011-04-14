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
package org.richfaces.examples.tweetstream.model;

import twitter4j.HashtagEntity;

import java.util.List;

/** @author <a href="mailto:whales@redhat.com">Wesley Hales</a> */
public class SimpleTweet
{

   private long id;

   private String text;

   private List hashTags;

   private String profileImageURL;

   private String screenName;

   private boolean retweet;

   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
   }

   public String getText()
   {
      return text;
   }

   public void setText(String text)
   {
      this.text = text;
   }

   public List getHashTags()
   {
      return hashTags;
   }

   public void setHashTags(List hashTags)
   {
      this.hashTags = hashTags;
   }

   public String getProfileImageURL()
   {
      return profileImageURL;
   }

   public void setProfileImageURL(String profileImageURL)
   {
      this.profileImageURL = profileImageURL;
   }

   public String getScreenName()
   {
      return screenName;
   }

   public void setScreenName(String screenName)
   {
      this.screenName = screenName;
   }

   public boolean isRetweet()
   {
      return retweet;
   }

   public void setRetweet(boolean retweet)
   {
      this.retweet = retweet;
   }
}
