package ssmith.mail;

//package rblasch.Mail;

/**
 * This class is used to indicate an error while communicating with an SMTP
 * server.
 *
 * @author Ronald Blaschke &lt;rblasch@cs.bgsu.edu&gt;
 * @version 1.0
 */
public class SmtpException extends Exception
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1985269029667858044L;
/**
   * Create a new Exception.
   *
   * @param lastCmd The last command sent before the error was recognized.
   * @param errorCode The errorcode returned by the SMTP server.
   * @param errorMsg The errormessage (from SMTP server).
   */
  public SmtpException(String lastCmd, int errorCode, String errorMsg)
  {
    this.lastCmd = lastCmd;
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
  }

  /**
   * Convert Exception to String.
   *
   * <p>Format is <code>Error while executing cmd <var>&lt;command&gt;</var>:
   * <var>&lt;errorCode&gt;</var> - <var>&lt;errorMessage&gt;</var>.</p>
   *
   * @return String representation of Exception
   */
  public String toString()
  {
    StringBuffer buff = new StringBuffer();

    buff.append("Error while executing cmd " + lastCmd + ":"
                + errorCode + "-" + errorMsg);

    return buff.toString();
  }

  String lastCmd = null;
  int errorCode = -1;
  String errorMsg = null;
}
