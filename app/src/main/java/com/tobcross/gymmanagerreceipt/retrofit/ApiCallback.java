package com.tobcross.gymmanagerreceipt.retrofit;

import android.util.Log;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Koo on 2016. 10. 14..
 */

public abstract class ApiCallback<M> extends Subscriber<M> {
    private final static String TAG = "ApiCallback";
    public abstract void onSuccess(M model);
    public abstract void onFailure(String msg);
    public abstract void onFinish();

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.code();
            String msg = httpException.getMessage();
            Log.d(TAG, "code=" + code);
            if (code == 500) {
                msg = "Internal Server Error";
            } else if (code == 404) {
                msg = "Page Not Found";
            }
            onFailure(msg);
        } else {
            onFailure(e.getMessage());
        }
        onFinish();
    }

    @Override
    public void onNext(M model) {
        onSuccess(model);

    }

    @Override
    public void onCompleted() {
        onFinish();
    }
}
