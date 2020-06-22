package com.apl;


import com.apl.wms.order.inner.WmsOrderInnerApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(WmsOrderInnerApplication.class)
public class OrderCreateTest {


    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }
        @Test
        public void testMvc () throws Exception {

            RequestBuilder requestBuilder = new RequestBuilder() {
                @Override
                public MockHttpServletRequest buildRequest(ServletContext servletContext) {
                    return null;
                }
            };
            MvcResult mvcResult = mvc.perform(requestBuilder).andReturn();

            System.out.println(mvcResult);

        }

}
