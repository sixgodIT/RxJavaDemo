#RxJava  使用
***
##集成
    compile 'io.reactivex:rxandroid:1.2.1'
    compile 'io.reactivex:rxjava:1.2.1'
##创建被观察者
####create
 使用OnSubscribe从头创建一个Observable，这种方法比较简单。需要注意的是，使用该方法创建时，建议在OnSubscribe#call方法中检查订阅状态，以便及时停止发射数据或者运算。
 
    Observable.create(new Observable.OnSubscribe<String>() {

        @Override
        public void call(Subscriber<? super String> subscriber) {

            subscriber.onNext("item1");
            subscriber.onNext("item2");
            subscriber.onCompleted();
        }
    });
####from
将一个Iterable, 一个Future, 或者一个数组，内部通过代理的方式转换成一个Observable。Future转换为OnSubscribe是通过OnSubscribeToObservableFuture进行的，Iterable转换通过OnSubscribeFromIterable进行。数组通过OnSubscribeFromArray转换。

	String[] words = {"Hello", "Hi", "Aloha"};
	Observable observable = Observable.from(words);
####just
将一个或多个对象转换成发射这个或这些对象的一个Observable。如果是单个对象，内部创建的是ScalarSynchronousObservable对象。如果是多个对象，则是调用了from方法创建。

	Observable observable = Observable.just("Hello", "Hi", "Aloha");
####timer
创建一个在给定的延时之后发射数据项为0的Observable<Long>,内部通过OnSubscribeTimerOnce工作

	Observable.timer(1000,TimeUnit.MILLISECONDS)
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    Log.d("JG",aLong.toString()); // 0
                }
            });
####interval
创建一个按照给定的时间间隔发射从0开始的整数序列的Observable<Long>，内部通过OnSubscribeTimerPeriodically工作

	Observable.interval(1, TimeUnit.SECONDS)
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                     //每隔1秒发送数据项，从0开始计数
                     //0,1,2,3....
                }
            });
####range
创建一个发射指定范围的整数序列的Observable<Integer>

	 Observable.range(2,5).subscribe(new Action1<Integer>() {
        @Override
        public void call(Integer integer) {
            Log.d("JG",integer.toString());// 2,3,4,5,6 从2开始发射5个数据
        }
    });

##创建观察者
####创建Observer
Observer 即观察者，它决定事件触发的时候将有怎样的行为。 RxJava 中的 Observer 接口的实现方式：

	Observer<String> observer = new Observer<String>() {
    	@Override
    	public void onNext(String s) {
        	Log.d(tag, "Item: " + s);
    	}

    	@Override
    	public void onCompleted() {
        	Log.d(tag, "Completed!");
    	}

    	@Override
    	public void onError(Throwable e) {
        	Log.d(tag, "Error!");
    	}
    };
###创建subscriber
除了 Observer 接口之外，RxJava 还内置了一个实现了 Observer 的抽象类：Subscriber。 Subscriber 对 Observer 接口进行了一些扩展，但他们的基本使用方式是完全一样的：

	Subscriber<String> subscriber = new Subscriber<String>() {
    @Override
    public void onNext(String s) {
        Log.d(tag, "Item: " + s);
    }

    @Override
    public void onCompleted() {
        Log.d(tag, "Completed!");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(tag, "Error!");
    }
	};
####Action1,Action0
	Action1<String> onNextAction = new Action1<String>() {
    // onNext()
    @Override
    public void call(String s) {
        Log.d(tag, s);
    }
    };
	Action1<Throwable> onErrorAction = new 	Action1<Throwable>() {
    // onError()
    @Override
    public void call(Throwable throwable) {
        // Error handling
    }
    };
	Action0 onCompletedAction = new Action0() {
    // onCompleted()
    @Override
    public void call() {
        Log.d(tag, "completed");
    }
    };

	// 自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
	observable.subscribe(onNextAction);
	// 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
	observable.subscribe(onNextAction, onErrorAction);
	// 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
	observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
Action0 是 RxJava 的一个接口，它只有一个方法 call()，这个方法是无参无返回值的；由于 onCompleted() 方法也是无参无返回值的，因此 Action0 可以被当成一个包装对象，将 onCompleted() 的内容打包起来将自己作为一个参数传入 subscribe() 以实现不完整定义的回调。这样其实也可以看做将 onCompleted() 方法作为参数传进了 subscribe()，相当于其他某些语言中的『闭包』。 Action1 也是一个接口，它同样只有一个方法 call(T param)，这个方法也无返回值，但有一个参数；与 Action0 同理，由于 onNext(T obj) 和 onError(Throwable error) 也是单参数无返回值的，因此 Action1 可以将 onNext(obj) 和 onError(error) 打包起来传入 subscribe() 以实现不完整定义的回调。事实上，虽然 Action0 和 Action1 在 API 中使用最广泛，但 RxJava 是提供了多个 ActionX 形式的接口 (例如 Action2, Action3) 的，它们可以被用以包装不同的无返回值的方法。
##订阅
创建了 Observable 和 Observer 之后，再用 subscribe() 方法将它们联结起来，整条链子就可以工作了。代码形式很简单：

	observable.subscribe(observer);
	// 或者：
	observable.subscribe(subscriber);
##Scheduler
 subscribeOn(): 指定 subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程。 * observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
![Scheduler](http://upload-images.jianshu.io/upload_images/1167421-3be517cb352b2095.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#####多切换几次线程：
![Scheduler](http://ww1.sinaimg.cn/mw1024/52eb2279jw1f2rxd1vl7xj20hd0hzq6e.jpg)
* 对subscribeOn()的调用是自下向上，所以多次调用subscribeOn()，结果会被最上面的subscribeOn()覆盖。（生成和消费都会被覆盖）

* observeOn()之上有subscribeOn()调用 
observeOn()的工作原理是把消费结果先缓存，再切换到新线程上让原始消费者消费，它和生产者是没有一点关系的，就算subscribeOn()调用了，也只是改变observeOn()这个消费者所在的线程，和OperatorObserveOn中存储的原始消费者一点关系都没有，它还是由observeOn()控制。

* observeOn()之下有subscribeOn()调用 
这也不会改变observeOn()所指定的消费线程，因为observeOn()是自上而下调用，对subscribeOn()的调用是自下向上，在observeOn()指定的线程会覆盖下面subscribeOn()指定线程来去消费
#####栗子
	Observable.just(1, 2, 3, 4) // IO 线程，由 subscribeOn() 指定
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.newThread())
    .map(mapOperator) // 新线程，由 observeOn() 指定
    .observeOn(Schedulers.io())
    .map(mapOperator2) // IO 线程，由 observeOn() 指定
    .observeOn(AndroidSchedulers.mainThread) 
    .subscribe(subscriber);  // Android 主线程，由 observeOn() 指定
    
##变换
flatMap() 和 map() 有一个相同点：它也是把传入的参数转化之后返回另一个对象。但需要注意，和 map() 不同的是， flatMap() 中返回的是个 Observable 对象，并且这个 Observable 对象并不是被直接发送到了 Subscriber 的回调方法中。
 flatMap() 的原理是这样的：1. 使用传入的事件对象创建一个 Observable 对象；2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，而这个 Observable 负责将这些事件统一交给 Subscriber 的回调方法。这三个步骤，把事件拆成了两级，通过一组新创建的 Observable 将初始的对象『铺平』之后通过统一路径分发了下去。而这个『铺平』就是 flatMap() 所谓的 flat。
####map
	Observable.just("images/logo.png") // 输入类型 String
    .map(new Func1<String, Bitmap>() {
        @Override
        public Bitmap call(String filePath) { // 参数类型 String
            return getBitmapFromPath(filePath); // 返回类型 Bitmap
        }
    })
    .subscribe(new Action1<Bitmap>() {
        @Override
        public void call(Bitmap bitmap) { // 参数类型 Bitmap
            showBitmap(bitmap);
        }
    });
####flatmap
	Student[] students = ...;
	Subscriber<Course> subscriber = new Subscriber<Course>() {
    @Override
    public void onNext(Course course) {
        Log.d(tag, course.getName());
    }
    ...
	};
	Observable.from(students)
    .flatMap(new Func1<Student, Observable<Course>>() {
        @Override
        public Observable<Course> call(Student student) {
            return Observable.from(student.getCourses());
        }
    })
    .subscribe(subscriber);
####lift（变换的原理）
	// 注意：这不是 lift() 的源码，而是将源码中与性能、兼容性、扩展性有关的代码剔除后的核心代码。
	// 如果需要看源码，可以去 RxJava 的 GitHub 仓库下载。
	public <R> Observable<R> lift(Operator<? extends R, ? super T> operator) {
    return Observable.create(new OnSubscribe<R>() {
        @Override
        public void call(Subscriber subscriber) {
            Subscriber newSubscriber = operator.call(subscriber);
            newSubscriber.onStart();
            onSubscribe.call(newSubscriber);
        }
    });
	}
在 Observable 执行了 lift(Operator) 方法之后，会返回一个新的 Observable，这个新的 Observable 会像一个代理一样，负责接收原始的 Observable 发出的事件，并在处理后发送给 Subscriber。
##过滤
####filter 
filter(Func1)用来过滤观测序列中我们不想要的值，只返回满足条件的值
![filter icon](http://upload-images.jianshu.io/upload_images/1167421-b065454753c9f0a2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####ofType
ofType(Class class)过滤指定类型Class，属于filter的一种实现
#### skip
skip(int)让我们可以忽略Observable发射的前n项数据。
![skip icon](http://upload-images.jianshu.io/upload_images/1167421-8b343c90b2f71e95.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####take
take(int)用一个整数n作为一个参数，从原始的序列中发射前n个元素.
![take icon](http://upload-images.jianshu.io/upload_images/1167421-22bfbabf028711d3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####distinct
distinct()的过滤规则是只允许还没有发射过的数据通过，所有重复的数据项都只会发射一次。
![diatinct](http://upload-images.jianshu.io/upload_images/1167421-2576e801f19fa185.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##组合
####merge
merge(Observable, Observable)将两个Observable发射的事件序列组合并成一个事件序列，就像是一个Observable发射的一样。你可以简单的将它理解为两个Obsrvable合并成了一个Observable，合并后的数据是无序的。
![merge](http://upload-images.jianshu.io/upload_images/1167421-2f269a8811793bf9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####startWith
startWith(T)用于在源Observable发射的数据前插入数据
![startWith](http://upload-images.jianshu.io/upload_images/1167421-46bf223042f59214.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### concat
concat(Observable<? extends T>, Observable<? extends T>) concat(Observable<？ extends Observable<T>>)用于将多个obserbavle发射的的数据进行合并发射，concat严格按照顺序发射数据，前一个Observable没发射玩是不会发射后一个Observable的数据的。它和merge、startWitch和相似，不同之处在于：

* merge:合并后发射的数据是无序的；
* startWitch:只能在源Observable发射的数据前插入数据。
![concat](http://upload-images.jianshu.io/upload_images/1167421-cb53c8189015cb58.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####zip
zip(Observable, Observable, Func2)用来合并两个Observable发射的数据项，根据Func2函数生成一个新的值并发射出去。当其中一个Observable发送数据结束或者出现异常后，另一个Observable也将停在发射数据。
![zip](http://upload-images.jianshu.io/upload_images/1167421-dc5abf808a199553.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### combineLatest
comnineLatest(Observable, Observable, Func2)用于将两个Observale最近发射的数据已经Func2函数的规则进展组合
![combineLatest](http://upload-images.jianshu.io/upload_images/1167421-8a324e1c18a36c06.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
##转换操作
#### toList
收集原始Observable发射的所有数据到一个列表，然后返回这个列表.
#### toSortedList
收集原始Observable发射的所有数据到一个有序列表，然后返回这个列表。
####toMap
 将序列数据转换为一个Map。我们可以根据数据项生成key和生成value。
####toMultiMap
类似于toMap，不同的地方在于map的value是一个集合。

##错误处理/重试机制
* onErrorResumeNext： 当原始Observable在遇到错误时，使用备用Observable。。
* onExceptionResumeNext： 当原始Observable在遇到异常时，使用备用的Observable。与onErrorResumeNext类似，区别在于onErrorResumeNext可以处理所有的错误，onExceptionResumeNext只能处理异常。
* onErrorReturn： 当原始Observable在遇到错误时发射一个特定的数据。
* retry： 当原始Observable在遇到错误时进行重试。
* retryWhen： 当原始Observable在遇到错误，将错误传递给另一个Observable来决定是否要重新订阅这个Observable,内部调用的是retry。
##连接操作
ConnectableObservable与普通的Observable差不多，但是可连接的Observable在被订阅时并不开始发射数据，只有在它的connect()被调用时才开始。用这种方法，你可以等所有的潜在订阅者都订阅了这个Observable之后才开始发射数据。 
ConnectableObservable.connect()指示一个可连接的Observable开始发射数据. 
Observable.publish()将一个Observable转换为一个可连接的Observable 
Observable.replay()确保所有的订阅者看到相同的数据序列的ConnectableObservable，即使它们在Observable开始发射数据之后才订阅。 
ConnectableObservable.refCount()让一个可连接的Observable表现得像一个普通的Observable。
	
	ConnectableObservable<Integer> co= Observable.just(1,2,3)
                .publish();

        co .subscribe(integer -> Log.d("JG",integer.toString()) );
        co.connect();//此时开始发射数据
##阻塞操作

BlockingObservable是一个阻塞的Observable。普通的Observable 转换为 BlockingObservable，可以使用 Observable.toBlocking( )方法或者BlockingObservable.from( )方法。内部通过CountDownLatch实现了阻塞操作。

以下的操作符可以用于BlockingObservable，
`如果是普通的Observable，务必使用Observable.toBlocking()转为阻塞Observable后使用，否则达不到预期的效果。`
* forEach 对BlockingObservable发射的每一项数据调用一个方法
* first/firstOrDefault/last/lastOrDefault
* single/singleOrDefault 如果Observable终止时只发射了一个值，返回那个值，否则抛出异常或者发射默认值。
* mostRecent 返回一个总是返回Observable最近发射的数据的Iterable
* next 返回一个Iterable，会阻塞直到Observable发射了第二个值，然后返回那个值。
* latest 返回一个iterable，会阻塞直到或者除非Observable发射了一个iterable没有返回的值，然后返回这个值
* toFuture 将Observable转换为一个Future
* toIterable  将一个发射数据序列的Observable转换为一个Iterable。
* getIterator 将一个发射数据序列的Observable转换为一个Iterator

##工具集
* timestamp： 给Observable发射的每个数据项添加一个时间戳。
* doOnEach： 注册一个动作，对Observable发射的每个数据项使用
* doOnCompleted： 注册一个动作，对正常完成的Observable使用
* doOnError： 注册一个动作，对发生错误的Observable使用
* doOnTerminate：注册一个动作，对完成的Observable使用，无论是否发生错误
* doOnSubscribe： 注册一个动作，在观察者订阅时使用。内部由OperatorDoOnSubscribe实现
* doOnUnsubscribe： 注册一个动作，在观察者取消订阅时使用。内部由OperatorDoOnUnsubscribe实现，在call中加入一个解绑动作
* finallyDo/doAfterTerminate： 注册一个动作，在Observable完成时使用
* delay： 延时发射Observable的结果。即让原始Observable在发射每项数据之前都暂停一段指定的时间段。效果是Observable发射的数据项在时间上向前整体平移了一个增量（除了onError，它会即时通知）
* using： 创建一个只在Observable生命周期存在的资源，当Observable终止时这个资源会被自动释放。

###附：参考资料
[RxJava github地址](https://github.com/ReactiveX/RxJava)

[RxAndroid](https://github.com/ReactiveX/RxAndroid)

[Rxjava翻译中文文档](https://mcxiaoke.gitbooks.io/rxdocs/content/)

[Rxjava原理详解](http://gank.io/post/560e15be2dca930e00da1083)

[Rxjava简介](http://www.jianshu.com/p/ec9849f2e510)
