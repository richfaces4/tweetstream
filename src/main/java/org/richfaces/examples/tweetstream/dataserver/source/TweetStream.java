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

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.io.Serializable;
import java.lang.annotation.Annotation;

/** @author <a href="mailto:whales@redhat.com">Wesley Hales</a> */

@ManagedBean
public class TweetStream implements Serializable
{
   @Inject
   Logger log;

   @Inject @Any Instance<TwitterSource> twitterSource;

   class TwitterServerQualifier extends AnnotationLiteral<TwitterServer> implements TwitterServer {}
   class TwitterLocalQualifier extends AnnotationLiteral<TwitterLocal> implements TwitterLocal {}

   boolean initialCheck = true;
   boolean demoexists = false;

   @Produces
   public TwitterSource getTwitterSource(){

      if(initialCheck){
         try {
            Class.forName("org.jboss.jbw2011.keynote.demo.model.TweetAggregate");
            log.info("Running in JBW2011 Demo Mode.");
            demoexists = true;
         } catch (ClassNotFoundException ex) {
            log.info("Running in local JUDCon2011 Demo Mode.");
         }
         initialCheck = false;
      }

      Annotation qualifier = demoexists ?
      new TwitterServerQualifier() : new TwitterLocalQualifier();
      return twitterSource.select(qualifier).get();
   }

   @PostConstruct
   private void init(){
      getTwitterSource().init();
   }


}
