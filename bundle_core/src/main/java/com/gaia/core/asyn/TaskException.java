package com.gaia.core.asyn;

/**
 * 异步任务中用于抛出的异常信息
 * @author neil
 * @since 2017.05.09
 */
public class TaskException extends Exception {

    public TaskException(String message) {
        super(message);
    }

}
