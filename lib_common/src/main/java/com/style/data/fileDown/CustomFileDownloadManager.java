package com.style.data.fileDown;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public final class CustomFileDownloadManager {
    public static final String FLAG_NEW_DOWNLOAD = "flag_new_download";
    public static final String FLAG_PAUSE_DOWNLOAD = "flag_pause_download";
    public static final String FLAG_CONTINUE_DOWNLOAD = "flag_continue_download";
    public static final String FLAG_BATCH_DOWNLOAD = "flag_batch_download";

    private HashMap<String, Future> mTaskMap = new HashMap<>();
    private CustomFileDownloadThreadPoolExecutor mThreadPool;
    private static final Object mLock = new Object();
    private static volatile CustomFileDownloadManager mInstance;

    public static CustomFileDownloadManager getInstance() {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new CustomFileDownloadManager();
            }
            return mInstance;
        }
    }

    private CustomFileDownloadThreadPoolExecutor getThreadPool() {
        if (mThreadPool == null)
            mThreadPool = new CustomFileDownloadThreadPoolExecutor();
        return mThreadPool;
    }

    public void addDownloadTask(String tag, SingleFileDownloadTask task) {
        //如果中断并移除旧记录
        stopDownloadTask(tag);
        Future ftask = getThreadPool().submit(task);
        mTaskMap.put(tag, ftask);
    }

    public boolean stopDownloadTask(String tag) {
        boolean isCancelled = false;
        if (!TextUtils.isEmpty(tag) && mTaskMap.containsKey(tag)) {
            Future task = mTaskMap.get(tag);
            if (task != null)
                isCancelled = task.cancel(true);
        }
        removeTask(tag);
        return isCancelled;
    }

    public void removeTask(String tag) {
        mTaskMap.remove(tag);
    }

    public void runTask(String tag, Callable callBack) {
        Future<?> ftask = getThreadPool().submit(callBack);
    }

    public void shutdown() {
        //关闭线程池
        if (mThreadPool != null)
            mThreadPool.shutdown();
    }

    public void shutdownNow() {
        //关闭线程池
        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
            mThreadPool = null;
        }
    }
}
