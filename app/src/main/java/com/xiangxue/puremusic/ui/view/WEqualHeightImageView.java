package com.xiangxue.puremusic.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 功能：首页 3网格 每一个item 的 每一个网格 的 自定义View
 */
public class WEqualHeightImageView extends AppCompatImageView {
    public WEqualHeightImageView(Context context) {
        super(context);
    }

    public WEqualHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WEqualHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // 测量后的宽 设置 为 控件的 宽度 与 高度

        // TODO 下面做实验：
        /*
        // 获取View的宽度：
        int width = MeasureSpec.getSize(widthMeasureSpec);

        // 获取View的高度：
        int height = MeasureSpec.getSize(heightMeasureSpec);
        */
    }
}
