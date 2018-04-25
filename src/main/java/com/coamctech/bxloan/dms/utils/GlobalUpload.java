package com.coamctech.bxloan.dms.utils;

import java.util.HashMap;

public class GlobalUpload {
	public static HashMap<String, String> uploadmap = null;
	
	private static Long documentSerial = 0L;
	
	public static Long getDocumentSerial(){
		Long documentSerial = GlobalUpload.documentSerial;
		GlobalUpload.documentSerial++;
		return documentSerial;
	}
}
