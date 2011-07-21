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
package org.richfaces.examples.tweetstream.dataserver.cache;

import org.infinispan.Cache;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Serializable;

/** @author <a href="mailto:whales@redhat.com">Wesley Hales</a> */

@Singleton
@Alternative
public class InfinispanCacheBuilder implements CacheBuilder, Serializable
{

   @Inject
   Logger log;

   private static final EmbeddedCacheManager cacheManager;

   static
   {
      cacheManager = new DefaultCacheManager(GlobalConfiguration.getClusteredDefault());
   }

   public InfinispanCacheBuilder()
   {
   }

   //public EmbeddedCacheManager getCacheManager() {
   // System.out.println("--------enter cache manager");
   // Create the configuration, and set to replication
   //GlobalConfiguration gc = GlobalConfiguration.getClusteredDefault();
   //Configuration c = new Configuration();
   //c.setCacheMode(Configuration.CacheMode.REPL_SYNC);
   //c.setCacheMode(Configuration.CacheMode.DIST_SYNC);
   // if(cacheManager == null){
   //   System.out.println("--------null cache manager");
   //   try
   //   {
   //       cacheManager = new DefaultCacheManager(GlobalConfiguration.getClusteredDefault());
   //    }
   //   catch (Exception e)
   //   {
   //      throw new RuntimeException("Unable to configure Infinispan cache manager", e);
   //   }
   //  }
   //    return cacheManager;
   //  }

   /**
    * Retrieves the default cache.
    *
    * @param <K> type used as keys in this cache
    * @param <V> type used as values in this cache
    * @return a cache
    */
   public <K, V> Cache<K, V> getCache()
   {
      return cacheManager.getCache();
   }

   /**
    * Retrieves a named cache.
    *
    * @param cacheName name of cache to retrieve
    * @param <K>       type used as keys in this cache
    * @param <V>       type used as values in this cache
    * @return a cache
    */
   public <K, V> Cache<K, V> getCache(String cacheName)
   {
      if (cacheName == null)
      {
         throw new NullPointerException("Cache name cannot be null!");
      }
      return cacheManager.getCache(cacheName);
   }

   /**
    * Retrieves the embedded cache manager.
    *
    * @return a cache manager
    */
   public EmbeddedCacheManager getCacheContainer()
   {
      return cacheManager;
   }

}
