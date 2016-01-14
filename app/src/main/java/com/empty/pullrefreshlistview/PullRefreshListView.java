package com.empty.pullrefreshlistview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by wulei on 16/1/12.
 */
public class PullRefreshListView extends ListView implements AbsListView.OnScrollListener {
    private ScaleView loadMoreView;
    private TextView tv;
    private View mHeadView;
    private int mHeadViewHeight;
    private float mLastY, y, offsetY;
    private int mfirstVisibleItem;
    /**
     * 动画播放时间
     */
    private static final int ANIM_DURATION = 200;
    /**
     * 缩小滑动时对padding的影响
     */
    private static final int RESISTANCE = 3;
    /**
     * 是否实现下拉刷新接口
     */
    private boolean refreshEnable = false;
    /**
     * 是否在播放动画
     */
    private boolean isAnimatoring = false;
    /**
     * 下拉刷新回调接口
     */
    private OnPullRefreshListener mOnPullRefreshListener;
    /**
     * 刷新动画
     */
    private ObjectAnimator mObjectAnimator;

    public PullRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOverScrollMode(OVER_SCROLL_NEVER);

        //先把布局加载进来
        mHeadView = LayoutInflater.from(context).inflate(R.layout.item_headview, null, false);
        loadMoreView = (ScaleView) mHeadView.findViewById(R.id.loadMoreView);
        tv = (TextView) mHeadView.findViewById(R.id.tv);
        addHeaderView(mHeadView);

        post(new Runnable() {
            @Override
            public void run() {
                //把headView的高度取出来
                mHeadViewHeight = mHeadView.getMeasuredHeight();
                resetState();
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!refreshEnable || isAnimatoring)
        {
            return super.onTouchEvent(ev);
        }

        y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //下拉,不超过原始的布局高度
                if (mfirstVisibleItem == 0 && y > mLastY && offsetY < mHeadViewHeight) {
                    changState();
                }
                //上滑
                if (mfirstVisibleItem == 0 && y < mLastY && offsetY > 0) {
                    changState();
                }
                break;
            case MotionEvent.ACTION_UP:
                int curPaddingTop = getPaddingTop();

                if (curPaddingTop > 0) {
                    isAnimatoring = true;

                    refreshingState();

                    mObjectAnimator = startRefreshAnim(loadMoreView);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            mOnPullRefreshListener.onRefresh();
                        }
                    });
                } else {
                    resetState();
                }
                break;
        }
        mLastY = y;
        return super.onTouchEvent(ev);
    }

    /**
     * 正在刷新的状态
     */
    private void refreshingState() {
        setHeadViewPadding(mHeadViewHeight);
        setCurrentProgress(mHeadViewHeight);
        offsetY = mHeadViewHeight;
        tv.setText("正在刷新");
    }


    /**
     * 将状态设置回原始状态
     */
    private void resetState() {
        offsetY = 0;
        setHeadViewPadding(0);
        setCurrentProgress(0);
    }

    /**
     * 滑动时动态设置各个组件的状态
     */
    private void changState() {
        offsetY = offsetY + (y - mLastY) / RESISTANCE;
        setHeadViewPadding((int) (offsetY));
        //从二分之一的地方开始缩放，使缩放效果更明显
        if (offsetY > mHeadViewHeight / 2) {
            setCurrentProgress((offsetY - mHeadViewHeight / 2) * 2);
        }
        //设置字体状态
        if (offsetY > mHeadViewHeight) {
            tv.setText("松开刷新");
        } else {
            tv.setText("下拉刷新");
        }
    }

    /**
     * 播放刷新动画
     *
     * @param target
     */
    private ObjectAnimator startRefreshAnim(ScaleView target) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(target, View.TRANSLATION_X, 0, 20, 0, -20, 0);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(ANIM_DURATION);
        objectAnimator.start();
        return objectAnimator;
    }

    /**
     * 根据滑动的距离设置图片的缩放
     *
     * @param offsetY
     */
    private void setCurrentProgress(float offsetY) {
        float scale = offsetY / mHeadViewHeight;
        scale = scale > 1 ? 1 : scale;
        loadMoreView.setCurrentProgress(scale);
    }

    /**
     * 位移相对于隐藏headview原点
     *
     * @param offset
     */
    private void setHeadViewPadding(int offset) {
        setPadding(0, offset - mHeadViewHeight, 0, 0);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mfirstVisibleItem = firstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 刷新完成
     */
    public void complete()
    {
        mObjectAnimator.cancel();
        resetState();
        isAnimatoring = false;
    }

    /**
     * 设置刷新回调监听
     * @param onPullRefreshListener
     */
    public void setOnPullRefreshListener(OnPullRefreshListener onPullRefreshListener) {
        if (onPullRefreshListener == null) {
            return;
        }
        this.mOnPullRefreshListener = onPullRefreshListener;
        refreshEnable = true;
    }

    public interface OnPullRefreshListener {
        void onRefresh();
    }
}
