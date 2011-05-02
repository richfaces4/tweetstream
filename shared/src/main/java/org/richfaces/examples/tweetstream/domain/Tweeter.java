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


import java.io.Serializable;

/** @author jbalunas@redhat.com */
public class Tweeter implements Serializable, Comparable<Tweeter>
{
   private long userId;
   private String user;
   private String profileImgUrl;
   private int tweetCount;

   public long getUserId()
   {
      return userId;
   }

   public void setUserId(long userId)
   {
      this.userId = userId;
   }

   public String getUser()
   {
      return user;
   }

   public void setUser(String user)
   {
      this.user = user;
   }

   public String getProfileImgUrl()
   {
      return profileImgUrl;
   }

   public void setProfileImgUrl(String profileImgUrl)
   {
      this.profileImgUrl = profileImgUrl;
   }

   public int getTweetCount()
   {
      return tweetCount;
   }

   public void setTweetCount(int tweetCount)
   {
      this.tweetCount = tweetCount;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Tweeter)) return false;

      Tweeter tweeter = (Tweeter) o;

      if (userId != tweeter.userId) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return (int) (userId ^ (userId >>> 32));
   }

   @Override
   public int compareTo(Tweeter o) {
      if (this.tweetCount < o.getTweetCount()){
         return +1;
      }else if (this.tweetCount > o.getTweetCount()){
         return -1;
      }else {
         //must be equal
         return 0;
      }
   }
}
