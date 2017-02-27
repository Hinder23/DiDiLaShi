package com.android.didilashi.CustomView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/2/11.
 */

public class CountDownButton extends Button implements View.OnClickListener {
    private long countDownLength = 60 * 1000;
    private String beforeText = "获取验证码";
    private String refreshText = "重新获取";
    private Timer timer;                                    //开始执行计时的类，可以在每秒实行间隔任务
    private String afterText = "秒";                    //在开始倒计时之后那个秒数数字之后所要显示的字，默认是秒

    private OnClickListener onClickListener;
    private TimerTask timerTask;                                            //每秒时间到了之后所执行的任务


    public CountDownButton(Context context) {
        super(context);
        init();
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }



    private void init() {
        if (!TextUtils.isEmpty(getText())) {
            beforeText = getText().toString().trim();
        }
        this.setText(beforeText);
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        start();
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    /**
     * 设置监听按钮点击事件
     *
     * @param onclickListener
     */
    @Override
    public void setOnClickListener(OnClickListener onclickListener) {
        if (onclickListener instanceof CountDownButton) {
            super.setOnClickListener(onclickListener);
        } else {
            this.onClickListener = onclickListener;
        }
    }

    /**
     * 开始倒计时
     */
    public void start() {
        initTimer();
        this.setText(countDownLength / 1000 + afterText);
        this.setEnabled(false);
        timer.schedule(timerTask, 0, 1000);
    }

    /**
     * 初始化时间
     */
    private void initTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
    }

    /**
     * 设置倒计时时长
     *
     * @param length 默认毫秒
     */
    public void setLength(long length) {
        this.countDownLength = length;
    }

    /**
     * 设置未点击时显示的文字
     *
     * @param beforeText
     */
    public void setBeforeText(String beforeText) {
        this.beforeText = beforeText;
    }

    /**
     * 设置未点击后显示的文字
     *
     * @param beforeText
     */
    public void setAfterText(String beforeText) {
        this.afterText = afterText;
    }

    /**
     * 更新显示的文本                                                                          这里可以改动颜色
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CountDownButton.this.setText(countDownLength / 1000 + afterText);
            countDownLength -= 1000;
            if (countDownLength < 0) {
                CountDownButton.this.setEnabled(true);
                CountDownButton.this.setText(refreshText);
                clearTimer();
                countDownLength = 60 * 1000;
            }
        }
    };



    /**
     * 清除倒计时
     */
    private void clearTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     *在界面销毁后调用，否则有内存泄漏风险
     */
    @Override
    protected void onDetachedFromWindow() {
        clearTimer();
        super.onDetachedFromWindow();
    }




}