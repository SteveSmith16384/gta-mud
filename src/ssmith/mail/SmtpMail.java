package ssmith.mail;

//package rblasch.Mail;

import java.io.*;
import java.net.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;


/**
 * This class implements the SMTP protocol.
 *
 * <p>This implementation is very limited.  The only way to send is
 * <code>simpleSend</code>; see method description for details.</p>
 *
 * <p>The expected message format is: lines separated by newline
 * <code>'\n'</code> characters.</p>
 *
 * <p>You may turn on the debug flag via <code>debugMode(true)</code>; debug
 * output goes to stderr.</p>
 *
 * @see RFC821, RFC974, RFC822
 * @author Ronald Blaschke &lt;rblasch@cs.bgsu.edu&gt;
 * @version 1.0
 */
public class SmtpMail
{
  /**
   * Create new Object.
   */
  public SmtpMail()
  {
    super();

    rcpts = new Vector();
  }

  /**
   * Sets the debug mode.
   *
   * @param debug true for debug mode, otherwise false (default).
   */
  public void debugMode(boolean debug)
  {
    this.debug = debug;
  }

  /**
   * Outputs the String to stderr if debug is turned on.
   *
   * @param t String to trace.
   */
  protected void trace(String t)
  {
    if (debug) {
      System.err.println("D:" + t);
    }
  }

  /**
   * Minimalistic send method.
   *
   * @param serverName Address of SMTP server (numerical or by name)
   * @param port SMTP port number (should be 25)
   * @param sender Address of mail sender
   * @param receiver Address of mail receiver
   * @param subject Subject of message
   * @param msg The message; line seperator is <code>'\n'</code>.
   * @exception UnknownHostException Unknown host
   * @exception IOException Error while communicating
   * @exception SmtpException Error returned by SMTP server
   *
   * @see #send
   */
  public void simpleSend(String serverName, int port, MailAddress sender,
                         MailAddress receiver, String subject, String msg)
                         throws SmtpException, UnknownHostException, IOException
  {
    serverAddress = InetAddress.getByName(serverName);
    serverPort = port;

    this.sender = sender;
    rcpts.addElement(receiver);

    this.subject = subject;
    message = msg;

    send();
  }

  /**
   * Sends a message to the SMTP server.
   *
   * <p>Sending is done via the following sequence:</p>
   * <ol>
   *   <li>Connect to server
   *   <li>HELO
   *   <li>MAIL FROM
   *   <li>RCPT TO
   *   <li>DATA
   *   <li>...data...
   *   <li>QUIT
   * </ol>
   *
   * <p>The following limitations apply:</p>
   * <ul>
   *   <li>No retry logic; all failures are considered as errors, although some
   *       may be recovered.
   *   <li>Message is sent to the first receiver only.
   *   <li>Header should contain more that <code>From</code>, <code>To</code>
   *       and <code>Subject</code> (eg DATE, MIME type).
   * </ul>
   *
   * @exception IOException Error while communicating
   * @exception SmtpException Error returned by SMTP server
   */
  protected void send()
    throws SmtpException, IOException
  {
    Socket sock = null;

    try {
      // setup connection
      trace("trying to connect to server");
      sock = new Socket(serverAddress, serverPort);

      BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      PrintWriter out = new PrintWriter(sock.getOutputStream());
      trace("connected");

      int rc = getResponse(in);

      if (rc/100 != 2) {
        throw new SmtpException("CONNECT", rc, lastResponseText);
      }

      // connected, be nice and say hello
      sendCommand(out, "HELO " + InetAddress.getLocalHost().getHostName(),
                  in, 2);

      sendCommand(out, "MAIL from: " + sender.getAddress(),
                  in, 2);

      // XXX only implemented sending to first rcpt
      sendCommand(out, "RCPT to: " + ((MailAddress)(rcpts.firstElement())).getAddress(), in, 2);

      sendCommand(out, "DATA", in, 3);

      sendData(out);
      rc = getResponse(in);
      if (rc/100 != 2) {
        throw new SmtpException("SEND-DATA", rc, lastResponseText);
      }

      sendCommand(out, "QUIT", in, 2);
    }
    catch (IOException e1) {
      if (sock != null) sock.close();
      /*re*/throw e1;
    }
    catch (SmtpException e2) {
      if (sock != null) sock.close();
      /*re*/throw e2;
    }
  }

  /**
   * Sends a single command to the SMTP server.
   *
   * @param out Outputstream to SMTP server
   * @param cmd Command to send, without trailing <code>&lt;CR/LF&gt;</code>.
   */
  protected void sendCommand(PrintWriter out, String cmd)
  {
    trace("sending command: " + cmd);
    out.write(cmd);
    out.write("\r\n");
    out.flush();
  }

  /**
   * Sends a command and throws a SMTPException if the returncode is not in the
   * expected class, ie the first digit of the returncode matches.
   *
   * @param out Outputstream to SMTP server
   * @param cmd Command to send, without trailing <code>&lt;CR/LF&gt;</code>.
   * @param in Inputstream to SMTP server
   * @param OkClass Class of returncodes that is ok
   * @exception SmtpException Thrown if answer not in expected returncode
   * class.
   * @exception IOException Error while communicating
   */
  protected void sendCommand(PrintWriter out, String cmd, BufferedReader in,
                             int OkClass)
    throws SmtpException, IOException
  {
    sendCommand(out, cmd);
    int rc = getResponse(in);

    if (rc/100 != OkClass) {
      throw new SmtpException(cmd, rc, lastResponseText);
    }
  }

  /**
   * Sends the data to the SMTP server.
   *
   * <p>First a simple header, containing the <var>From</var> and <var>To</var>
   * field is sent.  The follows the <var>Subject</var>.  After that we convert
   * the message to the data format and send it.  We finish the data with a
   * <code>&lt;CR/LF&gt;.&lt;CR/LF&gt;</code> sequence.</p>
   *
   * @param out Outputstream to SMTP server
   */
  protected void sendData(PrintWriter out)
  {
    // send header
    out.write("From: " + sender + "\r\n");
    out.write("To: " + rcpts.firstElement() + "\r\n");
    out.write("Subject: " + subject + "\r\n");
    out.write("\r\n"); // end header

    // send message text
    String data = msg2data(message);
    trace(data);
    out.write(data);

    // end data with <CR/LF>.<CR/LF>
    trace(".");
    out.write("\r\n.\r\n");
    out.flush();
  }

  /**
   * Converts a message to the data format.
   *
   * <p>The expected message format is: lines separated by newline
   * (<code>'\n'</code>) characters.  The SMTP data format is: lines separated
   * by <code>&lt;CR/LF&gt;</code>; if a message line begins with a period
   * another period is added in front of this line.</p>
   *
   * @param msg Message to convert
   * @return The converted message
   */
  protected String msg2data(String msg)
  {
    StringBuffer buff = new StringBuffer();
    String line;
    int start=0;
    int end=0;

    if (msg != null) {
      buff.ensureCapacity(msg.length()+100);
      do {
        end = msg.indexOf('\n', start);

        if (end == -1) {
          line = msg.substring(start);
        }
        else {
          line = msg.substring(start, end);
          end++; // skip newline character
        }

        if (line.length() > 0 && line.charAt(0) == '.') {
          buff.append('.');
        }

        buff.append(line);

        if (end != -1) {
          buff.append("\r\n");
        }

        start = end;
      } while (end != -1);
    }

    return buff.toString();
  }

  /**
   * Gets a response from the SMTP server.
   *
   * <p>Each line begins with a returncode, followed by either a space or a
   * dash (<code>'-'</code>).  A dash indicates that there are following lines,
   * a space that this is the last line.  The rest of a line is addition text.
   * Lines are separated by <code>&lt;CR/LF&gt;</code>.</p>
   *
   * @param in Inputstream to SMTP server
   * @return Returncode from SMTP server
   * @exception IOException Error while reading from server
   */
  protected int getResponse(BufferedReader in)
    throws IOException
  {
    int responseCode;
    boolean moreLines;
    String line;
    StringBuffer text = new StringBuffer();

    do {
      // check if there are more lines and, if yes, skip them
      line = in.readLine();
      trace("response: " + line);

      moreLines = (line.charAt(3) == '-');
      text.append(line.substring(4, line.length()));

    } while (moreLines);

    // last line; chars 0-2 contain returncode, char 3 is space, rest is additional text
    responseCode = Integer.parseInt(line.substring(0, 3));

    // store last response
    lastResponseText = text.toString();

    return responseCode;
  }

  /**
   * The internet address of the SMTP server
   */
  private InetAddress serverAddress = null;

  /**
   * The port on the host the SMTP server is running.
   */
  private int serverPort = -1;

  /**
   * Sender address.
   */
  private MailAddress sender = null;

  /**
   * Receivers.
   */
  private Vector rcpts = null;

  /**
   * Mail subject.
   */
  private String subject = null;

  /**
   * The message.  The expected message format is: lines separated by newline
   * <code>'\n'</code> characters.
   */
  private String message = null;

  /**
   * Text of the last received response of server.
   */
  private String lastResponseText = null;

  /**
   * Debugging?
   */
  private boolean debug = false;
}
