/**
 *
 * @author  steve smith
 * @version
 */

package ssmith.net;

import java.net.*;
import java.io.*;

/**
 * Extend this class for the clients.
 */
public class NetworkMultiServerConn2 {

  public Socket sck;
  protected DataOutputStream bos;
  protected DataInputStream bis;
  protected NetworkMultiServer2 server;
  public long started_time;

  public NetworkMultiServerConn2(NetworkMultiServer2 svr, Socket sck) throws IOException {
    this.sck = sck;
    this.server = svr;
    bis = new DataInputStream(sck.getInputStream());
    bos = new DataOutputStream(sck.getOutputStream());
    this.started_time = System.currentTimeMillis();
  }

  public void close() {
      try {
          bis.close();
      } catch (IOException e) {
          // Nothing
      }
      try {
          bos.flush();
      } catch (IOException e) {
          // Nothing
      }
      try {
          bos.close();
      } catch (IOException e) {
          // Nothing
      }
      try {
          sck.close();
      } catch (IOException e) {
          // Nothing
      }
      server.removeConnection(this);
  }

  public DataInputStream getInput() {
    return bis;
  }

  public DataOutputStream getOutput() {
    return bos;
  }

  public InetAddress getINetAddress() {
    return sck.getInetAddress();
  }

}
