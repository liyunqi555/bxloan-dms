package com.coamctech.bxloan.dms.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.coamctech.bxloan.dms.commons.exception.BizException;
import com.coamctech.bxloan.dms.conf.DmsConfig;
import com.coamctech.bxloan.dms.dao.entity.DocumentIndex;
import com.coamctech.bxloan.dms.dao.entity.DocumentIndexVO;
import com.coamctech.bxloan.dms.dao.repo.DocumentIndexRepo;
import com.coamctech.bxloan.dms.utils.CommonHelper;
import com.coamctech.bxloan.dms.utils.FileUtil;
import com.coamctech.bxloan.dms.utils.GlobalUpload;
import com.coamctech.bxloan.dms.utils.ImportFileManager;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.NativeStorage;
import de.innosystec.unrar.rarfile.FileHeader;

@Service
public class DmsUploadService {
	private Logger logger = LoggerFactory.getLogger(DmsUploadService.class);
	@Autowired
	private DmsConfig dmsConfig;
	@Autowired
	private DocumentIndexRepo documentIndexRepo;
	@Autowired
	private DataDict dataDict;
	@Autowired
	private DynamicQuery dynamicQuery;
	@Autowired
	private Generator generator;
	@Autowired
	private ImageMagickService imageMagickService;
	
	public DocumentIndex get(Long id){
		return documentIndexRepo.findOne(id);
	}
	
	/**
	 * 文件上传方法
	 * @param file
	 * @param fileName
	 * @param documentIndexVO
	 * @throws BizException
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void upload(List<MultipartFile> fileList,DocumentIndexVO documentIndexVO) throws BizException{
		fileList.forEach(f->{
			if(null == f){
				throw new BizException("文件为空！");
			}
			if(null == documentIndexVO){
				throw new BizException("丢失相关文档信息，上传文档失败！");
			}
			//获取文件名称
			String fileName = f.getOriginalFilename();
			if(StringUtils.isBlank(fileName)){
				throw new BizException("文件名称为空！");
			}
			logger.info("上传文件：{} 上传时间：{}",fileName,CommonHelper.getNowStr(CommonHelper.DF_DATE_TIME));
			String[] fileSplit = fileName.split("\\.");
			String fileType = fileSplit[fileSplit.length-1];
			if(!StringUtils.isEmpty(fileType)){
				fileType=fileType.toLowerCase();
			}
			//校验文件类型
			String allowTypeLists = dmsConfig.getAllowTypeLists();
			if(!allowTypeLists.contains(fileType)){
				throw new BizException("文件类型错误！可以上传的文件类型为：" + allowTypeLists);
			}
			if(dataDict.getCodeName("FileType", "9").equals(fileType)){
				/**
				 * RAR 文件处理
				 */
				this.dealRarDoc(f, fileName, documentIndexVO);
			}else{
				/**
				 * 普通文件处理
				 */
				documentIndexVO.setFileType(fileType);
				this.dealNormalDoc(f, fileName, documentIndexVO);
			}
		});
	}
	
	
	/**
	 * 处理普通文件
	 * @param infile 
	 * @param fileFileName
	 * @param documentIndexVO
	 */
	public void dealNormalDoc(MultipartFile infile,String fileFileName,DocumentIndexVO documentIndexVO){
		//获取文件名称
		String[] fileNameSplit = fileFileName.split("\\\\");
		String fileName = fileNameSplit[fileNameSplit.length-1];
		DocumentIndex documentIndex = new DocumentIndex();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
		BeanUtils.copyProperties(documentIndexVO, documentIndex, "subcontractId");			
		String documentNameNum = "";
		try {
			//文件名称编号
			documentNameNum = this.getDocumentNameNum(documentIndexVO.getCustomerNum());
			//生成文档编号						
			String documentNum = this.generateDocumentNum(documentIndexVO.getCustomerNum(), documentIndexVO.getDocumentType());
			documentIndex.setDocumentNum(documentNum);
		} catch (Exception e) {
			throw new BizException(e.getMessage(), e);
		}
		//文档路径：年月/客户编号/文件名称编号.文件类型
		String documentRoute = sf.format(new Date()) + "/" + documentIndexVO.getCustomerNum() + "/" + documentNameNum + "." + documentIndexVO.getFileType();
		documentIndex.setDocumentName(fileName);
		documentIndex.setDocumentRoute(documentRoute);	
		//1:创建
		documentIndex.setCreateTypeCd(dataDict.getCodeVal("CreateType", "S1"));
		//1:是
		documentIndex.setStatus(dataDict.getCodeVal("CtrlIndicator", "S1"));
		//创建日期：默认当前时间
		documentIndex.setCreateDateTime(CommonHelper.getNow());
		//系统更新时间：默认当前时间
		documentIndex.setSystemUpdateTime(CommonHelper.getNow());
		//新增文档记录并上传单个文件
		this.saveDoceument(documentIndex, infile);
	}
	
	/**
	 * 处理rar文件
	 * 1.生成临时文件
	 * 2.上传rar文件并批量新增文档记录
	 * @param infile
	 * @param fileName
	 * @param documentIndexVO
	 */
	public void dealRarDoc(MultipartFile infile,String fileName,DocumentIndexVO documentIndexVO){
		String[] fileNameSplit = fileName.split("\\\\");
		String filefileName = fileNameSplit[fileNameSplit.length-1];
		this.createDir(dmsConfig.getDocumentUploadTmp());
		SimpleDateFormat formater = new SimpleDateFormat( "yyyyMMddHH24mmSS" );
        String strDateTime = formater.format(CommonHelper.getNow());// 得到日期时间	
		File newfile = new File(dmsConfig.getDocumentUploadTmp(), strDateTime + filefileName);
		//此处创建一个临时文件存放rar文件，临时文件有效时间为10分钟
		String fileId = this.generateFileId();
		try {
			infile.transferTo(newfile);
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
			if(null != newfile){
				newfile.delete();
			}
			throw new BizException("校验rar文件出错！", e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			if(null != newfile){
				newfile.delete();
			}
			throw new BizException("校验rar文件出错！", e1);
		}
		ImportFileManager.addFile(fileId, newfile);
		DocumentIndex documentIndex = new DocumentIndex();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
		BeanUtils.copyProperties(documentIndexVO, documentIndex, "subcontractId");	
		String documentRoute = sf.format(new Date()) + "/" + documentIndexVO.getCustomerNum() + "/";
		documentIndex.setDocumentRoute(documentRoute);	
		/**
		 * 关联方式（创建类型），1-创建
		 */
		documentIndex.setCreateTypeCd(dataDict.getCodeVal("CreateType", "S1"));
		/**
		 * 文档状态，1-有效
		 */
		documentIndex.setStatus(dataDict.getCodeVal("CtrlIndicator", "S1"));
		//创建日期：默认当前时间
		documentIndex.setCreateDateTime(CommonHelper.getNow());
		//系统更新时间：默认当前时间
		documentIndex.setSystemUpdateTime(CommonHelper.getNow());
		try {
			documentIndex.setCreateDateTime(CommonHelper.formatDate(CommonHelper.getNow(), CommonHelper.DF_DATE_TIME));
			documentIndex.setSystemUpdateTime(CommonHelper.formatDate(CommonHelper.getNow(), CommonHelper.DF_DATE_TIME));
		} catch (Exception e) {
			throw new BizException("上传文档失败！", e);
		}
		//上传rar文件并批量新增文档记录
		this.createRarDocument(documentIndex, fileId);
	
	}
	
	/**
	 * 存储文件操作
	 * 1.file写入本地 2.参数入库
	 * @param documentIndex
	 * @param file
	 * @throws BizException
	 */
	private void saveDoceument(DocumentIndex documentIndex,MultipartFile file) throws BizException{
		//校验文档是否已存在
		if(this.checkDocumentExist(documentIndex)){
			throw new BizException("文件名:["+documentIndex.getDocumentName()+"]已经存在,不能再次上传同名文件!!!");
		}
		if(null != documentIndex){
			File dir = null;
			File newfile = null;
			String documentRoute = documentIndex.getDocumentRoute();
			//创建目录
			String path = dmsConfig.getUploadPath() + documentRoute.substring(0, documentRoute.lastIndexOf("/"));
			dir = new File(path);
			if (!dir.exists() || !dir.isDirectory()) {
				dir.mkdirs();
			}
			//创建文件
			String documentName = documentRoute.substring(documentRoute.lastIndexOf("/")+1, documentRoute.length());
			newfile = new File(dir, documentName);
			try {
				//文件落地
				file.transferTo(newfile);
			} catch (IllegalStateException e) {
				//抛出异常将落地文件删除
				if(newfile.exists()){
					newfile.delete();
				}
				e.printStackTrace();
				throw new BizException("上传文件失败");
			} catch (IOException e) {
				//抛出异常将落地文件删除
				if(newfile.exists()){
					newfile.delete();
				}
				e.printStackTrace();
				throw new BizException("上传文件失败");
				
			}
			//jpg,png,gif文件要进行压缩处理
			if(Arrays.asList(dataDict.getCodeName("FileType", "6"),dataDict.getCodeName("FileType", "7"),dataDict.getCodeName("FileType", "8"))
					.contains(documentIndex.getFileType())){
				String oldFilePath = newfile.getPath();
				imageMagickService.dealImage(oldFilePath);
			}
			documentIndexRepo.save(documentIndex);
		}else{
			throw new BizException("丢失相关文档信息，上传文档失败！");
		}
	}
	
	
	/**
	 * 校验文档是否已存在
	 * @param documentIndex
	 * @return
	 */
	private Boolean checkDocumentExist(DocumentIndex documentIndex){
		StringBuffer sql = new StringBuffer("select document_id from document_index where 1=1 and status = '1' ");
		List<Object> params = new ArrayList<Object>();
		if(documentIndex.getPartyId() !=null && documentIndex.getPartyId().intValue()!=-1){
			params.add(documentIndex.getPartyId());
		    sql.append(" and party_id = ?").append(params.size()); 
		}
		if(documentIndex.getBizId() !=null && documentIndex.getBizId().intValue()!=-1){
			params.add(documentIndex.getBizId());
			sql.append(" and biz_id = ?").append(params.size());
		}
		if(!StringUtils.isEmpty(documentIndex.getBizNum())){
			params.add(documentIndex.getBizNum());
			sql.append(" and biz_num = ?").append(params.size());
		}
		if(!StringUtils.isEmpty(documentIndex.getDocumentType())){
			params.add(documentIndex.getDocumentType());
			sql.append(" and document_type = ?").append(params.size());
		}
		if(!StringUtils.isEmpty(documentIndex.getDocumentName())){
			params.add(documentIndex.getDocumentName());
			sql.append(" and document_name = ?").append(params.size()); 
		}
		if(!StringUtils.isEmpty(documentIndex.getCreateUserNum())){
			params.add(documentIndex.getCreateUserNum());
			sql.append(" and create_user_num = ?").append(params.size());
		}
		Long count = dynamicQuery.nativeQueryCount(sql.toString(), params.toArray());
		if(count.compareTo(0L)>0){			
			return true;
		}
		return false;
	}
	
	/**
	 * 创建目录
	 * @param dir
	 */
	public void createDir(String dir){
		if(!StringUtils.isEmpty(dir)){
			
			File file = new File(dir);
			if(!file.exists()){
				
				file.mkdir();
			}else if(!file.isDirectory()){
				
				file.mkdir();
			}
		}
	}
	
	/**
	 * 获取文档编号
	 * 文档编号生成规则：文档编号25位=客户编号+文档类型+年月日+【补齐0】+序号
	 */
	public String getDocumentNameNum(String customerNum){
		if(!StringUtils.isEmpty(customerNum)){
			SimpleDateFormat formater = new SimpleDateFormat( "yyMMdd" );
	        String strDateTime = formater.format(CommonHelper.getNow());// 得到日期时间	
			String key = customerNum + strDateTime;
			Long num = generator.generateSequenceNumber(key, 1);
			return key +  "000000".substring( 0, 6 - num.toString().length()) + num.toString();
		}else{
			throw new BizException("客户编号为空，生成文件名称编号异常！");
		}
		
	}
	
	/**
	 * 生成文件名称编号
	 * 文件名称编号生成规则：文件名称编号25位=客户编号+年月日+【补齐0】+序号
	 */
	public String generateDocumentNum(String customerNum, String documentType) throws BizException{
		
		if(!StringUtils.isEmpty(customerNum) && !StringUtils.isEmpty(documentType)){
			SimpleDateFormat formater = new SimpleDateFormat( "yyMMdd" );
	        String strDateTime = formater.format(CommonHelper.getNow());// 得到日期时间	
	        String key = customerNum + documentType + strDateTime;
	        Long num = generator.generateSequenceNumber(key, 1);
			return key + "0000".substring( 0, 4 - num.toString().length()) + num.toString();
		}else{
			throw new BizException("客户编号、文档类型为空，生成文档编号异常！");
		}
	}
	
	/**
	 * 生成临时文件ID
	 */
	public String generateFileId(){
		// 格式化当前日期
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH24mmSS");
		//获取SessionId
		Long sessionId = GlobalUpload.getDocumentSerial();
		return sessionId + "#" + simpleDateFormat.format(new Date());
	}
	
	/**
	 * 上传[RAR]文件调用的后台处理方法
	 * 1. 文件落地
	 * 2. 业务数据入库
	 * @param infile
	 * @param documentIndex
	 * @param fileId
	 * @throws BizException
	 */
	public void createRarDocument(DocumentIndex documentIndex, String fileId) throws BizException {
		List<DocumentIndex> documentIndexList = null;
		try {
			documentIndexList = this.uploadRarFile(documentIndex, fileId);
			this.addDocumentIndexAll(documentIndexList);
		} catch (BizException e) {
			this.deleteFileAll(documentIndexList);
			throw new BizException(e.getMessage());
		}
	}
	
	/**
	 * 业务数据入库（批量）
	 * <pre>
	 * 进行文档同名校验
	 * </pre>
	 */
	public void addDocumentIndexAll(List<DocumentIndex> documentIndexList) throws BizException{
		documentIndexList.forEach(d->{
			if(this.checkDocumentExist(d)){
				throw new BizException("文件名:["+d.getDocumentName()+"]已经存在,不能再次上传同名文件!!!");
			}
		});
		documentIndexRepo.save(documentIndexList);
	}
	
	/**
	 * 删除物理文件
	 * 
	 */
	public void deleteFileAll(List<DocumentIndex> documentIndexList) throws BizException{
		if(null != documentIndexList && documentIndexList.size() > 0){
			DocumentIndex documentIndex = null;
			for (int i = 0, j = documentIndexList.size(); i < j; i++) {
				documentIndex = documentIndexList.get(i);
				if(null != documentIndex){
					//删除目录（文件夹）以及目录下的文件
					FileUtil.deleteDirectory(dmsConfig.getUploadPath() + documentIndex.getDocumentRoute());
				}
				
			}
		}
	}
	
	/**
	 * [RAR]文件落地
	 * @param documentIndex
	 * @param fileId
	 * @throws BizException
	 */
	public List<DocumentIndex> uploadRarFile(DocumentIndex documentIndex, String fileId) throws BizException {
		List<DocumentIndex> documentIndexList = new ArrayList<DocumentIndex>();
		Archive a = null;
		FileOutputStream fos = null;
		File dir = null;
		File file = null;
		File tmpFile = null;
		try {
			tmpFile = ImportFileManager.getFileByFileId(fileId);
			a = new Archive(new NativeStorage(tmpFile));
			FileHeader fh = a.nextFileHeader();
			while (fh != null) {
				if(!fh.isDirectory()){
					//生成文档编号
					String customerNum = documentIndex.getCustomerNum();
					String documentType = documentIndex.getDocumentType();
					String documentNum = this.generateDocumentNum(customerNum,documentType);
					//获取文件名称
					String filename=null;
					if (fh.isUnicode()) {// Unicode文件名使用getFileNameW
						filename = fh.getFileNameW().trim();
					} else {
						filename = fh.getFileNameString().trim();
					}
					String[] fileNameArr = filename.split("\\\\");
					String fileName = fileNameArr[fileNameArr.length-1];
					//获取文件类型
					String[] fileTypeArr = filename.split("\\.");
					String fileType = fileTypeArr[fileTypeArr.length-1];
					//生成文件名称编号
					String documentNameNum = this.getDocumentNameNum(customerNum);
					//获取要入库的文档信息
					DocumentIndex document = new DocumentIndex();
					BeanUtils.copyProperties(documentIndex, document);
					document.setDocumentNum(documentNum);
					document.setDocumentName(fileName);
					document.setDocumentRoute(documentIndex.getDocumentRoute() + documentNameNum + "." + fileType);
					document.setFileType(fileType);
					documentIndexList.add(document);
					//创建目录
					dir = new File(dmsConfig.getUploadPath() + 
							documentIndex.getDocumentRoute().substring(0, documentIndex.getDocumentRoute().length()-1));
					if (!dir.exists() || !dir.isDirectory()) {
						dir.mkdirs();
					}
					//创建文件
					file = new File(dir, documentNameNum + "." + fileType);
					//解压缩文件
					fos = new FileOutputStream(file);
					a.extractFile(fh, fos);
					fos.close();
					fos = null;
					String oldFilePath = file.getPath();
					String fileLastName = oldFilePath.substring(oldFilePath.indexOf("."));//文件后缀名
					//文件若为jpg,png,gif生成 缩略图 和中图 
					if(Arrays.asList(".jpg",".png",".gif").contains(fileLastName)){
						imageMagickService.dealImage(oldFilePath);
					}
				}
				fh = a.nextFileHeader();
			}
			a.close();
			a = null;
			return documentIndexList;
		} catch (Exception e) {
			e.printStackTrace();
			if(null != file){
				file.delete();
			}
			if(null != dir){
				dir.delete();
			}
			throw new BizException("上传文档失败！", e);
		} finally {
			if(null != tmpFile){
				tmpFile.delete();
			}
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (Exception e) {
					throw new BizException("上传文档失败！", e);
				}
			}
			if(a != null){
				try{
					a.close();
					a = null;
				}catch(Exception e){
					throw new BizException("上传文档失败！", e);
				}
			}
		}
	}
	
}
