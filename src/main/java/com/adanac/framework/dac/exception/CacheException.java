package com.adanac.framework.dac.exception;

/**
 * 缓存异常
 * @author adanac
 * @version 1.0
 */
public class CacheException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CacheException() {
	}

	public CacheException(String msg) {
		super(msg);
	}

	public CacheException(Throwable exception) {
		super(exception);
	}

	public CacheException(String mag, Exception exception) {
		super(mag, exception);
	}
}
