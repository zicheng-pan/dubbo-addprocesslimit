package com.example.dubboinit.dubbo.provider;

import com.example.dubboinit.dubbo.GreetingsService;
import com.example.dubboinit.dubbo.exception.BusinessException;

import java.util.concurrent.ThreadLocalRandom;

public class GreetingsServiceImpl implements GreetingsService {
    @Override
    public String sayHi(String name) {
        // add wait time
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("call imple method");
        return "hi, " + name;
    }
}
