package com.um.PushSystem;


import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.um.PushSystem.service.push.PushService;
import com.um.PushSystem.service.push.impl.PushServiceImpl;

@Configuration
public class PushSystemCxfConfig {
    @Bean
    public ServletRegistrationBean dispatcherServlet() {
    	CXFServlet cfx = new CXFServlet();
        return new ServletRegistrationBean(cfx, "/PushSystem/*");
    }
    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }
    
    @Bean
    public PushService pushService() {
        return new PushServiceImpl();
    }
    
    @Bean
    public Endpoint endpoint() {
        
    	EndpointImpl endpoint = new EndpointImpl(springBus(), pushService());
        endpoint.publish("/push");
        return endpoint;
    }
}
