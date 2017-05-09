package com.gaia.hello.protocol;

import com.gaia.core.asyn.AbstractAsynTask;
import com.gaia.core.asyn.AsynExecutor;
import com.gaia.core.asyn.TaskEvent;
import com.gaia.core.asyn.TaskException;
import com.gaia.hello.model.GithubUserInfo;

import java.io.IOException;

public class GithubUserInfoTask extends AbstractAsynTask {
    public interface IGithubUserInfoTaskListener {
        void onDataObtain(GithubUserInfo info);
    }

    public static void queryUserInfo(String user, IGithubUserInfoTaskListener lis) {
        GithubUserInfoTask task = new GithubUserInfoTask();
        task.mUser = user;
        task.setListener(lis);
        AsynExecutor.defaultExecutor().execute(task);
    }

    private String mUser = null;
    private GithubUserInfo mResultUser = null;
    private IGithubUserInfoTaskListener mListener = null;

    public void setListener(IGithubUserInfoTaskListener lis) {
        mListener = lis;
    }

    @Override
    protected void runWorkThread() {
        try {
            GithubService service = makeService(GithubService.class, "https://api.github.com");
            mResultUser = requestHttpData(service.queryUserInfo(mUser));
            postEventMainThread(new TaskEvent());
        } catch (IOException | TaskException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleEventMainThread(TaskEvent event) {
        if (mListener != null) {
            mListener.onDataObtain(mResultUser);
        }
    }

}
