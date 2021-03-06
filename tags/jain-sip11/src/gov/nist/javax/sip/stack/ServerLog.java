/*******************************************************************************
 * Product of NIST/ITL Advanced Networking Technologies Division (ANTD).       *
 ******************************************************************************/



package gov.nist.javax.sip.stack;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import gov.nist.javax.sip.message.*;
import gov.nist.javax.sip.header.*;
import gov.nist.core.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.rmi.*;
import java.util.ListIterator;

/** Log file wrapper class.
 * Log messages into the message trace file and also write the log into the
 * debug file if needed. This class keeps an XML formatted trace around for
 * later access via RMI. The trace can be viewed with a trace viewer (see
 * tools.traceviewerapp).
 *
 *@version  JAIN-SIP-1.1
 *
 *@author M. Ranganathan <mranga@nist.gov>  <br/>
 *
 *<a href="{@docRoot}/uncopyright.html">This code is in the public domain.</a>
 *
 *
 */

public class ServerLog {
    
    /** Dont trace */
    public static int TRACE_NONE = 0;

    public static int TRACE_MESSAGES = 16;
    /** Trace exception processing
     */
    public static int TRACE_EXCEPTION = 17;
    /** Debug trace level (all tracing enabled).
     */
    public static int TRACE_DEBUG = 32;
    /** Name of the log file in which the trace is written out
     * (default is null)
     */
    private static String   logFileName;
    /** Name of the log directory in which the messages are written out
     */
    private static MessageLogTableImpl messageLogTable;
    /** Print writer that is used to write out the log file.
     */
    protected static PrintWriter printWriter;
    /** print stream for writing out trace
     */
    protected static PrintStream traceWriter = System.out;
    
    /** Name to assign for the log.
     */
    protected static String logRootName;
    
    /** Set auxililary information to log with this trace.
     */
    protected static String auxInfo;
    
    protected static String description;

    protected static String stackIpAddress;

    private static boolean initialized;
    
    
    //public static boolean isWebTesterCatchException=false;
    //public static String webTesterLogFile=null;
    
    /**
     *  Debugging trace stream.
     */
    private    static  PrintStream trace = System.out;
    /** default trace level
     */
    protected     static int traceLevel = TRACE_MESSAGES;
    
    public static void checkLogFile()  {
        if (logFileName == null) return;
        try {
            File  logFile = new File(logFileName);
            if (! logFile.exists()) {
                logFile.createNewFile();
                printWriter = null;
            }
            // Append buffer to the end of the file.
            if (printWriter == null) {
                FileWriter fw =
                new FileWriter(logFileName,true);
                printWriter = new PrintWriter(fw,true);
		printWriter.println
			("<!-- "+
			"Use the  Trace Viewer in src/tools/tracesviewer to" +
			" view this  trace  -->");
	        if (auxInfo != null)  {
			printWriter.println
			("<description\n logDescription=\""+description+
			"\"\n name=\"" + stackIpAddress  + 
			"\"\n auxInfo=\"" + auxInfo  + 
			"\"/>\n ");
			if ( LogWriter.needsLogging) {
			  LogWriter.logMessage(" ]]> ");
			  LogWriter.logMessage("</debug>");
			  LogWriter.logMessage
			  ("<description\n logDescription=\""+description+
			  "\"\n name=\"" + stackIpAddress  + 
			  "\"\n auxInfo=\"" + auxInfo  + 
			  "\"/>\n ");
			  LogWriter.logMessage("<debug>");
			  LogWriter.logMessage("<![CDATA[ ");
			}
	        } else  {
		    printWriter.println("<description\n logDescription=\""
			+description+ "\"\n name=\"" + stackIpAddress  + 
			"\" />\n");
		    if (LogWriter.needsLogging)  {
			 LogWriter.logMessage(" ]]>");
			  LogWriter.logMessage("</debug>");
		         LogWriter.logMessage
			 ("<description\n logDescription=\""
			+description+ "\"\n name=\"" + stackIpAddress  + 
			"\" />\n");
			LogWriter.logMessage("<debug>");
		        LogWriter.logMessage("<![CDATA[ ");
		   }
		}
            }
        } catch (IOException ex) {
            
        }
    }
    
    
    /**
     *Check to see if logging is enabled at a level (avoids
     * unecessary message formatting.
     *@param logLevel level at which to check.
     */
    public static boolean needsLogging(int logLevel) {
        return traceLevel >= logLevel;
    }
    
    
    /** Global check for whether to log or not. To minimize the time
     *return false here.
     *
     *@return true -- if logging is globally enabled and false otherwise.
     *
     */
    
    public static boolean needsLogging() {
        return logFileName != null;
    }
    
    /** Set the log file name
     *@param name is the name of the log file to set.
     */
    public static void setLogFileName(String name) {
        logFileName = name;
    }
    
    /** return the name of the log file.
     */
    public static String getLogFileName() { return logFileName; }
    
    
    /** Log a message into the log file.
     * @param message message to log into the log file.
     */
    private static void logMessage( String message) {
        // String tname = Thread.currentThread().getName();
        checkLogFile();
        String logInfo = message;
        if (printWriter == null) {
            System.out.println(logInfo);
        } else {
            printWriter.println(logInfo);
        }
        if (LogWriter.needsLogging) {
	    LogWriter.logMessage(" ]]>");
	    LogWriter.logMessage("</debug>");
            LogWriter.logMessage(logInfo);
	    LogWriter.logMessage("<debug>");
	    LogWriter.logMessage("<![CDATA[ ");
        }
    }
    
    /** Log a message into the log directory.
     * @param message a SIPMessage to log
     * @param from from header of the message to log into the log directory
     * @param to to header of the message to log into the log directory
     * @param sender is the server the sender (true if I am the sender).
     * @param callId CallId of the message to log into the log directory.
     * @param firstLine First line of the message to display
     * @param status Status information (generated while processing message).
     * @param time the reception time (or date).
     */
    public synchronized static void logMessage(String message,
    String from,
    String to,
    boolean sender,
    String callId,
    String firstLine,
    String status,
    String tid,
    String  time) {
        
        MessageLog log = new MessageLog(message, from, to, time,
            sender,  firstLine, status, tid,callId);
        logMessage(log.flush());
    }

    public synchronized static void logMessage(String message,
    String from,
    String to,
    boolean sender,
    String callId,
    String firstLine,
    String status,
    String tid,
    long  time) {
        
        MessageLog log = new MessageLog(message, from, to, time,
            sender,  firstLine,
            status, tid, callId);
	logMessage(log.flush());
    }
    
    /** Log a message into the log directory.
     * @param message a SIPMessage to log
     * @param from from header of the message to log into the log directory
     * @param to to header of the message to log into the log directory
     * @param sender is the server the sender
     * @param callId CallId of the message to log into the log directory.
     * @param firstLine First line of the message to display
     * @param status Status information (generated while processing message).
     * @param tid    is the transaction id for the message.
     */
    public static void logMessage(String message,
    String from, String to,
    boolean sender,
    String callId,
    String firstLine,
    String status,
    String tid) {
        String time = new Long(System.currentTimeMillis()).toString();
        logMessage
        (message,from, to,sender,callId,firstLine,status,
        tid,time);
    }
    
    
    /** Log a message into the log directory. 
     * @param message a SIPMessage to log
     * @param from from header of the message to log into the log directory
     * @param to to header of the message to log into the log directory
     * @param sender is the server the sender
     * @param time is the time to associate with the message.
     */
    public static void logMessage(SIPMessage message, String from,
    String to, boolean sender, String time) {
        checkLogFile();
        CallID cid = (CallID)message.getCallId();
        String callId = null;
        if (cid != null) callId = ((CallID)message.getCallId()).getCallId();
        String firstLine = message.getFirstLine().trim();
        String inputText = message.encode();
        String tid = message.getTransactionId();
        logMessage( inputText , from, to,  sender,
        callId, firstLine,null,tid,time);
    }
    
    /** Log a message into the log directory.
     * @param message a SIPMessage to log
     * @param from from header of the message to log into the log directory
     * @param to to header of the message to log into the log directory
     * @param sender is the server the sender
     * @param time is the time to associate with the message.
     */
    public static void logMessage(SIPMessage message, String from,
    String to, boolean sender, long time) {
        checkLogFile();
        CallID cid =(CallID )message.getCallId();
        String callId = null;
        if (cid != null) callId = cid.getCallId();
        String firstLine = message.getFirstLine().trim();
        String inputText = message.encode();
        String tid = message.getTransactionId();
        logMessage( inputText , from, to, sender,
        callId, firstLine,null,tid,time);
    }
    
    /** Log a message into the log directory. Status information is extracted
     * from SIPExtension header. The time associated with the message is the
     * current time.
     * @param message a SIPMessage to log
     * @param from from header of the message to log into the log directory
     * @param to to header of the message to log into the log directory
     * @param sender is the server the sender
     */
    public static void logMessage(SIPMessage message, String from,
    String to, boolean sender) {
        logMessage(message,from,to,sender,
        new Long(System.currentTimeMillis()).toString());
    }
    
    /** Log a message into the log directory.
     * @param message a SIPMessage to log
     * @param from from header of the message to log into the log directory
     * @param to to header of the message to log into the log directory
     * @param status the status to log. 
     * @param sender is the server the sender or receiver (true if sender).
     * @param time is the reception time.
     */
    public static void logMessage(SIPMessage message, String from,
    String to, String status,
    boolean sender, String time) {
        checkLogFile();
        CallID cid =(CallID) message.getCallId();
        String callId = null;
        if (cid != null) callId = cid.getCallId();
        String firstLine = message.getFirstLine().trim();
        String encoded = message.encode();
        String tid = message.getTransactionId();
        logMessage( encoded , from, to,  sender,
        callId, firstLine,status,tid,time);
    }
    /** Log a message into the log directory.
     * @param message a SIPMessage to log
     * @param from from header of the message to log into the log directory
     * @param to to header of the message to log into the log directory
     * @param status the status to log. 
     * @param sender is the server the sender or receiver (true if sender).
     * @param time is the reception time.
     */
    public static void logMessage(SIPMessage message, String from,
    String to, String status,
    boolean sender, long time) {
        checkLogFile();
        CallID cid = (CallID) message.getCallId();
        String callId = null;
        if (cid != null) callId = cid.getCallId();
        String firstLine = message.getFirstLine().trim();
        String encoded = message.encode();
        String tid = message.getTransactionId();
        logMessage( encoded , from, to,  sender,
        callId, firstLine,status,tid,time);
    }
    
    /** Log a message into the log directory. Time stamp associated with the
     * message is the current time.
     * @param message a SIPMessage to log
     * @param from from header of the message to log into the log directory
     * @param to to header of the message to log into the log directory
     * @param status the status to log.
     * @param sender is the server the sender or receiver (true if sender).
     */
    public static void logMessage(SIPMessage message, String from,
    String to, String status,
    boolean sender) {
        logMessage(message,from,to,status,sender,
        System.currentTimeMillis());
    }
    
    
    /** Log a message into the log file.
     * @param msgLevel Logging level for this message.
     * @param tracemsg message to write out.
     */
    public static void traceMsg(int msgLevel, String tracemsg) {
        if (needsLogging(msgLevel))   {
            traceWriter.println(tracemsg);
            logMessage(tracemsg);
        }
    }
    
    /** Log an exception stack trace.
     * @param ex Exception to log into the log file
     */
    
    public static void logException(Exception ex) {
        if (traceLevel >= TRACE_EXCEPTION) {
            checkLogFile();
            ex.printStackTrace();
            if (printWriter != null) ex.printStackTrace(printWriter);
            
        }
            /*
            if (isWebTesterCatchException) {
                try {
                    PrintWriter printWriterWeb=null;
                    File  logFile = new File(webTesterLogFile);
                    if (! logFile.exists()) {
                        logFile.createNewFile();
                        printWriterWeb = null;
                    }
                    // Append buffer to the end of the file.
                    if (printWriterWeb == null) {
                        FileWriter fw =
                        new FileWriter(webTesterLogFile,true);
                        printWriterWeb = new PrintWriter(fw,true);
                        ex.printStackTrace(printWriterWeb);
                    }
                } catch (IOException ex) {
                    ServerInternalError.handleException(ex);
                }
             
            }*/
    }
    
    /** Set the name to assign for the log.
     */
    public static void setLogName(String name) {
        logRootName = name;
    }
    
    /** Initialize the table for RMI access to the log file.
     * Call this function when the stack initializes to allow
     * remote access to the message log.
     *@param stacAddress is the address where the rmiRegistry will run.
     *@param rmiPort is the RMI registry port.
     *@param logRootName is the root name to assign to the log. Logs are
     * stored as logRootName/logId where a good value to pick for
     * logRootName is host:udpPort or host:tcpPort for the machine from
     * where the log originated.
     */
    public static void initMessageLogTable(
    String stackAddress,
    int rmiPort,
    String rootName ,
    int traceLifeTime )
    throws RemoteException {
        logRootName = rootName;
        messageLogTable = new MessageLogTableImpl(rmiPort);
        messageLogTable.init(stackAddress, rmiPort,
        logRootName, traceLifeTime);
    }
    
    /** Return the message log table.
     *@return the messageLogTable member.
     */
    public static MessageLogTableImpl getMessageLogTable() {
        return messageLogTable;
    }
    
    /** print a line to stdout if the traceLevel is TRACE_DEBUG.
     * @param s String to print out.
     */
    public static void println( String s) {
        if (traceLevel == TRACE_DEBUG) System.out.println(s);
    }
    
    /** Set the trace level for the stack.
     *
     *@param level -- the trace level to set. The following trace levels are
     *supported:
     *<ul>
     *<li>
     *0 -- no tracing
     *</li>
     *
     *<li>
     *16 -- trace messages only
     *</li>
     *
     *<li>
     *32 Full tracing including debug messages.
     *</li>
     *
     *</ul>
     */
    public static void setTraceLevel(int level) {
        traceLevel = level;
    }
    
    /** Get the trace level for the stack.
     *
     *@return the trace level
     */
    public static int getTraceLevel() { return traceLevel; }
    
    
    /** Set aux information. Auxiliary information may be associated
     *with the log file. This is useful for remote logs.
     *
     *@param auxInfo -- auxiliary information.
     *
     */
    public static void setAuxInfo(String auxInfo) {
        ServerLog.auxInfo = auxInfo;
    }
    
    /** Set the descriptive String for the log.
     *
     *@param desc is the descriptive string.
     */
    public static void setDescription(String desc) {
		description = desc;
    }
    
    
    
}
