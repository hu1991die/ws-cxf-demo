package com.feizi.service;

import com.feizi.entity.User;

import javax.jws.WebService;

/**
 * Created by feizi on 2017/11/4.
 */
@WebService(
        endpointInterface = "com.feizi.service.HelloWorld",
        serviceName = "HelloWorld",
        targetNamespace = "http://localhost:8090/ws/helloWorld"
)
public class HelloWorldImpl implements HelloWorld {
    @Override
    public String sayHi(String text) {
        System.out.println("sayHi called");
        return "Hello " + text;
    }

    @Override
    public String sayHiToUser(User user) {
        return user.toString();
    }
}
