### CXF方式实现webservice接口
### 代码结构
```
|-src/main/java
   |-com.feizi
      |-client
          |-HelloClient
      |-entity
          |-User
      |-service
          |-Deploy
          |-HelloWorld
          |-HelloWorldImpl
|-src/main/resources
    |-conf
       |-applicationContext.xml
       |-client-beans.xml
       |-cxf-servlet.xml
|-webapp
    |-WEB-INF
        |-web.xml
    |-index.jsp
|-src/test/java
|-src/test/resources
```

### 层次说明
```
HelloClient：客户端，测试webservice接口是否正常
User：实体类，可作为webservice复杂类型参数
Deploy：发布webservice接口类（与cxf-servlet.xml功能类似，二者选其一）
HelloWorld：需要发布暴露出去的webservice接口顶层定义
HelloWorldImpl：webservice接口实现类
applicationContext.xml：集成spring时的配置文件
client-beans.xml：使用spring注入需要配置的bean依赖xml文件
cxf-servlet.xml：使用tomcat外置容器发布接口时需要配置的文件（如果不用外置tomcat启动，可以直接在Deploy类中配置，然后使用内置jetty容器方式启动即可）
```

### 使用说明
1、定义发布的服务接口**HelloWorld**
```
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
```

2、定义接口实现类**HelloWorldImpl**
```
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
```

3、发布服务接口**Deploy**
```
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
```

4、运行Deploy的main方法，将接口服务暴露出去，并且使用[http://localhost:8090/ws/helloWorld?wsdl](http://localhost:8090/ws/helloWorld?wsdl) 
这个地址可以查看webservice暴露出去的接口定义

到这里其实就已经完成了。下面是集成spring的方式

1、如果要集成Spring，则首先**pom.xml**文件引入spring相关依赖
```
<dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-web</artifactId>
      <version>${spring.version}</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>${spring.version}</version>
    </dependency>
```

2、编写spring的核心配置文件**applicationContext.xml**文件
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context-4.1.xsd">

    <context:annotation-config/>
    <import resource="classpath:conf/cxf-servlet.xml"/>
</beans>
```

3、**web.xml**文件中初始化spring容器以及配置webservice接口的访问路径
```
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:conf/applicationContext.xml</param-value>
  </context-param>

  <listener>
    <listener-class>
      org.springframework.web.context.ContextLoaderListener
    </listener-class>
  </listener>

  <servlet>
    <servlet-name>CXFServlet</servlet-name>
    <servlet-class>org.apache.cxf.transport.servlet.CXFServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>

  <servlet-mapping>
    <servlet-name>CXFServlet</servlet-name>
    <url-pattern>/ws/*</url-pattern>
  </servlet-mapping>
```

4、使用CXF方式暴露webservice接口需要配置**cxf-servlet.xml**文件（备注：如果使用这种方式，则直接使用外置tomcat启动即可，启动完成可以直接查看wsdl访问地址）
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:jaxws="http://cxf.apache.org/jaxws" xsi:schemaLocation=" http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.2.xsd http://cxf.apache.org/jaxws http://cxf.apache.org/schemas/jaxws.xsd">
    <import resource="classpath:META-INF/cxf/cxf.xml"/>
    <import resource="classpath:META-INF/cxf/cxf-servlet.xml"/>
    <!--<jaxws:endpoint id="helloWorld" implementor="com.feizi.service.HelloWorldImpl" address="/HelloWorld"/>-->

    <!--If you want to reference a spring managed-bean, you can write like this:-->
    <bean id="helloWorldImpl" class="com.feizi.service.HelloWorldImpl" />
    <jaxws:endpoint id="helloWorld" implementor="#helloWorldImpl" address="/HelloWorld" />
</beans>
```

5、使用spring方式注入服务接口，以便测试**client-beans.xml**
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:jaxws="http://cxf.apache.org/jaxws"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
http://cxf.apache.org/jaxws http://cxf.apache.org/schemas/jaxws.xsd">

    <jaxws:client id="helloClient"
                  serviceClass="com.feizi.service.HelloWorld"
                  address="http://localhost:8090/ws-cxf-demo/ws/HelloWorld" />
</beans>
```

6、编写**HelloClient**测试类对webservice接口进行测试，看接口是否正常
```
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
```

可以看到上面有两种方式，第一种方式是使用spring的方式(在client-beans.xml文件种注入接口信息)进行处理的，第二种则是通过普通方式！





