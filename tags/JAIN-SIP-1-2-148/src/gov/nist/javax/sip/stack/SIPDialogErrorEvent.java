/*
 * This source code has been contributed to the public domain by Mobicents
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
 * of the terms of this agreement.
 */
package gov.nist.javax.sip.stack;


import gov.nist.javax.sip.DialogTimeoutEvent;

import java.util.EventObject;

/**
 * An event that indicates that a dialog has encountered an error.
 * 
 * @author jean deruelle
 * @since 2.0
 */
public class SIPDialogErrorEvent extends EventObject {


    /**
     * This event ID indicates that the transaction has timed out.
     */
    public static final int DIALOG_ACK_NOT_RECEIVED_TIMEOUT = 1;

    /**
     * This event ID indicates that there was an error sending a message using
     * the underlying transport.
     */
    public static final int DIALOG_ACK_NOT_SENT_TIMEOUT = 2;
    
    /**
     * This event ID indicates a timeout occured waiting to send re-INVITE ( for B2BUA)
     */
    public static final int DIALOG_REINVITE_TIMEOUT = 3;
    
    /*
     * This event Id indicates that some internal error happened and the ACK semaphore could not be acquired.
     */
    public static final int DIALOG_ERROR_INTERNAL_COULD_NOT_TAKE_ACK_SEM = 4;
    

    // ID of this error event
    private int errorID;

    /**
     * Creates a dialog error event.
     *
     * @param sourceDialog Dialog which is raising the error.
     * @param dialogErrorID ID of the error that has ocurred.
     */
    SIPDialogErrorEvent(
        SIPDialog sourceDialog,
        int dialogErrorID) {

        super(sourceDialog);
        errorID = dialogErrorID;

    }
    
    public SIPDialogErrorEvent (SIPDialog sourceDialog, DialogTimeoutEvent.Reason reason) {
    	super(sourceDialog);
    	if ( reason == DialogTimeoutEvent.Reason.AckNotReceived) {
    		this.errorID = DIALOG_ACK_NOT_RECEIVED_TIMEOUT;
    	} else if ( reason == DialogTimeoutEvent.Reason.AckNotSent ) {
    		this.errorID = DIALOG_ACK_NOT_SENT_TIMEOUT;
    	} else if ( reason == DialogTimeoutEvent.Reason.ReInviteTimeout) {
    		this.errorID = DIALOG_REINVITE_TIMEOUT;
    	} else if (reason == DialogTimeoutEvent.Reason.CannotAcquireAckSemaphoreForOk) {
    		this.errorID = DIALOG_ERROR_INTERNAL_COULD_NOT_TAKE_ACK_SEM;
    	}
    }

    /**
     * Returns the ID of the error.
     *
     * @return Error ID.
     */
    public int getErrorID() {
        return errorID;
    }
}
