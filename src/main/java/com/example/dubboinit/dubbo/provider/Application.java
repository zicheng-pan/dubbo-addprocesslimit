package com.example.dubboinit.dubbo.provider;

import com.example.dubboinit.dubbo.GreetingsService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Application {
    private static String zookeeperHost = System.getProperty("zookeeper.address", "127.0.0.1");

    public static void main(String[] args) throws Exception {
        ServiceConfig<GreetingsService> service = new ServiceConfig<>();
        Map<String, String> map = new HashMap<>();
        map.put("bulkheadlimit", "10");
        service.setParameters(map);
        service.setApplication(new ApplicationConfig("first-dubbo-provider"));
        service.setRegistry(new RegistryConfig("zookeeper://" + zookeeperHost + ":2181"));
        service.setInterface(GreetingsService.class);
//        service.setFilter("-exception");
        service.setFilter("myfilter1");
//        service.setFilter("-exception,myexceptisoonfilter,myfilter");
//        service.setFilter("myexceptionfilter");
        service.setRef(new GreetingsServiceImpl());
        service.export();
        service.setTimeout(10000);

        System.out.println("dubbo service started");
        new CountDownLatch(1).await();
    }
}
