package com.example.dubboinit.dubbo.filter;

import com.example.dubboinit.dubbo.exception.ProcessLimitException;
import org.apache.dubbo.rpc.*;

import java.util.concurrent.CompletableFuture;

public class ResponseExceptionFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result result = invoker.invoke(invocation);
        if (result.hasException()) {
            Throwable exception = result.getException();
            if (exception instanceof ProcessLimitException) {
                System.out.println("您已被服务端限流！！！");
                CompletableFuture<AppResponse> b = new CompletableFuture<AppResponse>();
                AsyncRpcResult asyncRpcResult = new AsyncRpcResult(b,invocation);
                asyncRpcResult.setException(new RuntimeException("process in limit"));
                return asyncRpcResult;
            }
        }

        return result;
    }
}
