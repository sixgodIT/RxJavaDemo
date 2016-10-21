package com.example.xzz.rxjava;

import android.os.Bundle;
import android.view.View;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class ErrorActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        findViewById(R.id.onErrorResumeNext).setOnClickListener(this);
        findViewById(R.id.onErrorReturn).setOnClickListener(this);
        findViewById(R.id.retry).setOnClickListener(this);
        findViewById(R.id.retryWhen).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        logText = "";
        int id = view.getId();
        switch (id) {
            case R.id.onErrorResumeNext:
                onErrorResumeNext();
                break;
            case R.id.onErrorReturn:
                onErrorReturn();
                break;
            case R.id.retry:
                retry();
                break;
            case R.id.retryWhen:
                retryWhen();
                break;
        }
    }

    private void retryWhen() {
        Observable
                .just(1, 2, "A", 3)
                .cast(Integer.class)
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(final Observable<? extends Throwable> observable) {
                        return Observable.timer(1,TimeUnit.SECONDS);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        log("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("抛出错误");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log(integer.toString());
                    }
                });
    }

    private void retry() {
        Observable.just(1, 2, "a", 3)
                .cast(Integer.class)
                .retry(3)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        log(integer.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        log("抛出错误");
                    }
                }, new Action0() {
                    @Override
                    public void call() {

                    }
                });
    }

    private void onErrorReturn() {
        Observable
                .just(1, "A", 3)
                .cast(Integer.class)
                .onErrorReturn(new Func1<Throwable, Integer>() {
                    @Override
                    public Integer call(Throwable throwable) {
                        return 4;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        log(integer.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        log("抛出错误");
                    }
                });
    }


    private void onErrorResumeNext() {
//        onExceptionResumeNext 和 onErrorResumeNext 的区别是只捕获 Exception；
        Observable
                .just(1, "A", 3)
                .cast(Integer.class)
                .onErrorResumeNext(Observable.just(1, 2, 1))
//                .onExceptionResumeNext(Observable.just(1,2,1))
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        log(integer.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        log("抛出错误");
                    }
                });
        ;
    }
}
