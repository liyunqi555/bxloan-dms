package com.coamctech.bxloan.dms.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coamctech.bxloan.dms.conf.DmsConfig;
import com.coamctech.bxloan.dms.service.DmsDownloadService;

@RestController
@RequestMapping(value={"fileDownloadServlet.servlet","downloadFileAction.action"})
public class DmsDownloadController {
	private static final String CMD = "cmd";
	
	private static final String PATH = "path";

	private static final String FILENAME = "filename";
	
	@Autowired
	private DmsConfig dmsConfig;
	
	@Autowired
	private DmsDownloadService dmsDownloadService;
	
	@RequestMapping
	public void download(HttpServletRequest request,HttpServletResponse response){
		String cmd = request.getParameter(CMD);
		String filepath = dmsConfig.getUploadPath() + request.getParameter(PATH);
		String filename = request.getParameter(FILENAME);
		String documentNums = request.getParameter("DocumentNums");
		
		try{
			if(documentNums!=null&&(!"".equals(documentNums.trim()))){
				List<Object[]> rsList = dmsDownloadService.getDownloadDocs(documentNums);
				dmsDownloadService.batchDownloadFileJs(rsList, response , request);
			}else {
				if(StringUtils.isBlank(cmd) 
						|| StringUtils.isBlank(filepath)
						|| StringUtils.isBlank(filename) ){
					return ;
				}
				dmsDownloadService.downloadFileJs(filepath, filename, response,request);
			}
		}catch(Exception ee){  //任何异常，包括客户端断开的SocketException进行捕获
			ee.printStackTrace();
			System.out.println("FileDownloadAction error:" + ee.getMessage());
		}
	}

}
