package com.coamctech.bxloan.dms.web.advice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.coamctech.bxloan.dms.commons.exception.BizException;
import com.coamctech.bxloan.dms.dao.entity.Result;

/**
 *	统一异常处理
 */
@ControllerAdvice
public class WebControllerAdvice {
	
	protected Logger logger = LoggerFactory.getLogger(WebControllerAdvice.class);

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<?> handleException(Throwable ex,
			HttpServletRequest request, HttpServletResponse response) {
		if(ex instanceof BizException){
			ex.printStackTrace();
			logger.error(ex.getMessage());
		}
		ex.printStackTrace();
		return renderResponse(ex.getMessage());
	}
	public ResponseEntity<?> renderResponse(String msg) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("ContentType", "application/json;charset=UTF-8");
		return new ResponseEntity<Result>(new Result(false,msg,null), headers, HttpStatus.OK);
	}
}