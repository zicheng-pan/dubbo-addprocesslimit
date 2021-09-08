package com.example.dubboinit.dubbo.filter;

import com.example.dubboinit.dubbo.exception.ProcessLimitException;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;


@Activate(order = 900)
public class BulkheadFilter implements Filter {

    private Bulkhead bulkhead;

    public BulkheadFilter() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("common.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(Integer.parseInt(properties.getProperty("process-limit")))
                .maxWaitDuration(Duration.ofMillis(1))
                .build();

        // Create a BulkheadRegistry with a custom global configuration
        BulkheadRegistry registry = BulkheadRegistry.of(config);

        // Get or create a Bulkhead from the registry -
        // bulkhead will be backed by the default config
        bulkhead = registry.bulkhead("name");
    }


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        CheckedFunction0<Result> checkedRunnable = Bulkhead.decorateCheckedSupplier(bulkhead, () -> invoker.invoke(invocation));

        Try<Result> result = Try.of(checkedRunnable);

        if (result.isSuccess()) {
            System.out.println("not in process limited");
        }

        if (result.isFailure()) {
            //&& result.failed().get() instanceof BulkheadFullException
            System.out.println("in process limited");
            CompletableFuture<AppResponse> b = new CompletableFuture<AppResponse>();
            AsyncRpcResult asyncRpcResult = new AsyncRpcResult(b,invocation);
            asyncRpcResult.setException(new ProcessLimitException("process in limit"));
            return asyncRpcResult;
        }
        return result.get();

    }


}
