package com.coamctech.bxloan.dms.test.controller;

import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coamctech.bxloan.dms.test.AbstractSpringMvcTest;

public class DmsFileControllerTest extends AbstractSpringMvcTest {
	/*@Test
	public void testInfo() throws Exception {
		mockMvc.perform(
				get("/file/info/18989").accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isOk()).andDo(print());
	}*/
	
	/*@Test
	public void testInfo2() throws Exception {
		mockMvc.perform(
				get("/file/redisSave/18989").accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isOk()).andDo(print());
	}*/
	
	@Test
	public void testInfo2() throws Exception {
		mockMvc.perform(
				get("/file/redisGet2").accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isOk()).andDo(print());
	}

}
