package com.sxc.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.github.kristofa.brave.*;
import com.github.kristofa.brave.http.HttpSpanCollector;
import com.sxc.dubbo.core.*;


import javax.annotation.PostConstruct;

/**
 * Description:
 *
 * @author: ZMM
 * @version: 1.0
 * Filename:    	BraveDubboFilter
 * Create at:   	2018/3/21
 * <p>
 * Copyright:   	Copyright (c)2018
 * Company:     	songxiaocai
 * <p>
 * Modification History:
 * Date        		      Author          Version      Description
 * ------------------------------------------------------------------
 * 2018/3/21    	          ZMM           1.0          1.0 Version
 */
@Activate(group = {Constants.PROVIDER, Constants.CONSUMER})
public class BraveDubboFilter implements Filter {

    private ClientRequestInterceptor clientRequestInterceptor;

    private ClientResponseInterceptor clientResponseInterceptor;

    private ServerRequestInterceptor serverRequestInterceptor;

    private ServerResponseInterceptor serverResponseInterceptor;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        /*
         * 监控的 dubbo 服务，不纳入跟踪范围
         */
        if ("com.alibaba.dubbo.monitor.MonitorService".equals(invoker.getInterface().getName())) {
            return invoker.invoke(invocation);
        }
        RpcContext context = RpcContext.getContext();
        /*
         * 调用的方法名 以此作为 span name
         */
        String methodName = invocation.getMethodName();
        /*
         * provider 应用相关信息
         */
        if ("0".equals(invocation.getAttachment(DubboTraceConstant.SAMPLED))
                || "false".equals(invocation.getAttachment(DubboTraceConstant.SAMPLED))) {
            return invoker.invoke(invocation);
        }
        //注入
        if (!inject(context)) {
            return invoker.invoke(invocation);
        }
        if (context.isConsumerSide()) {
            clientRequestInterceptor.handle(new DubboClientRequestAdapter(RpcContext.getContext(), "fuck"));
            Result result;
            try {
                result = invoker.invoke(invocation);
            } finally {
                clientResponseInterceptor.handle(new DubboClientResponseAdapter());
            }
            return result;
        } else if (context.isProviderSide()) {
            serverRequestInterceptor.handle(new DubboServerRequestAdapter(RpcContext.getContext(), methodName));
            Result result = null;
            try {
                result = invoker.invoke(invocation);
            } finally {
                serverResponseInterceptor.handle(new DubboServerResponseAdapter());
            }
            return result;
        }
        return invoker.invoke(invocation);
    }

    private boolean inject(RpcContext context) {

        Brave brave = ApplicationBeanHolder.getBean(Brave.class);
       /* Brave brave;
        if (context.isConsumerSide()) {
            brave = new Brave.Builder("consumer").spanCollector(HttpSpanCollector.create("http://localhost:9411", new EmptySpanCollectorMetricsHandler())).build();
        } else {
            brave = new Brave.Builder("provider").spanCollector(HttpSpanCollector.create("http://localhost:9411", new EmptySpanCollectorMetricsHandler())).build();
        }*/
        if (brave == null) {
            return false;
        }
        this.setClientRequestInterceptor(brave.clientRequestInterceptor());
        this.setClientResponseInterceptor(brave.clientResponseInterceptor());
        this.setServerRequestInterceptor(brave.serverRequestInterceptor());
        this.setServerResponseInterceptor(brave.serverResponseInterceptor());
        return true;
    }

    public void setClientRequestInterceptor(ClientRequestInterceptor clientRequestInterceptor) {
        this.clientRequestInterceptor = clientRequestInterceptor;
    }

    public void setClientResponseInterceptor(ClientResponseInterceptor clientResponseInterceptor) {
        this.clientResponseInterceptor = clientResponseInterceptor;
    }

    public void setServerRequestInterceptor(ServerRequestInterceptor serverRequestInterceptor) {
        this.serverRequestInterceptor = serverRequestInterceptor;
    }

    public void setServerResponseInterceptor(ServerResponseInterceptor serverResponseInterceptor) {
        this.serverResponseInterceptor = serverResponseInterceptor;
    }
}

