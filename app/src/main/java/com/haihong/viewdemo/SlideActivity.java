package com.haihong.viewdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlideActivity extends Activity {

    private LinearLayout ll_container;
    private ImageView ivMove;

    private int containerWidth, containerHeight;    //容器宽高
    private float lastX, lastY; //最后一次触摸事件的坐标
    private float actY1;
    private TextView num;
    TextView showText[];
    private int showNum;
    //滑动下注
    private LinearLayout params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        addView();
        initXYview();

        //咱们让数值默认隐藏状态
        HIDE();
    }

    /**
     * 滑动自由下注 (动态生成滑动条)================================================================
     */
    private void addView() {
        int cost = 5000;
        showNum = 35;
        num = (TextView) findViewById(R.id.num);

        showText = new TextView[showNum];
        for (int i = 0; i < showNum; i++) {
            showText[i] = new TextView(this);
            final TextView showText1 = new TextView(this);
            showText[i].setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            showText[i].setTag(i); //设置id
            showText[i].setTextColor(Color.WHITE);

            // set 文本大小
            showText1.setTextColor(Color.TRANSPARENT);
            showText1.setHeight(4);
            showText1.setWidth(4);

            params = (LinearLayout) findViewById(R.id.fl_container);
            params.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            //添加文本到主布局
            params.addView(showText[i]);
            params.addView(showText1);
            if (i == 0) {
                showText[i].setBackgroundResource(R.drawable.btn_data2);
                showText[i].setText(String.valueOf(cost));
                showText[i].getLayoutParams().width = ((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 52, getResources().getDisplayMetrics()));
                showText[i].getLayoutParams().height = ((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()));
            } else if (i == showNum / 2) {
                showText[i].setBackgroundResource(R.drawable.btn_data2);
                showText[i].setText(String.valueOf(cost / 2));
                showText[i].getLayoutParams().width = ((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 52, getResources().getDisplayMetrics()));
                showText[i].getLayoutParams().height = ((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()));
            } else if (i == showNum - 1) {
                showText[i].setBackgroundResource(R.drawable.btn_data2);
                showText[i].setText("100");
                showText[i].getLayoutParams().width = ((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 52, getResources().getDisplayMetrics()));
                showText[i].getLayoutParams().height = ((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()));
            } else {
                showText[i].setBackgroundResource(R.drawable.msg_num_shape);
                showText[i].setText(String.valueOf(cost / showNum * (showNum - i)));
                showText[i].setTextSize(0);
                showText[i].getLayoutParams().width = ((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()));
                showText[i].getLayoutParams().height = ((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()));
            }
        }

    }

    /**
     * 滑动取值
     */
    @SuppressLint("ClickableViewAccessibility")
    /**
     * 滑动自由下注 ================================================================================
     */
    private void initXYview() {
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        ll_container.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //不能直接在onCreate()方法中得到宽高,会得到0,所以注册视图树的观察者来得到宽高
                //OnPreDrawListener是当一个视图树将要绘制时，所要调用的回调函数的接口类
                containerWidth = ll_container.getWidth();
                containerHeight = ll_container.getHeight();
                return true;
            }
        });

        ivMove = (ImageView) findViewById(R.id.iv_move);     //滑动logo
        ivMove.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ivMove.setVisibility(View.VISIBLE);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //记录触摸时的坐标,这里为什么要用getRawX()和getRawY()相信理解getX(),
                        // getY()和getRawX(),getRawY()的区别就知道为什么了
                        lastX = motionEvent.getRawX();
                        lastY = motionEvent.getRawY();

                        actY1 = ivMove.getY();
                        DIAPLAY(); //显示滑动条

                        //return true对事件进行拦截,不继续下发,防止继续响应onClick事件.
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //每次移动的距离
                        float distanceX = motionEvent.getRawX() - lastX;
                        float distanceY = motionEvent.getRawY() - lastY;

                        //控件将要移动到的位置,先计算一下,不在ofFloat()方法中
                        // 再计算是因为要防止控件移动到容器之外.
                        float nextX = ivMove.getX() + distanceX;
                        float nextY = ivMove.getY() + distanceY;
                        //如果将要移动到的 x 轴坐标小于0,则等于0,防止移出容器左边
                        if (nextX < 0)
                            nextX = 0;
                        //防止移出容器右边
                        if (nextX > containerWidth - ivMove.getWidth())
                            nextX = containerWidth - ivMove.getWidth();
                        //防止移出容器顶边
                        if (nextY < 0)
                            nextY = 0;
                        //防止移出容器底边
                        if (nextY > containerHeight - ivMove.getHeight())
                            nextY = containerHeight - ivMove.getHeight();

                        if (nextY < showText[0].getY())
                            nextY = showText[0].getY();
                        //利用属性动画改变控件的x,y坐标
                        ObjectAnimator mObjectAnimatorX = ObjectAnimator.ofFloat(ivMove,
                                "x", ivMove.getX(), nextX);
                        ObjectAnimator mObjectAnimatorY = ObjectAnimator.ofFloat(ivMove,
                                "y", ivMove.getY(), nextY);
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(mObjectAnimatorX, mObjectAnimatorY);
                        mAnimatorSet.setDuration(0);
                        mAnimatorSet.start();
                        //移动完之后记录当前坐标
                        lastX = motionEvent.getRawX();
                        lastY = motionEvent.getRawY();

                        MatchingY(ivMove.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        HIDE();

                        //利用属性动画改变控件的x,y坐标
                        ObjectAnimator mObjectAnimatorXX = ObjectAnimator.ofFloat(ivMove,
                                "x", ivMove.getX(), 0);
                        ObjectAnimator mObjectAnimatorYY = ObjectAnimator.ofFloat(ivMove,
                                "y", ivMove.getY(), actY1);
                        AnimatorSet mAnimatorSetX = new AnimatorSet();
                        mAnimatorSetX.playTogether(mObjectAnimatorXX, mObjectAnimatorYY);
                        mAnimatorSetX.setDuration(0);
                        mAnimatorSetX.start();
                        break;
                }
                return false;
            }
        });
    }

    //匹配滑动坐标取值
    private void MatchingY(float y) {
        float showy;
        for (int i = 0; i < showNum; i++) {
            showy = showText[i].getY();
            if (showy - 2 < y && y < showy + 2) {
                num.setText(showText[i].getText().toString());
            } else if (showText[showNum - 1].getY() - 2 < y) {
                num.setText("100");
            }
        }
    }

    //隐藏
    private void HIDE() {
//        String colorhide = "#00000000";
//        ColorDrawable dw = new ColorDrawable(0x00000000);
        params.setVisibility(View.INVISIBLE);
        ivMove.setBackgroundResource(R.mipmap.png);
    }

    //显示
    private void DIAPLAY() {
//        String colorwhite = "#FFFFFF";
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
        params.setVisibility(View.VISIBLE);
        ivMove.setBackgroundResource(R.mipmap.my_gold2x);
    }


}
