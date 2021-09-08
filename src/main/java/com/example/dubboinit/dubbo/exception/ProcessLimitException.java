package com.example.dubboinit.dubbo.exception;

import org.apache.dubbo.rpc.RpcException;

public class ProcessLimitException extends RpcException {
    public ProcessLimitException(String errMsg) {
        super(errMsg);
    }
}
