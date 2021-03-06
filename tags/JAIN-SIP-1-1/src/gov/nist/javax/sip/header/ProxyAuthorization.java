/*******************************************************************************
* Product of NIST/ITL Advanced Networking Technologies Division (ANTD).        *
*******************************************************************************/
package gov.nist.javax.sip.header;

import javax.sip.header.*;

/**  
 * ProxyAuthorization Header.
 *
 * @version JAIN-SIP-1.1 $Revision: 1.2 $ $Date: 2004-01-22 13:26:29 $
 *
 * @author M. Ranganathan <mranga@nist.gov>  <br/>
 * @author Olivier Deruelle <deruelle@nist.gov><br/>
 *
 * <a href="{@docRoot}/uncopyright.html">This code is in the public domain.</a>
 *
 */
public class ProxyAuthorization
	extends AuthenticationHeader
	implements ProxyAuthorizationHeader {

	/** default constructor
	 */
	public ProxyAuthorization() {
		super(PROXY_AUTHORIZATION);
	}

}
/*
 * $Log: not supported by cvs2svn $
 */
