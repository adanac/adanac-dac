package com.adanac.framework.dac.exception;

/**
 * 
 * Dal异常类<br>
 * @author adanac
 */
public class DalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DalException() {
	}

	public DalException(String msg) {
		super(msg);
	}

	public DalException(Throwable exception) {
		super(exception);
	}

	public DalException(String mag, Exception exception) {
		super(mag, exception);
	}
}
