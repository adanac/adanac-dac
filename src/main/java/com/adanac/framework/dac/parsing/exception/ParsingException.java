package com.adanac.framework.dac.parsing.exception;

/**
 * 描述：解析异常类
 * @author 13092011
 */
public class ParsingException extends RuntimeException {
    private static final long serialVersionUID = -176685891441325943L;

    public ParsingException() {
        super();
    }

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsingException(Throwable cause) {
        super(cause);
    }
}