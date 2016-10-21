package com.example.xzz.rxjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.concurrent.TimeUnit;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.schedulers.Timestamped;

public class OtherActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        findViewById(R.id.timestamp).setOnClickListener(this);
        findViewById(R.id.timeInterval).setOnClickListener(this);
        findViewById(R.id.doOn).setOnClickListener(this);
        findViewById(R.id.delay).setOnClickListener(this);
        findViewById(R.id.delaySubscription).setOnClickListener(this);
        findViewById(R.id.using).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        logText = "";
        int id = view.getId();
        switch (id) {
            case R.id.timestamp:
                timestamp();
                break;
            case R.id.timeInterval:
                timeInterval();
                break;
            case R.id.doOn:
                doOn();
                break;
            case R.id.delay:
                delay();
                break;
            case R.id.delaySubscription:
                delaySubscription();
                break;
            case R.id.using:
                using();
                break;
        }
    }

    private void using() {
        Observable
                .using(new Func0<Bitmap>() {
                    @Override
                    public Bitmap call() {
                        log("创建bitmap");
                        return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    }
                }, new Func1<Bitmap, Observable<String>>() {
                    @Override
                    public Observable<String> call(Bitmap bitmap) {
                        log("发送bitmap数据");
                        return Observable.just("bitmap width: " + bitmap.getWidth() + "bitmap height: " + bitmap.getHeight());
                    }
                }, new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        log("释放bitmap");
                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log("消费bitmap数据：" + s);
                    }
                });
    }

    /**
     * 延时处理请求，
     */
    private void delaySubscription() {
        Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        log("call 1: " + System.currentTimeMillis());
                        subscriber.onNext(1);
                        log("call 2: " + System.currentTimeMillis());
                        subscriber.onNext(2);
                        log("call 3: " + System.currentTimeMillis());
                        subscriber.onNext(3);
                        subscriber.onCompleted();
                    }
                })
                .delaySubscription(500, TimeUnit.MILLISECONDS)
                .timestamp()
                .subscribe(new Action1<Timestamped<Integer>>() {
                    @Override
                    public void call(Timestamped<Integer> integerTimestamped) {
                        log("subscribe：" + integerTimestamped.getTimestampMillis() + " " + integerTimestamped.getValue());
                    }
                });
    }

    /**
     * 延时发射结果，
     */
    private void delay() {
        Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        log("call 1: " + System.currentTimeMillis());
                        subscriber.onNext(1);
                        log("call 2: " + System.currentTimeMillis());
                        subscriber.onNext(2);
                        log("call 3: " + System.currentTimeMillis());
                        subscriber.onNext(3);
                        subscriber.onCompleted();
                    }
                })
                .delay(500, TimeUnit.MILLISECONDS)
                .timestamp()
                .subscribe(new Action1<Timestamped<Integer>>() {
                    @Override
                    public void call(Timestamped<Integer> integerTimestamped) {
                        log("subscribe：" + integerTimestamped.getTimestampMillis() + " " + integerTimestamped.getValue());
                    }
                });
    }

    private void doOn() {
        Observable.interval(500, TimeUnit.MILLISECONDS)
                .take(5)
                .subscribeOn(Schedulers.computation())
                //注册一个动作，对Observable发射的每个数据项使用
                .doOnEach(new Action1<Notification<? super Long>>() {
                    @Override
                    public void call(Notification<? super Long> notification) {
                        log("--doOnEach--" + notification.getKind() + " " + notification.getValue());
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        log("--doOnCompleted--");
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        log("--doOnError--");
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        log("--doOnTerminate--");
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        log("--doOnSubscribe--");
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        log("--doOnUnsubscribe--");
                    }
                })
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        log("--doAfterTerminate--");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        log("onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        log("onError: ");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        log("onNext: " + aLong.toString());
                    }
                });
    }

    private void timeInterval() {
        Observable.interval(500, TimeUnit.MILLISECONDS)
                .take(4)
                .timeInterval()
                .subscribe(new Action1<TimeInterval<Long>>() {
                    @Override
                    public void call(TimeInterval<Long> integerTimeInterval) {
                        log(integerTimeInterval.getIntervalInMilliseconds() + " " + integerTimeInterval.getValue());
                    }
                });
    }

    private void timestamp() {
        Observable.just(1, 2, 3)
                .timestamp()
                .subscribe(new Action1<Timestamped<Integer>>() {
                    @Override
                    public void call(Timestamped<Integer> timestamped) {
                        log(timestamped.getTimestampMillis() + " " + timestamped.getValue());
                        //1472627510548 1
                        //1472627510549 2
                        //1472627510549 3
                    }
                });
    }
}
