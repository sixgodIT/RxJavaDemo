package com.example.xzz.rxjava;

import android.os.Bundle;
import android.view.View;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * 基础用法
 */
public class ZipActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip);

        findViewById(R.id.merge).setOnClickListener(this);
        findViewById(R.id.StartWith).setOnClickListener(this);
        findViewById(R.id.Concat).setOnClickListener(this);
        findViewById(R.id.Zip).setOnClickListener(this);
        findViewById(R.id.CombineLatest).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        logText = "";
        int id = view.getId();
        switch (id) {
            case R.id.merge:
                merge();
                break;
            case R.id.StartWith:
                StartWith();
                break;
            case R.id.Concat:
                Concat();
                break;
            case R.id.Zip:
                Zip();
                break;
            case R.id.CombineLatest:
                CombineLatest();
                break;
        }
    }


    private void CombineLatest() {

        Observable.just(1,2,3,45)
                .toMap(new Func1<Integer, Object>() {
                    @Override
                    public Object call(Integer integer) {
                        return null;
                    }
                })
                .subscribe(new Action1<Map<Object, Integer>>() {
                    @Override
                    public void call(Map<Object, Integer> objectIntegerMap) {

                    }
                });


        final String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        Observable<String> letterSequence = Observable.interval(300, TimeUnit.MILLISECONDS)

                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long position) {
                        return letters[position.intValue()];
                    }
                }).take(letters.length);

        Observable<Long> numberSequence = Observable.interval(500, TimeUnit.MILLISECONDS).take(5);

        Observable
                .combineLatest(letterSequence, numberSequence, new Func2<String, Long, String>() {
                    @Override
                    public String call(String s, Long aLong) {
                        return s + aLong.toString();
                    }
                })
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        log(serializable.toString());
                    }
                });
    }

    private void Zip() {
        final String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        Observable<String> letterSequence = Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long position) {
                        return letters[position.intValue()];
                    }
                }).take(letters.length);

        Observable<Long> numberSequence = Observable.interval(500, TimeUnit.MILLISECONDS).take(5);

        Observable
                .zip(letterSequence, numberSequence, new Func2<String, Long, String>() {
                    @Override
                    public String call(String s, Long aLong) {
                        return s + aLong.toString();
                    }
                })
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        log(serializable.toString());
                    }
                });
    }

    private void Concat() {
        final String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        Observable<String> letterSequence = Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long position) {
                        return letters[position.intValue()];
                    }
                }).take(letters.length);

        Observable<Long> numberSequence = Observable.interval(500, TimeUnit.MILLISECONDS).take(5);

        Observable.concat(letterSequence, numberSequence)
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        log(serializable.toString());
                    }
                });
    }

    private void StartWith() {
        final String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        Observable<String> letterSequence = Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long position) {
                        return letters[position.intValue()];
                    }
                }).take(letters.length);

        Observable<String> numberSequence = Observable.interval(500, TimeUnit.MILLISECONDS).take(5)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        return aLong.toString();
                    }
                });

        letterSequence
                .startWith(numberSequence)
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        log(serializable.toString());
                    }
                });
    }

    private void merge() {
        final String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        Observable<String> letterSequence = Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long position) {
                        return letters[position.intValue()];
                    }
                }).take(letters.length);

        Observable<Long> numberSequence = Observable.interval(500, TimeUnit.MILLISECONDS).take(5);

        Observable.merge(letterSequence, numberSequence)
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        log(serializable.toString());
                    }
                });
    }


}
