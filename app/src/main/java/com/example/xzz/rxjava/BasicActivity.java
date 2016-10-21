package com.example.xzz.rxjava;

import android.os.Bundle;
import android.view.View;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 基础用法
 */
public class BasicActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        findViewById(R.id.create).setOnClickListener(this);
        findViewById(R.id.from).setOnClickListener(this);
        findViewById(R.id.just).setOnClickListener(this);
        findViewById(R.id.timer).setOnClickListener(this);
        findViewById(R.id.interval).setOnClickListener(this);
        findViewById(R.id.range).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        logText = "";
        int id = view.getId();
        switch (id) {
            case R.id.create:
                create();
                break;
            case R.id.from:
                from();
                break;
            case R.id.just:
                just();
                break;
            case R.id.timer:
                timer();
                break;
            case R.id.interval:
                interval();
                break;
            case R.id.range:
                range();
                break;
        }
    }

    private void range() {
        Observable.range(2, 5).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                log(integer.toString());// 2,3,4,5,6 从2开始发射5个数据
            }
        });

    }

    private void interval() {
        //第一个参数，延时。第二个参数间隔
        Observable.interval(1000, 500, TimeUnit.MILLISECONDS)
                .take(App.getInstance().weeks.length)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        return App.getInstance().weeks[aLong.intValue()];
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        //每隔1秒发送数据项，从0开始计数
                        //0,1,2,3....
                        log(s);
                    }
                });
    }

    private void timer() {
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        log(aLong.toString()); // 0
                    }
                });
    }

    private void just() {
        Observable.just("a", "b", "c")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log(s);
                    }
                });
    }

    private void from() {
        Observable.from(App.getInstance().weeks)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log(s);
                    }
                });
    }

    private void create() {
        log("onClick: ");
        //第一步：创建被观察者
        //第二步：创建观察者
        //第三步：订阅
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        log("call: ");
                        subscriber.onNext("Hello");
                        subscriber.onNext("Hi");
                        subscriber.onNext("Aloha");
                        subscriber.onCompleted();
                    }

                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<?>>() {
                    @Override
                    public Observable<?> call(String s) {

                        return Observable.just(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onStart() {
                        log("onStart: ");
                    }

                    @Override
                    public void onNext(Object s) {
                        log("onNext Item: " + s);
                    }

                    @Override
                    public void onCompleted() {
                        log("Completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("Error!");
                    }
                });
    }



}
