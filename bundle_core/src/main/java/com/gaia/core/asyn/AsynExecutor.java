package com.gaia.core.asyn;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务执行器
 * @author neil
 * @since 2017.05.08
 */
public class AsynExecutor {
    private static final int DEFAULT_POOL_SIZE_CORE = 10;
    private static final int DEFAULT_POOL_SIZE_MAX = 20;
    private static final int DEFAULT_POOL_KEEP_ALIVE = 2;

    private static AsynExecutor DEFAULT_EXECUTOR = null;
    private static final Object DEFAULT_SYNC_OBJ = new Object();

    /**
     * 获取默认的异步任务执行器
     */
    public static AsynExecutor defaultExecutor() {
        if (DEFAULT_EXECUTOR == null) {
            synchronized (DEFAULT_SYNC_OBJ) {
                if (DEFAULT_EXECUTOR == null) {
                    DEFAULT_EXECUTOR = new AsynExecutor(DEFAULT_POOL_SIZE_CORE, DEFAULT_POOL_SIZE_MAX, DEFAULT_POOL_KEEP_ALIVE);
                }
            }
        }
        return DEFAULT_EXECUTOR;
    }

    public static void shutdownAll() {
        synchronized (DEFAULT_SYNC_OBJ) {
            if (DEFAULT_EXECUTOR != null) {
                DEFAULT_EXECUTOR.shutdown();
                DEFAULT_EXECUTOR = null;
            }
        }
    }

    private final PriorityBlockingQueue<Runnable> mWaitingQueue = new PriorityBlockingQueue<>();
    private final ArrayList<AbstractAsynTask> mWorkingQueue = new ArrayList<>();
    private final InnerThreadPool mThreadPool;
    private final MainThreadDelivery mDeliveryMain = new MainThreadDelivery();

    private AsynExecutor(int corePoolSize, int maximumPoolSize, int keepAliveTime) {
        mThreadPool = new InnerThreadPool(corePoolSize, maximumPoolSize, keepAliveTime);
    }

    public void execute(@NonNull AbstractAsynTask task) {
        task.setDelivery(mDeliveryMain);
        mThreadPool.execute(task);
    }

    public void cancle(AbstractAsynTask task) {
        mWaitingQueue.remove(task);
    }

    public void cancelAllTask() {
        mWaitingQueue.clear();
        synchronized (mWorkingQueue) {
            for (AbstractAsynTask task : mWorkingQueue) {
                task.markCancel();
            }
        }
    }

    public void shutdown() {
        cancelAllTask();
        if (!mThreadPool.isShutdown()) {
            mThreadPool.shutdown();
        }
    }

    private class InnerThreadPool extends ThreadPoolExecutor {

        InnerThreadPool(int corePoolSize, int maximumPoolSize, int keepAliveTime) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES, mWaitingQueue);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            synchronized (mWorkingQueue) {
                if (r instanceof AbstractAsynTask) {
                    mWorkingQueue.add((AbstractAsynTask)r);
                }
            }
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            synchronized (mWorkingQueue) {
                if (r instanceof AbstractAsynTask) {
                    mWorkingQueue.remove(r);
                }
            }
        }

    }

}
