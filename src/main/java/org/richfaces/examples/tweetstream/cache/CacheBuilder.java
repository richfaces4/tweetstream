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
package org.richfaces.examples.tweetstream.cache;

import javax.inject.Inject;

/** @author <a href="mailto:whales@redhat.com">Wesley Hales</a> */

public class CacheBuilder
{

   @Inject
   org.slf4j.Logger log;

   //TODO - we need to init infinispan instance
   //create a clusterable cache
//   EmbeddedCacheManager manager = new DefaultCacheManager(
//                GlobalConfiguration.getClusteredDefault() );



   public void refresh(){
      //TODO - receive event from TweetListenerBean
   }

   //TODO - get tweet history and store in cache
   //we need to decide if we should use the view object in cache or
   //simple lists
     // Cache<String, View> cache = manager.getCache();
    //cache.put("key", "value");
   //TODO - calculate top tweeters based on tweet history and store in cache


}
