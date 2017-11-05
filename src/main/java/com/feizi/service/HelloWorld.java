package com.feizi.service;

import com.feizi.entity.User;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by feizi on 2017/11/4.
 */
@WebService(
        name = "HelloWorld",
        targetNamespace = "http://localhost:8090/ws/helloWorld"
)
public interface HelloWorld {
    @WebMethod
    String sayHi(@WebParam(name = "text") String text);

    @WebMethod
    String sayHiToUser(User user);
}
