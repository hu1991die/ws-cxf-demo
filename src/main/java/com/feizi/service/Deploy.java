package com.feizi.service;

import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

/**
 * Created by feizi on 2017/11/4.
 */
public class Deploy {
    public static void main(String[] args) throws Exception {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceClass(HelloWorld.class);
        //服务发布的地址
        factory.setAddress("http://localhost:8090/ws/helloWorld");
        factory.setServiceBean(new HelloWorldImpl());
        factory.create();
        System.out.println("wsdl:" + "http://localhost:8090/ws/helloWorld?wsdl");
    }
}
