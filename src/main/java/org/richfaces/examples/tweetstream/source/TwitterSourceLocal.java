package org.richfaces.examples.tweetstream.source;

import org.richfaces.examples.tweetstream.model.Hashtag;
import org.richfaces.examples.tweetstream.model.Tweeter;
import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public class TwitterSourceLocal implements TwitterSource {
    @Override
    public List<Tweet> getTweets(String searchTerm) {
        Twitter twitter = new TwitterFactory().getInstance();
        List<Tweet> tweets = null;
        try {
            QueryResult result = twitter.search(new Query(searchTerm).rpp(75));
            tweets = result.getTweets();
            for (Tweet tweet : tweets) {
                System.out.println("@" + tweet.getFromUser() + " - " + tweet.getText());
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
        return tweets;
    }

    @Override
    public List<Tweeter> getTopTweeters(String searchTerm) {
        List<Tweeter> tweeters = new ArrayList<Tweeter>();

        Tweeter tweeter = null;
        for (int i=0; i<50;i++){
            tweeter = new Tweeter();
            tweeter.setProfileImgUrl("http://twitter.com/account/profile_image/tech4j?hreflang=en");
            tweeter.setTweetCount(100-(2*i));
            tweeter.setUser("tech4j_" + i);
            tweeter.setUserId(32423444);
            tweeters.add(tweeter);
        }

        return tweeters;
    }

    @Override
    public List<Hashtag> getTopHashtags(String searchTerm) {
        List<Hashtag> hashtags = new ArrayList<Hashtag>();

        Hashtag hashtag = null;
        for (int i=0; i<50;i++){
            hashtag = new Hashtag();
            hashtag.setHashtag("#richfaces_" + i);
            hashtag.setCount(1000-(5*i));
            hashtags.add(hashtag);
        }

        return hashtags;
    }
}
