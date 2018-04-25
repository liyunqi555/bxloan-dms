package com.coamctech.bxloan.dms.web.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.coamctech.bxloan.dms.dao.entity.DocumentIndexVO;
import com.coamctech.bxloan.dms.dao.entity.Result;
import com.coamctech.bxloan.dms.service.DmsUploadService;

@RestController
@RequestMapping
@CrossOrigin
public class DmsUploadController {
	@Autowired
	private DmsUploadService dmsUploadService;
	
	/**
	 * 文件上传
	 * @param files
	 * @param vo
	 * @return
	 */
	@PostMapping("uploadFileAction.action")
	public ResponseEntity<?> upload(@RequestParam(value = "file", required = false) MultipartFile[] files,DocumentIndexVO vo){
		HttpHeaders headers = new HttpHeaders();
		headers.set("ContentType", "application/json;charset=UTF-8");
		if(files==null){
			return new ResponseEntity<Result>(new Result(false,"未获取到文件",null), headers, HttpStatus.OK);

		}
		List<MultipartFile> fileList=Arrays.asList(files);
		dmsUploadService.upload(fileList,vo);
		return new ResponseEntity<Result>(new Result(true,"上传成功",null), headers, HttpStatus.OK);

		
	}
}
