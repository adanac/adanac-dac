package com.adanac.framework.rws.exception;

public class RWSException extends RuntimeException {

	/**
	 */
	private static final long serialVersionUID = 1L;

	public RWSException() {
		super();
	}

	public RWSException(String message, Throwable cause) {
		super(message, cause);
	}

	public RWSException(String message) {
		super(message);
	}

	public RWSException(Throwable cause) {
		super(cause);
	}
}
