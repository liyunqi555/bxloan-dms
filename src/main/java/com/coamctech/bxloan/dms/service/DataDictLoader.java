package com.coamctech.bxloan.dms.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coamctech.bxloan.dms.dao.entity.Code;
import com.coamctech.bxloan.dms.dao.entity.CodeData;
import com.google.common.collect.Lists;
@Service
public class DataDictLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataDictLoader.class);
	@Autowired
	private DynamicQuery dynamicQuery;
	
	@Autowired
	private DataDict dataDict;
	
	@SuppressWarnings("unchecked")
	public void preLoad() {
		LOGGER.info("************数据库查询***********************");
		String jpql = "select c from Code c where c.usableStatusInd = '1' order by c.orderNumber";
		List<Code> codes = (List<Code>) dynamicQuery.query(jpql);
		dataDict.addAll(codes);
		
	}
	public Collection<CodeData> getOneType(String codeType) {
		return dataDict.getOneType(codeType);
	}
	public String getCodeName(String codeType, String codeValue) {
		return dataDict.getCodeName(codeType, codeValue);
	}

	public Collection<CodeData> getOneType(String codeType, String[] codeKeys) {
		Collection<CodeData> codeList = Lists.newArrayList();
		for (CodeData codeData : this.getOneType(codeType)) {
			if(Arrays.asList(codeKeys).contains(codeData.getCodeValue())){
				codeList.add(codeData);
			}
		}
		return codeList;
	}

	public String getCodeVal(String codeType, String codeKey) {
		return dataDict.getCodeVal(codeType, codeKey);
	}
}
