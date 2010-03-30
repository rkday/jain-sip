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
package gov.nist.javax.sip.stack;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Factory used to create message processor instances on behalf of the stack 
 * which are created when a new Listening point is created. 
 * This allows to plug other implementations of MessageProcessor than the ones 
 * provided by default with the jain sip stack
 * 
 * @author jean.deruelle@gmail.com
 *
 */
public interface MessageProcessorFactory {
	MessageProcessor createMessageProcessor(SIPTransactionStack sipStack, InetAddress ipAddress,
			int port, String transport) throws IOException; 
}
