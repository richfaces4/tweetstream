package org.richfaces.examples.tweetstream.util;

import org.infinispan.marshall.exts.ArrayListExternalizer;
import org.richfaces.examples.tweetstream.domain.HashTag;
import org.richfaces.examples.tweetstream.domain.Tweet;
import org.richfaces.examples.tweetstream.domain.Tweeter;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public class TwitterAggregateUtil {
   private static final Pattern TAG_PATTERN =
   Pattern.compile("(?:^|\\s|[\\p{Punct}&&[^/]])(#[\\p{L}0-9-_]+)");

   public static TwitterAggregate updateTwitterAggregate(Tweet tweet, TwitterAggregate tweetAggregate){
      //add the latest tweet to the aggregate tweet list
      List<Tweet> tweets = tweetAggregate.getTweets();
      if (tweets == null){
         tweets = new ArrayList<Tweet>();
      }

      tweets.add(0,tweet);

      //clip to top 25
      if (tweets.size() > 25) {
         tweets = tweets.subList(0, 24);
         tweetAggregate.setTweets(tweets);
      }

      //Add or increment the tweeter in the top tweeter list
      List<Tweeter> tweeters = tweetAggregate.getTopTweeters();
      if (tweeters == null){
         tweeters = new ArrayList<Tweeter>();
      }

      Tweeter tweeter = new Tweeter();
      tweeter.setUserId(tweet.getId());

      int tweeteridx = tweeters.indexOf(tweeter);

      if (tweeteridx == -1 ){
         //tweeter is not in the list yet - so set count to 1 and add
         tweeter.setUser(tweet.getScreenName());
         tweeter.setProfileImgUrl(tweet.getProfileImageUrl());
         tweeter.setTweetCount(1);
         tweeters.add(tweeter);
      }else{
         //tweeter is in list - so get the tweet and increment the count
         tweeters.get(tweeteridx).setTweetCount(tweeters.get(tweeteridx).getTweetCount() + 1);
      }

      //sort the list
      Collections.sort(tweeters);

      //Add or increment any hash tags
      String[] tags = tweet.getHashTags();
      List<HashTag> topTags =  tweetAggregate.getTopHashTags();
      if (topTags == null){
         topTags = new ArrayList<HashTag>();
      }

      for (String tagStr : tags){
         HashTag tag = new HashTag();
         tag.setHashtag(tagStr);
         int tagIdx = topTags.indexOf(tag);

         if (tagIdx == -1 ){
            //tag not tracked add it
            tag.setCount(1);
            topTags.add(tag);
         }else{
            //tag existing, increment count
            topTags.get(tagIdx).setCount(topTags.get(tagIdx).getCount() + 1);
         }
      }

      //sort the list
      Collections.sort(topTags);

      return tweetAggregate;

   }

   public static String[] pullHashtags(String txt){
      return txt.split(TAG_PATTERN.pattern());
   }

}
