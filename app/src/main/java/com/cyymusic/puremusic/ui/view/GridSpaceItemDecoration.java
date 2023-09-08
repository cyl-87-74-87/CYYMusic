package com.cyymusic.puremusic.ui.view;

import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：RecycleView的网格空间物品装饰 自定义
 */
public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public GridSpaceItemDecoration(int mSpace, RecyclerView parent) {
        this.mSpace = mSpace;
        getRecyclerViewOffsets(parent); // 只需要调用一次就行了
    }

    /**
     * 获取项目偏移量
     * @param outRect Item的矩形边界
     * @param view ItemView
     * @param parent RecycleView
     * @param state RecycleView的状态
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mSpace;
    }

    /**
     * View margin
     * margin 等于 正，  那么View 会距离边界产生一个距离
     * margin 等于 负，  那么View 会超出边界产生一个距离
     * 所以下面的代码，就是为了 让 第一个 边界左侧 超出边界 从而不会留白
     *
     * @param parent
     */
    private void getRecyclerViewOffsets(RecyclerView parent) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) parent.getLayoutParams();
        layoutParams.leftMargin = -mSpace;
        parent.setLayoutParams(layoutParams);
    }
}
