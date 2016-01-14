package com.empty.pullrefreshlistview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wulei on 16/1/8.
 */
public class ScaleView extends View {
    private Bitmap initBitmap;
    private Bitmap scaleBitmap;
    private float mCurrentProgress = 1;
    private int mWidth;
    private int mHeight;

    public ScaleView(Context context) {
        super(context);
        init(context);
    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        initBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bell));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = initBitmap.getWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = initBitmap.getHeight();
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //考虑padding的影响
        int leftPadding = getPaddingLeft();
        int topPadding = getPaddingTop();
        int rightPadding = getPaddingRight();
        int bottomPadding = getPaddingBottom();

        int lastWidth = getMeasuredWidth() - leftPadding - rightPadding;
        int lastHeight = getMeasuredHeight() - topPadding - bottomPadding;

        scaleBitmap = Bitmap.createScaledBitmap(initBitmap, lastWidth, lastHeight, true);

        canvas.save();
        //缩放画布
        canvas.scale(mCurrentProgress, mCurrentProgress, lastWidth / 2 + leftPadding, lastHeight / 2 + topPadding);
        //缩放图形,要写在画布缩放后边
        canvas.drawBitmap(scaleBitmap, topPadding, leftPadding, null);

        canvas.restore();
    }


    public void setCurrentProgress(float currentProgress) {
        mCurrentProgress = currentProgress;
        postInvalidate();
    }
}
