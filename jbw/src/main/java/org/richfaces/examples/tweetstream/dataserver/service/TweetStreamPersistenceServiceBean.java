/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.richfaces.examples.tweetstream.dataserver.service;

import org.jboss.jbw2011.keynote.demo.model.Hashtag;
import org.jboss.jbw2011.keynote.demo.model.ScoredTerm;
import org.jboss.jbw2011.keynote.demo.model.Tweet;
import org.jboss.jbw2011.keynote.demo.model.TweetAggregate;
import org.jboss.jbw2011.keynote.demo.model.Tweeter;
import org.jboss.jbw2011.keynote.demo.persistence.impl.PersistenceServiceImpl;
import org.jboss.logging.Logger;
import org.jboss.seam.solder.core.Requires;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
@Requires("org.jboss.jbw2011.keynote.demo.model.TweetAggregate")
public class TweetStreamPersistenceServiceBean implements TweetStreamPersistenceService
{
   @Inject
   Logger logger;

   @PersistenceUnit
   EntityManagerFactory entityManagerFactory;

   private final PersistenceServiceImpl impl = new PersistenceServiceImpl();

   /** Searches for all tweets from a specific account. This is case-sensitive. */
   public List<Tweet> messagesByName(final String name, final int maxResult)
   {
      return messagesByField("name", name, maxResult);
   }

   /** Searches for all tweets from a specific account. This is case-sensitive. */
   public List<Tweet> messagesByScreenName(final String screenName, final int maxResult)
   {
      return messagesByField("screenName", screenName, maxResult);
   }

   public List<Tweet> messagesMentioning(final String keyword, final int maxResult)
   {
      return messagesByField("message", keyword, maxResult);
   }

   public List<Tweet> messagesByField(final String fieldName, final String keyword, final int maxResult)
   {
      return impl.messagesByField(entityManagerFactory, fieldName, keyword, maxResult);
   }

   /**
    * To search for all tweets, sorted in creation order (assuming the timestamp is correct).
    *
    * @return
    */
   public List<Tweet> allTweetsSortedByTime(final int maxResult)
   {
      return impl.allTweetsSortedByTime(entityManagerFactory, maxResult);
   }

   @Override
   public Set<ScoredTerm> getTopHashTags(final int maxResult)
   {
      try
      {
         return mostFrequentlyUsedTerms("hashtags", 1, maxResult);
      }
      catch (IOException e)
      {
         logger.error("Search for hashtags failed", e);
      }
      return Collections.emptySet();
   }

   @Override
   public Set<ScoredTerm> getTopScreenNames(final int maxResult)
   {
      try
      {
         return mostFrequentlyUsedTerms("screenName", 1, maxResult);
      }
      catch (IOException e)
      {
         logger.error("Search for getTopScreenNames failed", e);
      }
      return Collections.emptySet();
   }

   /**
    * This is the most complex one, and uses ScoredTerm to simplify return value. I guess this is the most practical way
    * to make a tag cloud out of all indexed terms.
    *
    * @param inField          Will return only scoredTerms in the specified field
    * @param minimumFrequency a minimum threshold, can be used to reduce not very significant words (see analyzers and
    *                         stopwords for better results).
    * @throws IOException
    */
   public Set<ScoredTerm> mostFrequentlyUsedTerms(String inField, int minimumFrequency, final int maxResult) throws IOException
   {
      return impl.mostFrequentlyUsedTerms(entityManagerFactory, inField, minimumFrequency, maxResult);
   }

   @Override
   public TweetAggregate getAggregate()
   {
      TweetAggregate aggregate = new TweetAggregate();
      aggregate.setTweets(getTweets());
      aggregate.setTopHashtags(getAggregateTopHashTags());
      aggregate.setTopTweeters(getTopTweeters());
      return aggregate;
   }

   private ArrayList<Tweeter> getTopTweeters()
   {
      ArrayList<Tweeter> topTweeters = new ArrayList<Tweeter>(10);
      int usersAddedCounter = 0;
      for (ScoredTerm scoredTweeter : getTopScreenNames(10))
      {
         List<Tweet> messagesByScreenName = messagesByScreenName(scoredTweeter.term, 1);
         if (messagesByScreenName.size() > 0)
         {
            Tweet aTweetForThisUser = messagesByScreenName.get(0);
            Tweeter tweeter = new Tweeter();
            tweeter.setTweetCount(scoredTweeter.frequency);
            tweeter.setUserId(aTweetForThisUser.getUserId());
            tweeter.setProfileImgUrl(aTweetForThisUser.getProfileImageURL());
            tweeter.setUser(aTweetForThisUser.getScreenName());
            topTweeters.add(tweeter);
            usersAddedCounter++;
            if (usersAddedCounter >= 10)
            {
               break;
            }
         }
      }
      return topTweeters;
   }

   private ArrayList<Hashtag> getAggregateTopHashTags()
   {
      ArrayList<Hashtag> hashTags = new ArrayList<Hashtag>(10);
      Set<ScoredTerm> topHashTags = getTopHashTags(10);
      int tagsAddedCounter = 0;
      for (ScoredTerm hashtag : topHashTags)
      {
         Hashtag tag = new Hashtag();
         tag.setCount(hashtag.frequency);
         tag.setHashtag(hashtag.term);
         hashTags.add(tag);
         tagsAddedCounter++;
         if (tagsAddedCounter == 10)
         {
            break;
         }
      }
      return hashTags;
   }

   private ArrayList<Tweet> getTweets()
   {
      return new ArrayList<Tweet>(allTweetsSortedByTime(25));
   }

}
