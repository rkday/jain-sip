package gov.nist.javax.sip.message;

import gov.nist.javax.sip.header.*;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Allows for uniform removal handling for singleton headers.
 * @version JAIN-SIP-1.1 $Revision: 1.5 $ $Date: 2004-01-22 13:26:31 $
 */
public class HeaderIterator implements ListIterator {
	private boolean toRemove;
	private int index;
	private SIPMessage sipMessage;
	private SIPHeader sipHeader;

	protected HeaderIterator(SIPMessage sipMessage, SIPHeader sipHeader) {
		this.sipMessage = sipMessage;
		this.sipHeader = sipHeader;
	}

	public Object next() throws NoSuchElementException {
		if (sipHeader == null || index == 1)
			throw new NoSuchElementException();
		toRemove = true;
		index = 1;
		return (Object) sipHeader;
	}

	public Object previous() throws NoSuchElementException {
		if (sipHeader == null || index == 0)
			throw new NoSuchElementException();
		toRemove = true;
		index = 0;
		return (Object) sipHeader;
	}

	public int nextIndex() {
		return 1;
	}

	public int previousIndex() {
		return index == 0 ? -1 : 0;
	}

	public void set(Object header) {
		throw new UnsupportedOperationException();
	}

	public void add(Object header) {
		throw new UnsupportedOperationException();
	}

	public void remove() throws IllegalStateException {
		if (this.sipHeader == null)
			throw new IllegalStateException();
		if (toRemove) {
			this.sipHeader = null;
			this.sipMessage.removeHeader(sipHeader.getName());
		} else {
			throw new IllegalStateException();
		}
	}

	public boolean hasNext() {
		return index == 0;
	}

	public boolean hasPrevious() {
		return index == 1;
	}
}
/*
 * $Log: not supported by cvs2svn $
 */
