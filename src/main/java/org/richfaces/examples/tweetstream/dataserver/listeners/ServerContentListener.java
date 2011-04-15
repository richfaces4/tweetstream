package org.richfaces.examples.tweetstream.dataserver.listeners;

import javax.ejb.Local;
import javax.ejb.Schedule;

/**
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */
@Local
public interface ServerContentListener {

  void startServerListener();
}
