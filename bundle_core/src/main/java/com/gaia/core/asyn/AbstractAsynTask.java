package com.gaia.core.asyn;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.gaia.core.net.ProtocolResult;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 异步任务抽象父类, 基本任务单元
 * 提供基础的http接口辅助方法. 包括构建请求, 参数封装等.
 * @author neil
 * @since 2017.05.09
 */
abstract public class AbstractAsynTask implements Runnable, Comparable<AbstractAsynTask> {
    private MainThreadDelivery mDelivery = null;
    private boolean isMarkCancel = false;

    protected AbstractAsynTask() {}

    @Override
    public void run() {
        runWorkThread();
    }

    @Override
    public int compareTo(@NonNull AbstractAsynTask o) {
        return 0;
    }

    protected void postEventMainThread(TaskEvent event) {
        mDelivery.postEvent(this, event);
    }

    void markCancel() {
        isMarkCancel = true;
    }

    void setDelivery(MainThreadDelivery delivery) {
        mDelivery = delivery;
    }

    void onEvent(TaskEvent event) {
        handleEventMainThread(event);
    }

    /**
     * 由实现的具体task重写, 实现需要异步完成的任务操作, 在工作线程中运行
     */
    abstract protected void runWorkThread();

    /**
     * 由实现的具体task重写, 调用postEventMainThread后, 在主线程执行响应代码
     */
    abstract protected void handleEventMainThread(TaskEvent event);

    /**
     * 构建一个基于Retrofit的网络服务接口类
     * 数据格式使用Json
     * @param serviceInterface 服务接口类型class
     * @param baseUrl 请求的url
     * @return 服务接口
     * @throws IllegalArgumentException IllegalArgumentException
     * @throws IllegalStateException IllegalStateException
     */
    protected <T> T makeService(final Class<T> serviceInterface, String baseUrl)
            throws TaskException{
        if (serviceInterface == null || TextUtils.isEmpty(baseUrl)) {
            throw new TaskException("service_interface or base_url is null");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        T s = retrofit.create(serviceInterface);
        if (s == null) {
            throw new TaskException("made null service");
        }
        return s;
    }

    /**
     * 调用基于Retrofit的网络服务接口类Call提供的接口,请求对应的网络数据
     * @param call Retrofit Call
     * @param <T> 返回的数据结构类型, Json格式
     * @return 网络返回的数据结构
     * @throws IllegalArgumentException IllegalArgumentException
     * @throws IOException IOException
     */
    protected <T extends ProtocolResult> T requestHttpData(Call<T> call)
            throws TaskException, IOException {
        if (call == null) {
            throw new TaskException("call is null");
        }
        Response<T> resp = call.execute();
        if (resp == null || resp.body() == null) {
            throw new TaskException("response of call is null");
        }
        return resp.body();
    }

}
