服务提供方为com.example.dubboinit.dubbo.provider.Application
服务消费方为com.example.dubboinit.dubbo.consumer.Application，其中共发送两批请求，每批请求同时并发10次请求，不会会被限流
配置限流的次数为配置文件common.properties中的process-limit，修改后可以看到同时请求过多的效果