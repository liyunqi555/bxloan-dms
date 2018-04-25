package com.coamctech.bxloan.dms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.coamctech.bxloan.dms.service.DataDict;
import com.coamctech.bxloan.dms.service.DataDictLoader;

@Component
@Order(1)
public class DmsDataInitRunner implements CommandLineRunner {
	private Logger logger = LoggerFactory
			.getLogger(getClass());

	@Autowired
	private DataDictLoader dataDictLoader;

	@Autowired
	private DataDict dataDict;

	@Override
	public void run(String... args) throws Exception {
		logger.info("*******************初始化字典数据**********************************");
		/**
		 * 初始化字典表加载
		 */
		dataDictLoader.preLoad();
		logger.info("*******************初始化字典数据 End**********************************");
	}
}
