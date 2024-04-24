package com.rookie.middleware.gateway.core.context;

import com.rookie.middleware.gateway.common.rule.Rule;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author eumenides
 * @description 核心上下文基础类
 * @date 2024/4/13
 */
public abstract class BaseContext implements IContext{
    //转发协议，例如http或dubbo等，因为是一开始就决定了，所以加上final
    protected final String protocol;

    //	上下文的状态，因为后面可能涉及到多线程共享，所以加上volatile关键字
    protected volatile int status = IContext.RUNNING;

    protected final ChannelHandlerContext nettyCtx;

    //	保存所有的上下文参数集合
    protected final Map<String, Object> attributes = new HashMap<String, Object>();

    //	在请求过程中出现异常则设置异常对象
    protected Throwable throwable;

    /**
     * 是否保持长连接
     */
    protected final boolean keepAlive;

    //	定义是否已经释放请求资源
    protected final AtomicBoolean requestReleased = new AtomicBoolean(false);

    //	存放回调函数的集合
    protected List<Consumer<IContext>> completedCallbacks;


    public BaseContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive) {
        this.protocol = protocol;
        this.nettyCtx = nettyCtx;
        this.keepAlive = keepAlive;
    }


    @Override
    public void runned() {
        status = GatewayContext.RUNNING;
    }

    @Override
    public void writtened() {
        status = GatewayContext.WRITTEN;
    }

    @Override
    public void completed() {
        status = GatewayContext.COMPLETED;
    }

    @Override
    public void terminated() {
        status = GatewayContext.TERMINATED;
    }

    @Override
    public boolean isRunning() {
        return status == GatewayContext.RUNNING;
    }

    @Override
    public boolean isWrittened() {
        return status == GatewayContext.WRITTEN;
    }

    @Override
    public boolean isCompleted() {
        return status == GatewayContext.COMPLETED;
    }

    @Override
    public boolean isTerminated() {
        return status == GatewayContext.TERMINATED;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T putAttribute(String key, T value) {
        return (T) attributes.put(key, value);
    }


    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }


    @Override
    public ChannelHandlerContext getNettyCtx() {
        return this.nettyCtx;
    }

    @Override
    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    @Override
    public void releaseRequest() {
        this.requestReleased.compareAndSet(false, true);
    }

    @Override
    public void completedCallback(Consumer<IContext> consumer) {
        if(completedCallbacks == null) {
            completedCallbacks = new ArrayList<>();
        }
        completedCallbacks.add(consumer);
    }

    @Override
    public void invokeCompletedCallback() {
        if(completedCallbacks != null) {
            completedCallbacks.forEach(call -> call.accept(this));
        }
    }
}
