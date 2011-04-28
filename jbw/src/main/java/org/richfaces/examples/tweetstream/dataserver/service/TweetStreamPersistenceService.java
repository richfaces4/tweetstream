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

import org.jboss.jbw2011.keynote.demo.model.ScoredTerm;
import org.jboss.jbw2011.keynote.demo.model.Tweet;
import org.jboss.jbw2011.keynote.demo.model.TweetAggregate;
import org.jboss.seam.solder.core.Requires;

import javax.ejb.Local;
import java.util.List;
import java.util.Set;

@Local
@Requires("org.jboss.jbw2011.keynote.demo.model.TweetAggregate")
public interface TweetStreamPersistenceService
{

   public List<Tweet> messagesMentioning(final String keyword, final int maxResult);

   public List<Tweet> messagesByName(final String name, final int maxResult);

   public List<Tweet> messagesByScreenName(final String screenName, final int maxResult);

   public List<Tweet> allTweetsSortedByTime(final int maxResult);

   public Set<ScoredTerm> getTopHashTags(final int maxResult);

   public Set<ScoredTerm> getTopScreenNames(final int maxResult);

   public TweetAggregate getAggregate();

}
