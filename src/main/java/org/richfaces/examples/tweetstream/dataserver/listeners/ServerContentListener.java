package org.richfaces.examples.tweetstream.dataserver.listeners;

import javax.ejb.Local;
import javax.ejb.Schedule;

/**
 * @author <a href="mailto:jbalunas@redhat.com">Jay Balunas</a>
 */

public interface ServerContentListener {

  void startServerListener();
}
