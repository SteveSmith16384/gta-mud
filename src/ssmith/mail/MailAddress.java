package ssmith.mail;
//package rblasch.Mail;

/**
 * MailAddress contains the information <em>address</em> and
 * <em>realName</em>.
 *
 * <p>adress is the email address,
 * e.g. <samp>rblasch@cs.bgsu.edu</samp>. realName would be <samp>Ronald
 * Blaschke</samp>  The email address is <b>required</b>, the realName is
 * optional.</p>
 *
 * <p>The string representation is as follows:</p>
 * <dl>
 *   <dt>email address only
 *   <dd>&lt;<var>email address</var>&gt;
 *   <dt>email address and real life name
 *   <dd>"<var>Real Life Name</var>" &lt;<var>email address</var>&gt;
 * </dl>
 *
 * @author Ronald Blaschke &lt;rblasch@cs.bgsu.edu&gt;
 * @version 1.0
 */
public class MailAddress
{
  /**
   * Create new Object.
   */
  public MailAddress()
  {
    super();
  }

  /**
   * Convert a String to a MailAddress.  Currently the String may only contain
   * the email address, eg <samp>rblasch@cs.bgsu.edu</samp>.
   *
   * @param address String containing the address
   * @return The MailAddress
   */
  public static MailAddress parseAddress(String address)
  {
    MailAddress addr = new MailAddress();
    addr.setAddress(address);

    return addr;
  }

  /**
   * Sets the email address part.
   *
   * @param address email address
   */
  public void setAddress(String address)
  {
    this.address = address;
  }

  /**
   * Gets the email address part.
   *
   * @return email address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the real life name part.
   *
   * @param realName real life name
   */
  public void setRealName(String realName)
  {
    this.realName = realName;
  }

  /**
   * Gets the real life name part.
   *
   * @return real life name
   */
  public String getRealName() {
    return realName;
  }

  /**
   * Converts this object to a String; see class description for format.
   *
   * @return String representation of this MailAddress
   */
  public String toString()
  {
    StringBuffer buff = new StringBuffer();

    // real life name is OPTIONAL
    if (realName != null) {
      buff.append("\"" + realName + "\" ");
    }

    // address is REQUIRED
    if (address != null) {
      buff.append("<" + address + ">");
    }
    else {
      return null;
    }

    return buff.toString();
  }

  /**
   * The email address.  Eg <samp>rblasch@cs.bgsu.edu</samp>.
   */
  private String address = null;

  /**
   * The real life name.  Eg <samp>Ronald Blaschke</samp>.
   */
  private String realName = null;
}
