package org.richfaces.examples.tweetstream.dataserver.listeners;

import org.jboss.jbw2011.keynote.demo.model.Tweet;
import org.jboss.jbw2011.keynote.demo.model.TweetAggregate;
import org.jboss.logging.Logger;
import org.richfaces.examples.tweetstream.dataserver.jms.PublishController;
import org.richfaces.examples.tweetstream.dataserver.service.TweetStreamPersistenceService ;
import org.richfaces.examples.tweetstream.dataserver.util.TweetAggregateConverter;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;

import javax.inject.Inject;
import java.util.List;

/**
 * Listens/polls for data updates from the server's persistenceServiceBean.
 * When an update is needed it retrieves the updated content and publishes
 * to the JMS topic using the PublishController.
 * <p/>
 * The current impl polls, for updates, but will be updated to
 * listen for update events at some point.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public class ServerContentUpdateListener {
  private static final long INTERVAL = 1000l;

  @Inject
  Logger log;

  @Inject
  private TweetStreamPersistenceService persistenceService;

  private Tweet lastTweet = null;

  public void startServerListener() {
    //TODO setup timed service

    //TODO check if updates have been made
    if (updateContentAvailable()) {

      //Fetch the updates
      TweetAggregate svrAggregate = persistenceService.getAggregate();

      //Convert to local domain model
      TwitterAggregate twitterAggregate = TweetAggregateConverter.convertTwitterAggregate(svrAggregate);

      //TODO look into getting pubcontroller injected so we don't need to lookup RF push service each time
      //Send push controller updated content to publish
      PublishController pubControl = new PublishController();
      pubControl.publishView(twitterAggregate);

    }

  }

  private boolean updateContentAvailable() {
    //Get the latest tweet from the service
    List<Tweet> curTweetList = persistenceService.allTweetsSortedByTime(1);
    Tweet currentTweet = curTweetList.isEmpty() ? null : curTweetList.get(0);

    if ((lastTweet == null) || (currentTweet == null)) {
      log.info("First check for new server content, or content still empty from server");
      lastTweet = currentTweet;
      return true;
    } else if (lastTweet.getTimestamp() != currentTweet.getTimestamp()) {
      log.info("Server content has been updated, content update required");
      lastTweet = currentTweet;
      return true;
    } else {
      log.info("Server content has not been updated, no content update required");
      return false;
    }
  }

}
