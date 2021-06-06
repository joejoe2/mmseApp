package com.joejoe2.surveyapp.util;

public interface OnCompleteCallable {
    /**
     * @param msg pass by completed task
     * @param success whether the task has successfully completed
     */
    void doOnComplete(String msg, boolean success);
}
