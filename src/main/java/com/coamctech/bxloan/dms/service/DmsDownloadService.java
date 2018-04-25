package com.coamctech.bxloan.dms.service;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coamctech.bxloan.dms.commons.exception.BizException;
import com.coamctech.bxloan.dms.conf.DmsConfig;
import com.coamctech.bxloan.dms.utils.CommonHelper;
import com.coamctech.bxloan.dms.utils.FileTool;

@Service
public class DmsDownloadService {
	
	private static Logger logger = LoggerFactory.getLogger(DmsDownloadService.class);
	
	@Autowired
	private DmsConfig dmsConfig;
	
	@Autowired
	private DynamicQuery dynamicQuery;
	
	/**
	 * 单一文件下载
	 * 
	 * @param filepath 
	 * @param filename 
	 * @param response
	 * @throws BizException
	 */
	public void downloadFileJs(String filepath, String filename, HttpServletResponse response, HttpServletRequest request) throws Exception{
			
		File file = new File(filepath);
		if(file.exists() && file.isFile()){
			java.io.BufferedInputStream bis = null;
			java.io.BufferedOutputStream bos = null;
			try {
				response.setContentType("application/x-download");
				
				//继续网络传输，需要进行URLEncoder
				String agent = (String) request.getHeader("USER-AGENT");
				if (agent != null && agent.indexOf("MSIE") == -1) {
					response.setHeader("Content-Disposition","attachment; filename=" +new String(filename.getBytes("utf-8"),"ISO8859_1"));
				} else {
					// IE
					response.setHeader("Content-Disposition","attachment; filename=" +URLEncoder.encode(filename, "UTF8"));
				}
				bis = new java.io.BufferedInputStream(new java.io.FileInputStream(file));
				bos = new java.io.BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}
			} catch (Exception e) {
				logger.error("文件获取异常");
				e.printStackTrace();
				
			} finally {
				try {
					if (bis != null)
						bis.close();
					if (bos != null)
						bos.close();
				} catch (Exception e) {
					logger.error("FileDownloadServlet.downloadFileJs error:" + e.getMessage());
				}
			}
		}else{
			logger.error("文档：" + filename + " 不存在！");				
			return;
		}
	}
	
	/**
	 * 批量文件下载
	 * 
	 * @param filepath 
	 * @param filename 
	 * @param response
	 * @throws BizException
	 */
	public void batchDownloadFileJs(List<Object[]> list, HttpServletResponse response, HttpServletRequest request) throws Exception{
			
		//js文件进行encode之后，网络传输为ISO8859_1，进行反向decode，注意js肯定是utf-8格式
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH24mmSS");
		String zipFileName = sf.format(CommonHelper.getNow());
		String targetPath = dmsConfig.getZipTargetPath()+zipFileName +"/";
		String zipTargetPath = dmsConfig.getZipTargetPath();
		try {
			FileTool.copyFile(list, targetPath , dmsConfig.getUploadPath());
			logger.info("targetPath=="+targetPath+"zipTargetPath=="+zipTargetPath+"zipFileName=="+zipFileName);
			String rsStr = FileTool.fileToZipByShell(dmsConfig.getBatchDownShellPath(),targetPath, zipTargetPath, zipFileName);
			if(!"1".equals(rsStr)){
				logger.error("打包出错！！！");				
				return;
			}
		} catch (Exception e) {
			logger.error("Zip error:" + e.getMessage());
		}finally{
			try {
				//删除待打包文件。
				FileTool.deleteAllFilesOfDir(new File(targetPath));
			} catch (Exception e) {
				logger.error("delete SourceFiles error:" + e.getMessage());
			}
		}
		
		String returnFileName = zipFileName+".zip";
		File file = new File(zipTargetPath+returnFileName);
		if(file.exists() && file.isFile()){
			java.io.BufferedInputStream bis = null;
			java.io.BufferedOutputStream bos = null;
			try {
				response.setContentType("application/x-download");
				//继续网络传输，需要进行URLEncoder
				//response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
				String agent = (String) request.getHeader("USER-AGENT");
				if (agent != null && agent.indexOf("MSIE") == -1) {
					response.setHeader("Content-Disposition","attachment; filename=" +new String(returnFileName.getBytes("utf-8"),"ISO8859_1"));
				} else {
					// IE
					response.setHeader("Content-Disposition","attachment; filename=" +URLEncoder.encode(returnFileName, "UTF8"));
				}
				bis = new java.io.BufferedInputStream(new java.io.FileInputStream(file));
				bos = new java.io.BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}
			} catch (Exception e) {
				logger.error("获取文件异常");
				e.printStackTrace();
			} finally {
				try {
					if (bis != null)
						bis.close();
					if (bos != null)
						bos.close();
					//删除下载压缩包
					FileTool.deleteAllFilesOfDir(file);
				} catch (Exception e) {
					logger.error("FileDownloadServlet.downloadFileJs error:" + e.getMessage());
				}
			}
		}else{
			logger.error("文档：" + returnFileName + " 不存在！");				
			return;
		}
	}
	
	/**
	 * 获取要下载的文档参数
	 * @param documentNums
	 * @return
	 */
	public List<Object[]> getDownloadDocs(String documentNums) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DOCUMENT_NUM,DOCUMENT_NAME,DI.DOCUMENT_ROUTE,c.code_name as DOCUMENT_TYPE");
		sb.append(" FROM document_index di,code c");
		sb.append(" WHERE di.document_id in(").append(documentNums).append(")");
		sb.append(" AND c.code_type = 'DocumentType' AND c.code_value = di.document_type");
		return dynamicQuery.nativeQuery(sb.toString());
	}
}
