package test.tck.msgflow;

import junit.framework.*;

import javax.sip.*;
import javax.sip.address.*;
import javax.sip.message.*;
import javax.sip.header.*;
import java.util.Properties;
import java.util.List;
import java.util.*;
import java.text.*;
import test.tck.*;

/**
 * <p>Title: TCK</p>
 * <p>Description: JAIN SIP 1.1 Technology Compatibility Kit</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NIST</p>
 * @author Emil Ivov
 *      Network Research Team, Louis Pasteur University, Strasbourg, France.
 * This  code is in the public domain.
 * @version 1.0
 */

public class SipProviderTest
    extends MessageFlowHarness
{

    public SipProviderTest(String name)
    {
        super(name);
    }

    //======================= tests ====================================
    /**
     * Sends a single invite request and checks whether it arrives normally
     * at the other end.
     */
    public void testSendRequest()
    {
        try
        {
            //create an empty invite request.
            Request invite = createTiInviteRequest(null, null, null);
            Request receivedRequest = null;
            try
            {
                //Send using TI and collect using RI
                eventCollector.collectRequestEvent(riSipProvider);
                waitForMessage();
                tiSipProvider.sendRequest(invite);
                waitForMessage();
                RequestEvent receivedRequestEvent =
                    eventCollector.extractCollectedRequestEvent();
                assertNotNull(
                    "The sent request was not received at the other end!",
                    receivedRequestEvent
                    );
                assertNotNull(
                    "The sent request was not received at the other end!",
                    receivedRequestEvent.getRequest()
                    );
            }
            catch(TooManyListenersException ex)
            {
                throw new TckInternalError(
                    "The following exception was thrown while trying to add "+
                    "a SipListener to an RI SipProvider",
                    ex);
            }
            catch(SipException ex)
            {
                ex.printStackTrace();
                fail("A SipException exception was thrown while "
                     +"trying to send a request.");
            }
        }
        catch(Throwable exc)
        {
            exc.printStackTrace();
            fail(exc.getClass().getName()+": "+exc.getMessage());
        }
        assertTrue(new Exception().getStackTrace()[0].toString(), true);


    }

    /**
     * Send a simple invite request from the RI and check whether it is properly
     * delivered by the TI SipProvider
     */
    public void testReceiveRequest()
    {
        try
        {
            //create an empty invite request.
            Request invite = createRiInviteRequest(null, null, null);
            RequestEvent receivedRequestEvent = null;
            try
            {
                //Send using RI and collect using TI
                eventCollector.collectRequestEvent(tiSipProvider);
                riSipProvider.sendRequest(invite);
                waitForMessage();
                receivedRequestEvent =
                    eventCollector.extractCollectedRequestEvent();
                assertNotNull(
                    "The sent request was not received at the other end!",
                    receivedRequestEvent);
                assertNotNull(
                    "The sent request was not received at the other end!",
                    receivedRequestEvent.getRequest());
            }
            catch(TooManyListenersException ex)
            {
                //This time adding the listener is (sort of) part of the test
                //so we fail instead of just "throwing on" the exc
                ex.printStackTrace();
                fail("A TooManyListenersException was thrown while trying to add "+
                     "a SipListener to a TI SipProvider.");
            }
            catch(SipException ex)
            {
                throw new TckInternalError("The RI failed to send the request!", ex);
            }
            //question: should we compare sent and received request?
            //my opinion: finding a discrepancy while comparing requests
            //would most probably mean a parse error and parsing is not what
            //we are currently testing
            //Transaction initiating requests should not have a server transaction
            //associated with them as the application might decide to handle the
            //request statelessly
            assertNull(
                "The Tested Implementation has implicitly created a ServerTransaction "
                +"for the received request. Transactions should only be created "
                +"explicitly using the SipProvider.getNewXxxTransaction() method.",
                receivedRequestEvent.getServerTransaction());
        }
        catch(Throwable exc)
        {
            exc.printStackTrace();
            fail(exc.getClass().getName()+": "+exc.getMessage());
        }
        assertTrue(new Exception().getStackTrace()[0].toString(), true);
    }

    /**
     * Sends a request from the RI, generates a response at the TI side, sends
     * it back and checks whether it arrives at the RI.
     */
    public void testSendResponse()
    {
        try
        {
            // 1. Create and send the original request

            Request invite = createRiInviteRequest(null, null, null);
            RequestEvent receivedRequestEvent = null;
            try
            {
                //Send using RI and collect using TI
                eventCollector.collectRequestEvent(tiSipProvider);
                riSipProvider.sendRequest(invite);
                waitForMessage();
                receivedRequestEvent =
                    eventCollector.extractCollectedRequestEvent();
                if(receivedRequestEvent==null||
                   receivedRequestEvent.getRequest()==null)
                    throw new TiUnexpectedError(
                        "The sent request was not received by the RI!");
            }
            catch(TooManyListenersException ex)
            {
                throw new TiUnexpectedError
                    ("A TooManyListenersException was thrown while trying to add "+
                     "a SipListener to a TI SipProvider.",
                     ex);
            }
            catch(SipException ex)
            {
                throw new TckInternalError("The RI failed to send the request!",
                                           ex);
            }
            Request receivedRequest = receivedRequestEvent.getRequest();
            // 2. Create and send the response
            //Create an ok response. We are not testing the message factory so let's
            //not leave it a chance to mess something up and specify the response
            //as completely as possible.
            List via = new LinkedList();
            via.add(receivedRequest.getHeader(ViaHeader.NAME));
            Response ok = null;
            try
            {
                ok = tiMessageFactory.createResponse(
                    Response.OK,
                    (CallIdHeader)receivedRequest.getHeader(CallIdHeader.NAME),
                    (CSeqHeader)receivedRequest.getHeader(CSeqHeader.NAME),
                    (FromHeader)receivedRequest.getHeader(FromHeader.NAME),
                    (ToHeader)receivedRequest.getHeader(ToHeader.NAME),
                    via, (MaxForwardsHeader)receivedRequest.getHeader
			(MaxForwardsHeader.NAME)
                    );
		addStatus(receivedRequest,ok);
            }
            catch(ParseException ex)
            {
                throw new TiUnexpectedError("Failed to create an OK response!", ex);
            }
            //Send the response using the TI and collect using RI
            try
            {
                eventCollector.collectResponseEvent(riSipProvider);
            }
            catch(TooManyListenersException ex)
            {
                throw new TckInternalError("Error while trying to add riSipProvider");
            }
            try
            {
                tiSipProvider.sendResponse(ok);
            }
            catch(SipException ex)
            {
                ex.printStackTrace();
                fail("A SipException occurred while trying to send an ok response.");
            }
            waitForMessage();
            ResponseEvent responseEvent = eventCollector.extractCollectedResponseEvent();
            assertNotNull("The sent response was not received by the RI!", responseEvent);
            assertNotNull("The sent response was not received by the RI!", responseEvent.getResponse());
        }
        catch(Throwable exc)
        {
            exc.printStackTrace();
            fail(exc.getClass().getName()+": "+exc.getMessage());
        }
        assertTrue(new Exception().getStackTrace()[0].toString(), true);

    }


    /**
     * Sends a request from the TI, generates a response at the RI side, sends
     * it back and checks whether it arrives at the TI.
     */
    public void testReceiveResponse()
    {
        try
        {
            // 1. Create and send the original request

            Request invite = createTiInviteRequest(null, null, null);
            RequestEvent receivedRequestEvent = null;
            try
            {
                //Send using TI and collect using RI
                eventCollector.collectRequestEvent(riSipProvider);
                tiSipProvider.sendRequest(invite);
                waitForMessage();
                receivedRequestEvent =
                    eventCollector.extractCollectedRequestEvent();
                if(receivedRequestEvent==null||
                   receivedRequestEvent.getRequest()==null)
                    throw new TckInternalError(
                        "The sent request was not received by the RI!");
            }
            catch(TooManyListenersException ex)
            {
                throw new TckInternalError
                    ("A TooManyListenersException was thrown while trying to add "+
                     "a SipListener to an RI SipProvider.",
                     ex);
            }
            catch(SipException ex)
            {
                throw new TiUnexpectedError("The TI failed to send the request!",
                                            ex);
            }
            Request receivedRequest = receivedRequestEvent.getRequest();
            // 2. Create and send the response
            Response ok = null;
            try
            {
                ok = riMessageFactory.createResponse(Response.OK, receivedRequest);
		addStatus(receivedRequest,ok);
            }
            catch(ParseException ex)
            {
                throw new TckInternalError("Failed to create an OK response!", ex);
            }
            //Send the response using the RI and collect using TI
            try
            {
                eventCollector.collectResponseEvent(tiSipProvider);
            }
            catch(TooManyListenersException ex)
            {
                throw new TiUnexpectedError("Error while trying to add riSipProvider");
            }
            try
            {
                riSipProvider.sendResponse(ok);
            }
            catch(SipException ex)
            {
                throw new TckInternalError("Could not send back the response", ex);
            }
            waitForMessage();
            ResponseEvent responseEvent = eventCollector.extractCollectedResponseEvent();
            //3. Now ... do we like what we got?
            assertNotNull("The TI failed to receive the response!", responseEvent);
            assertNotNull("The TI failed to receive the response!", responseEvent.getResponse());
            assertNull(
                "The TI had implicitly created a client transaction! "
                +"Transactions should only be created explicitly using "
                +"the SipProvider.getNewXxxTransaction() method.",
                responseEvent.getClientTransaction());
        }
        catch(Throwable exc)
        {
            exc.printStackTrace();
            fail(exc.getClass().getName()+": "+exc.getMessage());
        }
        assertTrue(new Exception().getStackTrace()[0].toString(), true);
    }

    /**
     * Test whether new ClientTransactions are properly created.
     */
    public void testGetNewClientTransaction()
    {
        try
        {
            Request invite = createTiInviteRequest(null, null, null);
            ClientTransaction tran = null;
            try
            {
                tran = tiSipProvider.getNewClientTransaction(invite);
            }
            catch(TransactionUnavailableException exc)
            {
                exc.printStackTrace();
                fail("A TransactionUnavailableException was thrown while trying to "
                     +"create a new client transaction");
            }
            assertNotNull(
                "A null ClientTransaction was returned by SipProvider."
                +"getNewClientTransaction().",
                tran);
            String tranBranch = tran.getBranchId();
            String reqBranch = ((ViaHeader)invite.getHeader(ViaHeader.NAME)).getBranch();
            assertEquals(
                "The newly created transaction did not have the same "
                +"branch id as the request that created it",
                tranBranch, reqBranch
                );
            assertNotNull(
                "The newly created transaction returned a null Dialog. "
                +"Please check the docs on Transaction.getDialog()",
                tran.getDialog());
            assertNotNull(
                "The transaction's getRequest() method returned a null Request ",
                tran.getRequest());
            assertEquals(
                "The transaction's getRequest() method returned a Request "
                +"that did not match the one that we used to create it!",
                tran.getRequest(),
                invite
                );
        }
        catch(Throwable exc)
        {
            exc.printStackTrace();
            fail(exc.getClass().getName()+": "+exc.getMessage());
        }
        assertTrue(new Exception().getStackTrace()[0].toString(), true);

    }

    /**
     * Sends a request from the RI and tests whether the tested implementation
     * properly creates a ServerTransaction.
     */
    public void testGetNewServerTransaction()
    {
        try
        {
            Request invite = createRiInviteRequest(null, null, null);
            ServerTransaction tran = null;
            RequestEvent receivedRequestEvent = null;
            try
            {
                //Send using RI and collect using TI
                eventCollector.collectRequestEvent(tiSipProvider);
                riSipProvider.sendRequest(invite);
                waitForMessage();
                receivedRequestEvent =
                    eventCollector.extractCollectedRequestEvent();
                if(receivedRequestEvent==null||
                   receivedRequestEvent.getRequest()==null)
                    throw new TiUnexpectedError(
                        "The sent request was not received by the RI!");
            }
            catch(TooManyListenersException ex)
            {
                throw new TiUnexpectedError
                    ("A TooManyListenersException was thrown while trying to add "+
                     "a SipListener to a TI SipProvider.",
                     ex);
            }
            catch(SipException ex)
            {
                throw new TckInternalError("The RI failed to send the request!",
                                           ex);
            }
            try
            {
                tran = tiSipProvider.getNewServerTransaction(invite);
            }
            catch(TransactionUnavailableException exc)
            {
                exc.printStackTrace();
                fail("A TransactionUnavailableException was thrown while trying to "
                     +"create a new client transaction");
            }
            catch(TransactionAlreadyExistsException exc)
            {
                exc.printStackTrace();
                fail("A TransactionAlreadyExistsException was thrown while trying to "
                     +"create a new server transaction");
            }
            assertNotNull("A null ServerTransaction was returned by SipProvider."
                          +"getNewServerTransaction().",
                          tran);
            String tranBranch = tran.getBranchId();
            String reqBranch = ((ViaHeader)invite.getHeader(ViaHeader.NAME)).getBranch();
            assertEquals("The newly created transaction did not have the same "
                         +"branch id as the request that created it!"
                         , tranBranch
                         , reqBranch);
            assertNotNull(
                "The newly created transaction returned a null Dialog. "
                +"Please check the docs on Transaction.getDialog()",
                tran.getDialog());
            assertNotNull(
                "The transaction's getRequest() method returned a null Request ",
                tran.getRequest());
            assertEquals(
                "The transaction's getRequest() method returned a Request "
                +"that did not match the one that we used to create it!",
                tran.getRequest(),
                invite);
        }
        catch(Throwable exc)
        {
            exc.printStackTrace();
            fail(exc.getClass().getName()+": "+exc.getMessage());
        }
        assertTrue(new Exception().getStackTrace()[0].toString(), true);
    }



    //==================== end of tests


    //====== STATIC JUNIT ==========
    public static Test suite()
    {
        return new TestSuite(SipProviderTest.class);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(SipProviderTest.class);
    }



}
/**
 *
 */
