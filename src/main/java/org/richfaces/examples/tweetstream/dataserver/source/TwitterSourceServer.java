package org.richfaces.examples.tweetstream.dataserver.source;

import org.richfaces.examples.tweetstream.dataserver.cache.InfinispanCacheBuilder;
import org.richfaces.examples.tweetstream.dataserver.listener.TweetListenerBean;
import org.richfaces.examples.tweetstream.dataserver.listener.ViewBuilderListener;
import org.richfaces.examples.tweetstream.domain.*;
import org.richfaces.examples.tweetstream.domain.Tweet;
import twitter4j.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the twitter source interfaces that will
 * pull the initial content from the containers Cache manager.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
@ApplicationScoped
@Alternative
public class TwitterSourceServer implements TwitterSource {

  @Inject
  org.slf4j.Logger log;

  @Inject
  InfinispanCacheBuilder cacheBuilder;

  @Inject
  TweetListenerBean tweetListener;


  private TwitterAggregate twitterAggregate;

  @PostConstruct
  private void init() {

    //TODO Wrap in what ever try/catch is needed
    fetchContent();

    // add the listener that checks hi new data has been added.
    cacheBuilder.getCache().addListener(new ViewBuilderListener());

    //Populate cache with seed data from this class
    cacheBuilder.getCache().put("tweetaggregate", twitterAggregate);
    System.out.println("-------cacheBuilder.getCache().--" + cacheBuilder.getCache().containsKey("tweetaggregate"));

    //Start the twitter streaming
    tweetListener.startTwitterStream();


    //TODO setup connection/injection point etc... to interact with server content
    //Likely will be injected above.

    //TODO Load the filter/search term from server
    //TODO try/catch as needed
    fetchContent();

    //TODO Trigger polling of server, which will push updates.


    log.info("Initialization of twitter source server complete");
  }

  @Override
  public String getSearchTerm() {
    return twitterAggregate.getFilter();
  }

  public List<Tweet> getTweets() {
    return twitterAggregate.getTweets();
  }

  public List<Tweeter> getTopTweeters() {
    return twitterAggregate.getTopTweeters();
  }

  public List<Hashtag> getTopHashtags() {
    return twitterAggregate.getTopHashtags();
  }

  @Override
  public TwitterAggregate getTwitterAggregate() {
    return twitterAggregate;
  }

  @Override
  public void fetchContent() {
    twitterAggregate = new TwitterAggregate();

    //Load the base search term from context param
    String searchTerm = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("org.richfaces.examples.tweetstream.searchTermBase");

    if (searchTerm == null) {
      searchTerm = "";
      log.warn("Default initial twitter filter term not found in context params");
    }

    twitterAggregate.setFilter(searchTerm);

    //Load the twitter search
    List<Tweet> tweets = new ArrayList<Tweet>();

    Twitter twitter = new TwitterFactory().getInstance();
    List<twitter4j.Tweet> t4jTweets = null;
    try {
      QueryResult result = twitter.search(new Query(searchTerm));
      t4jTweets = result.getTweets();
      for (twitter4j.Tweet t4jTweet : t4jTweets) {
        log.info("@" + t4jTweet.getFromUser() + " - " + t4jTweet.getText());
        //Create a local tweet object from the t4j
        Tweet tweet = new Tweet();
        tweet.setText(t4jTweet.getText());
        tweet.setId(t4jTweet.getFromUserId());
        tweet.setProfileImageUrl(t4jTweet.getProfileImageUrl().toString());
        tweet.setScreenName(t4jTweet.getFromUser());
        //TODO fill in any other required data
        tweets.add(tweet);
      }
    } catch (TwitterException te) {
      te.printStackTrace();
      log.info("Failed to search tweets: " + te.getMessage());
    }

    twitterAggregate.setTweets(tweets);

    //Load TopTweeters
    List<Tweeter> tweeters = new ArrayList<Tweeter>();

    Tweeter tweeter = null;
    for (int i = 0; i < 10; i++) {
      tweeter = new Tweeter();
      tweeter.setProfileImgUrl("http://twitter.com/account/profile_image/tech4j?hreflang=en");
      tweeter.setTweetCount(100 - (2 * i));
      tweeter.setUser("tech4j_" + i);
      tweeter.setUserId(32423444);
      tweeters.add(tweeter);
    }

    twitterAggregate.setTopTweeters(tweeters);

    //Load TopTags
    List<Hashtag> hashtags = new ArrayList<Hashtag>();

    Hashtag hashtag = null;
    for (int i = 0; i < 10; i++) {
      hashtag = new Hashtag();
      hashtag.setHashtag("#richfaces_" + i);
      hashtag.setCount(1000 - (5 * i));
      hashtags.add(hashtag);
    }

    twitterAggregate.setTopHashtags(hashtags);

  }


}
