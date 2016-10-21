package com.example.xzz.rxjava;

import android.os.Bundle;
import android.view.View;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class SchedulerActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
        findViewById(R.id.scheduler).setOnClickListener(this);
        findViewById(R.id.blocking).setOnClickListener(this);
        findViewById(R.id.connectable).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        logText = "";
        int id = view.getId();
        switch (id) {
            case R.id.scheduler:
                scheduler();
                break;
            case R.id.blocking:
                blocking();
                break;
            case R.id.connectable:
                connectable();
                break;
        }
    }

    private void connectable() {
        final ConnectableObservable<Integer> connectableObservable = Observable.just(1)
                .publish();

        Observable.interval(500, TimeUnit.MILLISECONDS)
                .take(3)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        log(aLong.toString() + " 事件已被订阅：");
                        connectableObservable
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Integer>() {
                                    @Override
                                    public void call(Integer aLong) {
                                        log(aLong.toString() + " 事件被触发：");
                                    }
                                });

                        if (aLong == 2) {
                            log("建立连接，发射数据：");
                            connectableObservable.connect();
                        }
                    }
                });
    }

    private void blocking() {
        log("start");
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .subscribeOn(Schedulers.newThread())//无用
                .observeOn(Schedulers.io())//无用，
                .toBlocking()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer aLong) {
                        log(aLong.toString());
                    }
                });
        log("end");
    }

    private void scheduler() {
        Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        log("Observable 线程：");
                        subscriber.onNext(10);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(final Integer aLong) {
                        log("flatmap 1 线程：");
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                log("flatmap 1 Observable：");
                                subscriber.onNext(aLong.toString());
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())//毫无作用
                .observeOn(Schedulers.newThread())
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        log("flatmap 2 线程：");
                        return Observable.just(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log("subscribe 线程：");
                    }
                });
    }

}
