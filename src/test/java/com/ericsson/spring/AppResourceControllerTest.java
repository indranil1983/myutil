package com.ericsson.spring;

import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ericsson.spring.controller.AppResourceController;
import com.ericsson.spring.model.EbiUtilResponseBean;
import com.ericsson.spring.service.AppresourceService;

@RunWith(PowerMockRunner.class)
public class AppResourceControllerTest {
	
	@Mock
	private AppResourceController oAppResourceController;
	@Mock
	private AppresourceService appservice;	
	
	@Mock
	HttpServletResponse response;
	
	@Test
	public void testGetDevAppResrcList() throws Exception
	{
		//AppResourceController oAppResourceController =  mock
		EbiUtilResponseBean oEbiUtilResponseBean = oAppResourceController.getDevAppResrcList(response);
		assertTrue(oEbiUtilResponseBean.getErrorList()!=null) ;
	}
	
}
