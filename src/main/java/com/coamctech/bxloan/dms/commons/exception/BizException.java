package com.coamctech.bxloan.dms.commons.exception;

/**
 * 自定义业务异常
 * @author lyq
 *
 */
public class BizException extends RuntimeException{
	
	private static final long serialVersionUID = -2971676549928286528L;

	public BizException() {
		super();
	}

	public BizException(String message, Throwable cause) {
		super(message, cause);
	}

	public BizException(String message) {
		super(message);
	}

	public BizException(Throwable cause) {
		super(cause);
	}
}
