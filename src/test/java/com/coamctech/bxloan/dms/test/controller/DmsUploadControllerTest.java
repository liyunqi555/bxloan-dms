package com.coamctech.bxloan.dms.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.coamctech.bxloan.dms.test.AbstractSpringMvcTest;

public class DmsUploadControllerTest extends AbstractSpringMvcTest {
	
	@Test
	public void testUploadFile() throws Exception{
		File file=new File("D:\\dev\\test\\历史逾期记录更新删除报文接口规范.doc");
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload")
				.file(new MockMultipartFile("file", file.getName(), "application/octet-stream", new FileInputStream(file)))
				.param("allDocType", "017")
				.param("customerNum", "118012300019")
				.param("documentType", "02")
				.param("bizId", "53123")
				.param("bizNum", "Y106011803020003")
				)
		.andDo(print());
	}
}
