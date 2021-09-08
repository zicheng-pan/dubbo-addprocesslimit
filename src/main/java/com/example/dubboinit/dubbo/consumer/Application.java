package com.example.dubboinit.dubbo.consumer;

import com.example.dubboinit.dubbo.GreetingsService;
import com.example.dubboinit.dubbo.exception.ProcessLimitException;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

import java.util.concurrent.CountDownLatch;

public class Application {
    private static String zookeeperHost = System.getProperty("zookeeper.address", "127.0.0.1");


    public static void usingAPIREF() {

        ReferenceConfig<GreetingsService> reference = new ReferenceConfig<>();
        reference.setApplication(new ApplicationConfig("first-dubbo-consumer"));
        reference.setRegistry(new RegistryConfig("zookeeper://" + zookeeperHost + ":2181"));
        reference.setTimeout(10000);
        reference.setRetries(0);
        reference.setFilter("myfilter2");
        reference.setInterface(GreetingsService.class);
        GreetingsService service = reference.get();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        for (int i = 0; i < 10; i++) {

            new Thread(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    String message2 = service.sayHi("dubbo");
                    System.out.println(Thread.currentThread().getName() + " getMessage" + message2);
                }catch (Exception e){
                    System.out.println("error");
                }

            }).start();
        }
        countDownLatch.countDown();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("第二次请求，验证限流");
        CountDownLatch countDownLatch2 = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {

            new Thread(() -> {
                try {
                    countDownLatch2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                String message2 = service.sayHi("dubbo");
                System.out.println(Thread.currentThread().getName() + " getMessage" + message2);

                }catch (Exception e){
                    System.out.println("error");
                }
            }).start();
        }

        countDownLatch2.countDown();

    }


    public static void main(String[] args) {
        Application.usingAPIREF();
    }
}
