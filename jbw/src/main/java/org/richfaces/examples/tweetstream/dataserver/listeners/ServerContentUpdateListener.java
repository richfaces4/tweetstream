package org.richfaces.examples.tweetstream.dataserver.listeners;

import org.jboss.jbw2011.keynote.demo.model.Tweet;
import org.jboss.jbw2011.keynote.demo.model.TweetAggregate;
import org.jboss.logging.Logger;
import org.jboss.seam.solder.core.Requires;

import org.richfaces.examples.tweetstream.dataserver.service.TweetStreamPersistenceService;
import org.richfaces.examples.tweetstream.dataserver.util.TweetAggregateConverter;
import org.richfaces.examples.tweetstream.domain.TwitterAggregate;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.ScheduleExpression;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import java.util.List;
import javax.jms.*;
import javax.naming.InitialContext;

/**
 * Listens/polls for data updates from the server's persistenceServiceBean. When an update is needed it retrieves the
 * updated content and publishes to the JMS topic using the PublishController.
 * <p/>
 * The current impl polls, for updates, but will be updated to listen for update events at some point.
 * <p/>
 * //TODO change name of class to ServerContentListenerBean
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
@Singleton
@Local(ServerContentListener.class)
@Requires("org.jboss.jbw2011.keynote.demo.model.TweetAggregate")
public class ServerContentUpdateListener implements ServerContentListener {
  private static final String EVERY = "*";

  @Resource
  private SessionContext context;

  @Inject
  Logger log;

  @Inject
  private TweetStreamPersistenceService persistenceService;

  private Tweet lastTweet = null;

  @Override
  public void startServerListener() {

    log.info("Creating a  timer to poll for updated content");

    // Create a schedule expression to fire every 5 seconds
    ScheduleExpression scheduleExpression = new ScheduleExpression();
    scheduleExpression.year(EVERY).month(EVERY).dayOfMonth(EVERY).hour(EVERY).minute(EVERY).second("*/5");

    //Configure a timer so it is not persisted in the container
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);

    //Start the timer
    final TimerService timerService = context.getTimerService();
    final Timer timer = timerService.createCalendarTimer(scheduleExpression, timerConfig);

    log.info("Created " + timer + " to poll for updated content; next fire is at: " + timer.getNextTimeout() + " - Timer is persistent : " + timer.isPersistent());

  }

  @Timeout
  private void pollServer() {

    log.debug("ServerContentListener polling triggered");

    //check if updates have been made
    if (updatedContentAvailable()) {

      //Fetch the updates
      TweetAggregate svrAggregate = persistenceService.getAggregate();

      //Convert to local domain model
      TwitterAggregate twitterAggregate = TweetAggregateConverter.convertTwitterAggregate(svrAggregate);

      //Send push controller updated content to publish
      publishMessage(twitterAggregate);

    }

    log.debug("ServerContentListener polling completed");
  }

  /**
   * TODO move out to a common location
   * @param twitterAggregate
   */
  private void publishMessage(TwitterAggregate twitterAggregate) {
    Connection topicConnection = null;
    Session session = null;
    TextMessage message = null;
    MessageProducer producer = null;

    try {
      //Standard JMS connection setup
      InitialContext ic = new InitialContext();
      ConnectionFactory cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
      topicConnection = cf.createConnection("guest", "guest");
      Topic topic = (Topic) ic.lookup("/topic/twitter");

      //start the topicConnection
      topicConnection.start();

      //Create the session and msgProducer
      session = topicConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      producer = session.createProducer(topic);

      message = session.createTextMessage();
      message.setText("content");

      //set the subtopic for the message to "aggregate"
      message.setStringProperty("rf_push_subtopic", "content");

      producer.send(message);

      log.debug("Tweetstream content update push message sent");
    } catch (Exception e) {
      log.error("Exception attempting to send a message to tweetstream client - " + e.getLocalizedMessage());
    } finally {
      try {
        //Must close the session and topicConnection
        if (session != null) session.close();
        if (topicConnection != null) topicConnection.close();
      } catch (JMSException ex) {
        log.error("Exception closing stream JMS - " + ex);
      }
    }

  }

  private boolean updatedContentAvailable() {
    //Get the latest tweet from the service
    if (persistenceService != null) {
      List<Tweet> curTweetList = persistenceService.allTweetsSortedByTime(1);
      Tweet currentTweet = curTweetList.isEmpty() ? null : curTweetList.get(0);

      if ((lastTweet == null) || (currentTweet == null)) {
        log.debug("First check for new server content, or content still empty from server");
        lastTweet = currentTweet;
        return true;
      } else if (lastTweet.getTimestamp() != currentTweet.getTimestamp()) {
        log.debug("Server content has been updated, content update required");
        lastTweet = currentTweet;
        return true;
      } else {
        log.debug("Server content has not been updated, no content update required");
        return false;
      }
    }
    return false;
  }

}
