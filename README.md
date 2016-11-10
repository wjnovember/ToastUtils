## 效果图

<img src="https://github.com/inerdstack/ToastUtils/blob/master/images/toastutils.png" width = "300" alt="图片名称" align=center />

## 参考

[当关闭通知消息权限后无法显示系统Toast的解决方案](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650821437&idx=1&sn=b2f9687d6edea1c3965a415e8d8becd8&chksm=80b787a3b7c00eb5e758e2432c2dfb28df00541b6c1b90bd9eb97303820310d851205a750b66&mpshare=1&scene=1&srcid=1110GiKRl8H0WwoJsjxZcO6D#rd)

## 问题分析

Android 5.0以上系统将消息通知默认为关闭，而Toast源码中有如下一段代码：

```
/**
 * Show the view for the specified duration.
 */
public void show() {
    if (mNextView == null) {
        throw new RuntimeException("setView must have been called");
    }

    INotificationManager service = getService();
    String pkg = mContext.getOpPackageName();
    TN tn = mTN;
    tn.mNextView = mNextView;

    try {
        service.enqueueToast(pkg, tn, mDuration);
    } catch (RemoteException e) {
        // Empty
    }
}
```

其中调用了INotificationManager类，在默认关闭消息通知的情况下，此类无法调用，因此在Android 5.0以上的系统中，默认是不会显示Toast的，因此使用自定义的Toast实现吐司效果。

## 主要文件

* java
	* ToastUtils.java
	* MainActivity.java
	* BaseActivity.java
* res/layout
	* activity_main.xml
	* toast_layout.xml
* res/drawable
	* shape\_toastutils\_bg.xml
	

## 核心代码

**makeText()方法：**

```
public static ToastUtils makeText(Context context, String message,
                                  int HIDE_DELAY) {
    if (mInstance == null) {
        mInstance = new ToastUtils(context);
    } else {
        // 考虑Activity切换时，Toast依然显示
        if (!mContext.getClass().getName().endsWith(context.getClass().getName())) {
            mInstance = new ToastUtils(context);
        }
    }

    if (HIDE_DELAY == LENGTH_LONG) {
        mInstance.HIDE_DELAY = 2500;
    } else {
        mInstance.HIDE_DELAY = 1500;
    }
    mTextView.setText(message);
    return mInstance;
}
```

```
public static ToastUtils makeText(Context context, int resId, int HIDE_DELAY) {
    String mes = "";
    try {
        mes = context.getResources().getString(resId);
    } catch (Resources.NotFoundException e) {
        e.printStackTrace();
    }
    return makeText(context, mes, HIDE_DELAY);
}
```

**show()方法：**

```
public void show() {
    if (isShow) {
        // 若已经显示，则不再次显示
        return;
    }
    isShow = true;
    // 显示动画
    mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
    // 消失动画
    mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
    mFadeOutAnimation.setDuration(ANIMATION_DURATION);
    mFadeOutAnimation
            .setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // 消失动画后更改状态为 未显示
                    isShow = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // 隐藏布局，不使用remove方法为防止多次创建多个布局
                    mContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
    mContainer.setVisibility(View.VISIBLE);

    mFadeInAnimation.setDuration(ANIMATION_DURATION);

    mContainer.startAnimation(mFadeInAnimation);
    mHandler.postDelayed(mHideRunnable, HIDE_DELAY);
}
```

## 调用

ToastUtils调用与系统自带的Toast调用相似，调用如下：

ToastUtils.makeText(context, "消息内容",ToastUtils.LENGTH_SHORT).show();

## 温馨提示

1.Demo中为了与系统的Toast形成对比，将ToastUtils布局中的marginBottom设置偏大，实际开发中请将marginBottom设置为64dp，布局代码更改如下：
```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/mbContainer"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    
    android:layout_marginBottom="64dp"
    
    android:gravity="bottom|center"
    android:orientation="vertical"
    android:paddingLeft="50dp"
    android:paddingRight="50dp">
```

2.为防止Activity切换时ToastUtils单例依然持有上一个Activity的Context，请在BaseActivity的onDestroy()方法中调用reset()方法
