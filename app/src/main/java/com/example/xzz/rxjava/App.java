package com.example.xzz.rxjava;

import android.app.Application;

/**
 * Created by xzz on 2016/10/18.
 */

public class App extends Application {
    public String[] fruits = {
            "peach 桃子",
            "Lemon 柠檬",
            "Banana 香蕉",
            "Grape 葡萄",
            "orange 橘子",
            "Apple 苹果",
            "Coconut 椰子",
            "lychee 荔枝"};

    public String[] weeks = {
            "星期一：Monday",
            "星期二：Tuesday",
            "星期三：Wednesday",
            "星期四：Thursday",
            "星期五：Friday",
            "星期六：Saturday",
            "星期日：Sunday"
    };

    public String[] months = {
            "一月：January",
            "二月：February",
            "三月：March",
            "四月：April",
            "五月：May",
            "六月：June",
            "七月：July",
            "八月：August",
            "九月：September",
            "十月：October",
            "十一月：November",
            "十二月：December"
    };

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    private static App instance;

    public static App getInstance() {
        return instance;
    }
}
