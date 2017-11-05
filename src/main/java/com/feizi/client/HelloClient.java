package com.feizi.client;

import com.feizi.entity.User;
import com.feizi.service.HelloWorld;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by feizi on 2017/11/4.
 */
public final class HelloClient {
    private HelloClient(){

    }
    public static void main(String[] args) {
        /* 使用Spring的方式，服务器端可以直接使用tomcat方式启动也可以在main方法中使用内嵌的jetty容器启动*/
        ClassPathXmlApplicationContext context
                = new ClassPathXmlApplicationContext(new String[] {"classpath:conf/client-beans.xml"});
        HelloWorld client = (HelloWorld)context.getBean("helloClient");
        String response = client.sayHi("feizi");
        System.out.println("Response: " + response);

        String response1 = client.sayHiToUser(new User("feizi", 13, "xili"));
        System.out.println("Response: " + response1);
        System.exit(0);

        /*普通方式调用*/
        /*JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(HelloWorld.class);
        factory.setAddress("http://localhost:8090/ws-cxf-demo/ws/HelloWorld");
        HelloWorld helloWorld = factory.create(HelloWorld.class);
        String welcome = helloWorld.sayHi("feizi@qq.com");
        System.out.println(welcome);*/
    }
}
