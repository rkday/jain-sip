/*******************************************************************************
* Product of NIST/ITL Advanced Networking Technologies Division (ANTD).        *
*******************************************************************************/
package gov.nist.javax.sip.header;
import gov.nist.core.*;
import javax.sip.header.*;

/**
* List of Require headers.
* <pre>
* Require  =  "Require" ":" 1#option-tag 
* </pre>
*
*@version  JAIN-SIP-1.1
*
*@author M. Ranganathan <mranga@nist.gov>  <br/>
*
*<a href="{@docRoot}/uncopyright.html">This code is in the public domain.</a>
*
*
*/
public final class RequireList extends SIPHeaderList {

        /** Default constructor
         */    
	public RequireList () {
		   super( Require.class, RequireHeader.NAME);
	}

         /** Constructor
         * @param sip SIPObjectList to set
         */    
	public RequireList (SIPObjectList sip) {
		super(sip, RequireHeader.NAME);
	}
        

}
