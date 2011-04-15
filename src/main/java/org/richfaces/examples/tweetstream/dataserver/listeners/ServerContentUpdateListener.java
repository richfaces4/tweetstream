package org.richfaces.examples.tweetstream.dataserver.listeners;

/**
 * Listens/polls for data updates from the server's persistenceServiceBean.
 * When an update is needed it retrieves the updated content and publishes
 * to the JMS topic using the PublishController.
 *
 * The current impl polls, for updates, but will be updated to
 * listen for update events at some point.
 *
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
public class ServerContentUpdateListener {
  //TODO pull out an common interface for these classes

}
