package com.coamctech.bxloan.dms.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.coamctech.bxloan.dms.DmsApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DmsApplication.class)
public class AbstractSpringMvcTest {
	@Autowired
	protected WebApplicationContext context;
	protected MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

	}

}
