package com.example.xzz.rxjava;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

public class MapActivity extends BaseActivity implements View.OnClickListener {
    String[] courses = {"语文", "数学", "英语"};
    Student student1 = new Student("小明", courses);
    Student student2 = new Student("小红", courses);
    Student student3 = new Student("小刚", courses);
    Student[] students = {student1, student2, student3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        findViewById(R.id.map).setOnClickListener(this);
        findViewById(R.id.flatmap).setOnClickListener(this);
        findViewById(R.id.groupby).setOnClickListener(this);
        findViewById(R.id.buffer).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        logText = "";
        int id = view.getId();
        switch (id) {
            case R.id.map:
                map();
                break;
            case R.id.flatmap:
                flatmap();
                break;
            case R.id.groupby:
                groupby();
                break;
            case R.id.buffer:
                buffer();
                break;
        }
    }


    private void map() {
        Observable.from(students)
                .map(new Func1<Student, String>() {
                    @Override
                    public String call(Student student) {
                        return student.name;
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String name) {
                        log(name);
                    }
                });
    }

    private void flatmap() {
        Observable.from(students)
                .flatMap(new Func1<Student, Observable<String>>() {
                    @Override
                    public Observable<String> call(Student student) {
                        return Observable.from(student.courses);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String course) {
                        log(course);
                    }
                });
    }

    private void groupby() {
        Observable<GroupedObservable<String, Integer>> groupedObservableObservable = Observable.just(2, 3, 5, 6)
                .groupBy(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {//分组
                        return integer % 2 == 0 ? "偶数" : "奇数";
                    }
                });
        Observable.concat(groupedObservableObservable)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        log(integer.toString());
                    }
                });

    }

    private void buffer() {
        Observable.just(2, 3, 5, 6, 7, 8, 9)
                .buffer(3)
                .subscribe(new Action1<List<Integer>>() {
                    @Override
                    public void call(List<Integer> integers) {
                        String log = "";
                        for (Integer integer : integers) {
                            log += integer.toString() + ",";
                        }
                        log(log);
                    }
                });
    }

    public class Student {
        String name;
        String[] courses;

        public Student(String name, String[] courses) {
            this.name = name;
            this.courses = courses;
        }
    }

}
