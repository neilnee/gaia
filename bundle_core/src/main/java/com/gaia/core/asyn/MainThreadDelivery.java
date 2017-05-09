package com.gaia.core.asyn;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * 主线程投递器
 * @author neil
 * @since 2017.05.09
 */
class MainThreadDelivery {
    private final Executor mEventPoster;

    MainThreadDelivery() {
        final Handler handler = new Handler(Looper.getMainLooper());
        mEventPoster = new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                handler.post(command);
            }
        };
    }

    void postEvent(AbstractAsynTask task, TaskEvent event) {
        mEventPoster.execute(new InnerEventRunnable(task, event));
    }

    @SuppressWarnings("rawtypes")
    private class InnerEventRunnable implements Runnable {
        private final AbstractAsynTask mTask;
        private final TaskEvent mEvent;

        InnerEventRunnable(AbstractAsynTask task, TaskEvent event) {
            mTask = task;
            mEvent = event;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            mTask.onEvent(mEvent);
        }
    }

}
