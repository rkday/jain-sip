/*
* Conditions Of Use 
* 
* This software was developed by employees of the National Institute of
* Standards and Technology (NIST), an agency of the Federal Government.
* Pursuant to title 15 Untied States Code Section 105, works of NIST
* employees are not subject to copyright protection in the United States
* and are considered to be in the public domain.  As a result, a formal
* license is not needed to use the software.
* 
* This software is provided by NIST as a service and is expressly
* provided "AS IS."  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
* OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
* AND DATA ACCURACY.  NIST does not warrant or make any representations
* regarding the use of the software or the results thereof, including but
* not limited to the correctness, accuracy, reliability or usefulness of
* the software.
* 
* Permission to use this software is contingent upon your acceptance
* of the terms of this agreement
*  
* .
* 
*/
/*******************************************************************************
* Product of NIST/ITL Advanced Networking Technologies Division (ANTD).        *
*******************************************************************************/
package gov.nist.javax.sip.header;

import java.text.ParseException;

/**
 * AllowEvents SIPHeader.
 *
 * @author M. Ranganathan  NIST/ITL ANTD. <br/>
 * @author Olivier Deruelle <br/>
 * 
 *
 * @version 1.2 $Revision: 1.3 $ $Date: 2006-07-02 09:50:52 $
 * @since 1.1
 */
public class AllowEvents
	extends SIPHeader
	implements javax.sip.header.AllowEventsHeader {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 617962431813193114L;
	/** method field
	 */
	protected String eventType;

	/** default constructor
	 */
	public AllowEvents() {
		super(ALLOW_EVENTS);
	}

	/** constructor
	 * @param m String to set
	 */
	public AllowEvents(String m) {
		super(ALLOW_EVENTS);
		eventType = m;
	}

	/**
	 * Sets the eventType defined in this AllowEventsHeader.
	 *
	 * @param eventType - the String defining the method supported
	 * in this AllowEventsHeader
	 * @throws ParseException which signals that an error has been reached
	 * unexpectedly while parsing the Strings defining the eventType supported
	 */
	public void setEventType(String eventType) throws ParseException {
		if (eventType == null)
			throw new NullPointerException(
				"JAIN-SIP Exception,"
					+ "AllowEvents, setEventType(), the eventType parameter is null");
		this.eventType = eventType;
	}

	/**
	 * Gets the eventType of the AllowEventsHeader. 
	 *
	 * @return the String object identifing the eventTypes of AllowEventsHeader.
	 */
	public String getEventType() {
		return eventType;
	}

	/** Return body encoded in canonical form.
	    * @return body encoded as a string.
	    */
	protected String encodeBody() {
		return eventType;
	}
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/06/19 06:47:26  mranga
 * javadoc fixups
 *
 * Revision 1.2  2006/06/16 15:26:28  mranga
 * Added NIST disclaimer to all public domain files. Clean up some javadoc. Fixed a leak
 *
 * Revision 1.1.1.1  2005/10/04 17:12:34  mranga
 *
 * Import
 *
 *
 * Revision 1.2  2004/01/22 13:26:29  sverker
 * Issue number:
 * Obtained from:
 * Submitted by:  sverker
 * Reviewed by:   mranga
 *
 * Major reformat of code to conform with style guide. Resolved compiler and javadoc warnings. Added CVS tags.
 *
 * CVS: ----------------------------------------------------------------------
 * CVS: Issue number:
 * CVS:   If this change addresses one or more issues,
 * CVS:   then enter the issue number(s) here.
 * CVS: Obtained from:
 * CVS:   If this change has been taken from another system,
 * CVS:   then name the system in this line, otherwise delete it.
 * CVS: Submitted by:
 * CVS:   If this code has been contributed to the project by someone else; i.e.,
 * CVS:   they sent us a patch or a set of diffs, then include their name/email
 * CVS:   address here. If this is your work then delete this line.
 * CVS: Reviewed by:
 * CVS:   If we are doing pre-commit code reviews and someone else has
 * CVS:   reviewed your changes, include their name(s) here.
 * CVS:   If you have not had it reviewed then delete this line.
 *
 */
