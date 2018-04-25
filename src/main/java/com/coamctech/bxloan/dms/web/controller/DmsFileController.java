package com.coamctech.bxloan.dms.web.controller;

import java.io.IOException;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coamctech.bxloan.dms.dao.entity.DocumentIndex;
import com.coamctech.bxloan.dms.service.DmsUploadService;
import com.coamctech.bxloan.dms.service.RedisService;

@RestController
@RequestMapping("file")
public class DmsFileController {
	@Autowired
	private DmsUploadService dmsUploadService;
	
	@Autowired
	private RedisService redisService;
	
	@GetMapping("info/{id}")
	public DocumentIndex getInfo(@PathVariable("id")Long id){
		return dmsUploadService.get(id);
	}
	
	@GetMapping("redisSave/{id}")
	public void redisSave(@PathVariable("id")Long id){
		redisService.set("test", id);
	}
	
	@GetMapping({"redisGet","redisGet2"})
	public void redisGet() throws IOException, InterruptedException, IM4JavaException{
		IMOperation operation = new IMOperation();
		operation.addImage("D:\\dev\\test\\test.jpg");
		operation.resize(300, 300);  
		operation.addImage("D:\\dev\\test\\test_thu3.jpg");

		ConvertCmd cmd = new ConvertCmd();
		cmd.setSearchPath("E:\\Program Files\\ImageMagick-7.0.7-Q16");  //Windows需要设置，Linux不需要
		cmd.run(operation);
		/*Object a  = redisService.get("test");
		System.out.println(a.toString());*/
	}
	
	

}
