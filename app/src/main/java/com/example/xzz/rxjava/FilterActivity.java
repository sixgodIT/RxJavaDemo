package com.example.xzz.rxjava;

import android.os.Bundle;
import android.view.View;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 基础用法
 */
public class FilterActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        findViewById(R.id.filter).setOnClickListener(this);
        findViewById(R.id.ofType).setOnClickListener(this);
        findViewById(R.id.take).setOnClickListener(this);
        findViewById(R.id.skip).setOnClickListener(this);
        findViewById(R.id.distinct).setOnClickListener(this);
        findViewById(R.id.Debounce).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        logText = "";
        int id = view.getId();
        switch (id) {
            case R.id.filter:
                filter();
                break;
            case R.id.ofType:
                ofType();
                break;
            case R.id.take:
                take();
                break;
            case R.id.skip:
                skip();
                break;
            case R.id.distinct:
                distinct();
                break;
            case R.id.Debounce:
                Debounce();
                break;
        }
    }

    private void Debounce() {
        Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        if (subscriber.isUnsubscribed()) return;
                        try {
                            for (int i = 0; i < 10; i++) {
                                subscriber.onNext(i);
                                try {
                                    Thread.currentThread().sleep(i * 100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer aLong) {
                        log(aLong.toString());
                    }
                });
    }

    private void distinct() {
        Observable.just(1, 3, 5, 100, 9, 3, 1, 100, 214, 51)
                .distinct(new Func1<Integer, Object>() {
                    @Override
                    public Object call(Integer integer) {
                        return null;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        log(integer.toString());
                    }
                });
    }

    private void skip() {
        Observable.just(1, 3, 5, 4, 100, 214, 51)
                .skip(3)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        log(integer.toString());
                    }
                });
    }

    private void take() {
        Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                .take(5)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        log(aLong.toString());
                    }
                });
    }

    private void ofType() {
        Observable.just("1", 3, "哈哈", false, 10)
                .ofType(Integer.class)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        log(integer.toString());
                    }
                });
    }

    private void filter() {
        Observable.just(0, 1, -1, 3, 5, -2, 6)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer >= 0;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        log(integer.toString());
                    }
                });
    }


}
