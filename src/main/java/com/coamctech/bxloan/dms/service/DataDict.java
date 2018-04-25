package com.coamctech.bxloan.dms.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.coamctech.bxloan.dms.dao.entity.Code;
import com.coamctech.bxloan.dms.dao.entity.CodeData;
import com.google.common.collect.Lists;

@Component
public class DataDict {
	
	private static Map<String, LinkedHashMap<String, CodeData>> codes = new HashMap<String, LinkedHashMap<String, CodeData>>();
	
	public void addAll(List<Code> codes) {
		for (Code code : codes) {
			addOne(code);
		}
	}
	
	protected void addOne(Code oneCode) {
		CodeData data = new CodeData();
		BeanUtils.copyProperties(oneCode, data);
		
        String codeType = oneCode.getCodePk().getCodeType();
        LinkedHashMap<String, CodeData> codeMap = codes.get(codeType);
        if (codeMap == null) {
            codeMap = new LinkedHashMap<String, CodeData>();
            codeMap.put(oneCode.getCodePk().getCodeKey(), data);
            codes.put(codeType, codeMap);
        } else {
            codeMap.put(oneCode.getCodePk().getCodeKey(), data);
        }
    }
	
	public Collection<CodeData> getOneType(String codeType) {
		return codes.get(codeType).values();
	}

	public CodeData getUniqueOne(String codeType, String codeKey) {
		return codes.get(codeType).get(codeKey);
	}

	public String getCodeName(String codeType, String codeValue) {
		Collection<CodeData> codeList = this.getOneType(codeType);
		for (CodeData codeData : codeList) {
			if(codeData.getCodeValue().equals(codeValue)) {
				return codeData.getCodeName();
			}
		}
		return "";
	}
	public String getCodeKey(String codeType, String codeValue) {
		LinkedHashMap<String, CodeData> m = codes.get(codeType);
		Iterator<String> it = m.keySet().iterator();
		while(it.hasNext()){
			String codeKey = it.next();
			if(m.get(codeKey).getCodeValue().equals(codeValue)) {
				return codeKey;
			}
		}
		return null;
	}

	
	public String getCodeVal(String codeType, String codeKey) {
		return this.getUniqueOne(codeType, codeKey).getCodeValue();
	}
	
	public List<String> getAllValue(String codeType) {
		Collection<CodeData> cdList =  this.getOneType(codeType);
		List<String> resultVals = Lists.newArrayList();
		if(!CollectionUtils.isEmpty(cdList)){
			for (CodeData cd : cdList) {
				resultVals.add(cd.getCodeValue());
			}
		}else{
			throw new NullPointerException("Can't find this codeType from code");
		}
		return resultVals;
	}
	
	public List<String> getCodeValList(String codeType, String... codeKeys) {
		List<String> resultStrs=Lists.newArrayList();
		if(codeKeys==null){
			return this.getAllValue(codeType);
		}
		for (String key : codeKeys) {
			resultStrs.add(getCodeVal(codeType, key));
		}
		return resultStrs;
	}

	

	

	
	


}
