package com.coamctech.bxloan.dms.conf;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Getter
@Component
public class DmsConfig {
	
	@Value("${dms.config.uploadPath:}")
	private String uploadPath;
	
	@Value("${dms.config.allowTypeLists:}")
	private String allowTypeLists;
	
	@Value("${dms.config.compressMethod}")
	private String compressMethod;
	
	@Value("${dms.config.midBoxWidth:}")
	private String midBoxWidth;
	
	@Value("${dms.config.midBoxHeight:}")
	private String midBoxHeight;
	
	@Value("${dms.config.thuBoxWidth:}")
	private String thuBoxWidth;
	
	@Value("${dms.config.thuBoxHeight:}")
	private String thuBoxHeight;
	
	@Value("${dms.config.uploadTemp}")
	private String documentUploadTmp;
	
	@Value("${dms.config.batchDownShellPath}")
	private String batchDownShellPath;
	
	@Value("${dms.config.zipTargetPath}")
	private String zipTargetPath;
}
