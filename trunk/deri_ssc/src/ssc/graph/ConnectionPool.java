package ssc.graph;

import java.sql.*;
import java.util.*;

import virtuoso.jena.driver.VirtGraph;

/** A class for preallocating, recycling, and managing
 *  JDBC connections.
 *  <P>
 *  Taken from Core Servlets and JavaServer Pages
 *  from Prentice Hall and Sun Microsystems Press,
 *  http://www.coreservlets.com/.
 *  &copy; 2000 Marty Hall; may be freely used or adapted.
 */

public class ConnectionPool implements Runnable {
  private String  url, username, password;
  private int maxConnections;
  private boolean waitIfBusy;
  private Vector availableConnections, busyConnections;
  private boolean connectionPending = false;

  public ConnectionPool(String url,
                        String username, String password,
                        int initialConnections,
                        int maxConnections,
                        boolean waitIfBusy)
      throws SQLException {
    this.url = url;
    this.username = username;
    this.password = password;
    this.maxConnections = maxConnections;
    this.waitIfBusy = waitIfBusy;
    if (initialConnections > maxConnections) {
      initialConnections = maxConnections;
    }
    availableConnections = new Vector(initialConnections);
    busyConnections = new Vector();
    for(int i=0; i<initialConnections; i++) {
      availableConnections.addElement(makeNewConnection());
    }
  }
  
  public synchronized VirtGraph getConnection()
      throws SQLException {
    if (!availableConnections.isEmpty()) {
    	VirtGraph existingConnection =
        (VirtGraph)availableConnections.lastElement();
      int lastIndex = availableConnections.size() - 1;
      availableConnections.removeElementAt(lastIndex);
      // If connection on available list is closed (e.g.,
      // it timed out), then remove it from available list
      // and repeat the process of obtaining a connection.
      // Also wake up threads that were waiting for a
      // connection because maxConnection limit was reached.
      if (existingConnection.isClosed()) {
        notifyAll(); // Freed up a spot for anybody waiting
        return(getConnection());
      } else {
        busyConnections.addElement(existingConnection);
        return(existingConnection);
      }
    } else {
      

    if ((totalConnections() < maxConnections) &&
          !connectionPending) {
        makeBackgroundConnection();
      } else if (!waitIfBusy) {
        throw new SQLException("Connection limit reached");
      }
      // Wait for either a new connection to be established
      // (if you called makeBackgroundConnection) or for
      // an existing connection to be freed up.
      try {
        wait();
      } catch(InterruptedException ie) {}
      // Someone freed up a connection, so try again.
      return(getConnection());
    }
  }


  private void makeBackgroundConnection() {
    connectionPending = true;
    try {
      Thread connectThread = new Thread(this);
      connectThread.start();
    } catch(OutOfMemoryError oome) {
      // Give up on new connection
    }
  }

  @Override
public void run() {
    try {
      VirtGraph connection = makeNewConnection();
      synchronized(this) {
        availableConnections.addElement(connection);
        connectionPending = false;
        notifyAll();
      }
    } catch(Exception e) { // SQLException or OutOfMemory
      // Give up on new connection and wait for existing one
      // to free up.
    }
  }

  
  private VirtGraph makeNewConnection()
      throws SQLException {
    try {
      VirtGraph graph = new VirtGraph(url, username, password);
      graph.setReadFromAllGraphs(true);
      return(graph);
    } catch(Exception cnfe) {
      // Simplify try/catch blocks of people using this by
      // throwing only one exception type.
    	cnfe.printStackTrace();
      throw new SQLException("Can't connect to database");
    }
  }

  public synchronized void free(VirtGraph connection) {
	if (connection==null) return;
    busyConnections.removeElement(connection);
    availableConnections.addElement(connection);
    // Wake up threads that are waiting for a connection
    notifyAll(); 
  }
  
    
  public synchronized int totalConnections() {
	    return(availableConnections.size() +
	           busyConnections.size());
	  }

	  /** Close all the connections. Use with caution:
	   *  be sure no connections are in use before
	   *  calling. Note that you are not <I>required</I> to
	   *  call this when done with a ConnectionPool, since
	   *  connections are guaranteed to be closed when
	   *  garbage collected. But this method gives more control
	   *  regarding when the connections are closed.
	   */

	  public synchronized void closeAllConnections() {
	    closeConnections(availableConnections);
	    availableConnections = new Vector();
	    closeConnections(busyConnections);
	    busyConnections = new Vector();
	  }

	  private void closeConnections(Vector connections) {
	    try {
	      for(int i=0; i<connections.size(); i++) {
	    	  VirtGraph connection =
	          (VirtGraph)connections.elementAt(i);
	        if (!connection.isClosed()) {
	          connection.close();
	        }
	      }
	    } catch(Exception sqle) {
	      // Ignore errors; garbage collect anyhow
	    }
	  }
	  
	  @Override
	public synchronized String toString() {
	    String info =
	      "ConnectionPool(" + url + "," + username + ")" +
	      ", available=" + availableConnections.size() +
	      ", busy=" + busyConnections.size() +
	      ", max=" + maxConnections;
	    return(info);
	  }
}
