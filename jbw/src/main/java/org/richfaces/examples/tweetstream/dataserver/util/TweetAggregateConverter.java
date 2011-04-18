package org.richfaces.examples.tweetstream.dataserver.util;

import org.jboss.jbw2011.keynote.demo.model.Hashtag;
import org.jboss.jbw2011.keynote.demo.model.Tweet;
import org.jboss.jbw2011.keynote.demo.model.TweetAggregate;
import org.jboss.seam.solder.core.Requires;
import org.richfaces.examples.tweetstream.domain.HashTag;
import org.richfaces.examples.tweetstream.domain.Tweeter;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;

import javax.faces.bean.ManagedBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple util class to convert server based data to client based.
 * <p/>
 * At some point this may not be needed, if we can connect on a single domain object.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
@ManagedBean
@Requires("org.jboss.jbw2011.keynote.demo.model.TweetAggregate")
public class TweetAggregateConverter
{

   public static TwitterAggregate convertTwitterAggregate(TweetAggregate serverAggregate)
   {
      TwitterAggregate twAggragate = new TwitterAggregate();

      twAggragate.setFilter(serverAggregate.getFilter());

      //System.out.println("TEMP: TweetAggregate filter : " + twAggragate.getFilter());

      List<Tweet> svrTweets = serverAggregate.getTweets();
      List<org.richfaces.examples.tweetstream.domain.Tweet> tweets = new ArrayList<org.richfaces.examples.tweetstream.domain.Tweet>();

      //System.out.println("TEMP: TweetAggregate contains : " + svrTweets.size() + " Tweets");

      for (org.jboss.jbw2011.keynote.demo.model.Tweet svrTweet : svrTweets)
      {
         org.richfaces.examples.tweetstream.domain.Tweet tweet = new org.richfaces.examples.tweetstream.domain.Tweet();
         tweet.setText(svrTweet.getMessage());
         tweet.setScreenName(svrTweet.getScreenName());
         tweet.setProfileImageUrl(svrTweet.getProfileImageURL());
         tweet.setId(svrTweet.getUserId());
         tweet.setHashTags(svrTweet.getHashtagsAsArray());
         tweets.add(tweet);
      }

      twAggragate.setTweets(tweets);

      List<Hashtag> svrHashtags = serverAggregate.getTopHashtags();
      List<HashTag> hashTags = new ArrayList<HashTag>();

      //System.out.println("TEMP: TweetAggregate contains : " + svrHashtags.size() + " HashTags");

      for (Hashtag svrHashtag : svrHashtags)
      {
         HashTag tag = new HashTag();
         tag.setHashtag(svrHashtag.getHashtag());
         tag.setCount(svrHashtag.getCount());
         hashTags.add(tag);
      }

      twAggragate.setTopHashTags(hashTags);

      List<org.jboss.jbw2011.keynote.demo.model.Tweeter> svrTweeters = serverAggregate.getTopTweeters();
      List<Tweeter> tweeters = new ArrayList<Tweeter>();

      //System.out.println("TEMP: TweetAggregate contains : " + svrTweeters.size() + " Tweeters");

      for (org.jboss.jbw2011.keynote.demo.model.Tweeter svrTweeter : svrTweeters)
      {
         Tweeter tweeter = new Tweeter();
         tweeter.setUser(svrTweeter.getUser());
         tweeter.setUserId(svrTweeter.getUserId());
         tweeter.setProfileImgUrl(svrTweeter.getProfileImgUrl());
         tweeter.setTweetCount(svrTweeter.getTweetCount());
         tweeters.add(tweeter);
      }

      twAggragate.setTopTweeters(tweeters);

      return twAggragate;

   }

}
