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
public class HashTag implements Serializable, Comparable<HashTag>
{

   private String hashtag;
   private int count;


   public String getHashtag()
   {
      return hashtag;
   }

   public void setHashtag(String hashtag)
   {
      this.hashtag = hashtag;
   }

   public int getCount()
   {
      return count;
   }

   public void setCount(int count)
   {
      this.count = count;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof HashTag)) return false;

      HashTag hashTag = (HashTag) o;

      if (hashtag != null ? !hashtag.equalsIgnoreCase(hashTag.hashtag) : hashTag.hashtag != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return hashtag != null ? hashtag.hashCode() : 0;
   }

   @Override
   public int compareTo(HashTag o) {
      if (this.count < o.getCount()){
         return +1;
      }else if (this.count > o.getCount()){
         return -1;
      }else {
         //must be equal
         return 0;
      }
   }
}
