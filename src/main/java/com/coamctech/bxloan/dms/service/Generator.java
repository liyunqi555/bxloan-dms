package com.coamctech.bxloan.dms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Generator {
	
	@Autowired
	private RedisService redisService;
	
	public synchronized Long generateSequenceNumber(final String key,int count){
		if(count<=0) count=1;
		Long newValue = null;
		Long oldValue = null;
		if(redisService.get(key)==null){
			oldValue = 0L;
		}else if (redisService.get(key) instanceof Long) {
			return (Long) redisService.get(key);
		}else{
			Long.parseLong(String.valueOf(redisService.get(key)));
		}
		newValue = new Long((oldValue.longValue()+count+""));
		redisService.set(key, newValue);
		return newValue;
	}

}
