package com.cyymusic.puremusic.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.cyymusic.architecture.utils.FlingHelper;

public class NestedScrollLayout extends NestedScrollView {
    private View headerView;
    private ViewGroup nestedRecyclerViewContainer;
    private static final String TAG = "NestedScrollLayout";

    private FlingHelper mFlingHelper;
    private int totalDy = 0;
    private boolean isStartFling = false;
    private int velocityY = 0;

    public NestedScrollLayout(Context context) {
        this(context, null);
    }

    public NestedScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mFlingHelper = new FlingHelper(getContext());
        setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                handleScrollChange(scrollY, oldScrollY,v);
            }
        });
    }

    private void handleScrollChange(int scrollY, int oldScrollY,View v) {
        if (isStartFling) {
            totalDy = 0;
            isStartFling = false;
        }

        if (scrollY == 0) {
            Log.i(TAG, "TOP SCROLL");
            // Handle top scroll behavior
        }

        if (scrollY == (getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
            Log.i(TAG, "BOTTOM SCROLL");
            dispatchChildFling();
        }

        totalDy += scrollY - oldScrollY;
    }

    private void dispatchChildFling() {
        if (velocityY != 0) {
            double splineFlingDistance = mFlingHelper.getSplineFlingDistance(velocityY);
            if (splineFlingDistance > totalDy) {
                childFling(mFlingHelper.getVelocityByDistance(splineFlingDistance - totalDy));
            }
        }
        totalDy = 0;
        velocityY = 0;
    }

    private void childFling(int velY) {
        RecyclerView childRecyclerView = getChildRecyclerView(nestedRecyclerViewContainer);
        if (childRecyclerView != null) {
            childRecyclerView.fling(0, velY);
        }
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
        if (velocityY <= 0) {
            this.velocityY = 0;
        } else {
            isStartFling = true;
            this.velocityY = velocityY;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        headerView =((ViewGroup) getChildAt(0)).getChildAt(0);
        nestedRecyclerViewContainer = (ViewGroup)((ViewGroup) getChildAt(0)).getChildAt(1);
        //(ViewGroup)((ViewGroup)((ViewGroup) getChildAt(0)).getChildAt(0)).getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        adjustContentViewHeight();
    }

    private void adjustContentViewHeight() {
        ViewGroup.LayoutParams lp = nestedRecyclerViewContainer.getLayoutParams();
        lp.height = getMeasuredHeight();
        nestedRecyclerViewContainer.setLayoutParams(lp);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        // Calculate the remaining scroll distance that can be consumed by NestedScrollLayout
        int remainingScroll = headerView.getMeasuredHeight() - getScrollY();

        if (remainingScroll > 0) {
            if (dy > remainingScroll) {
                // Scroll the NestedScrollLayout by the remaining distance
                scrollBy(0, remainingScroll);
                consumed[1] = remainingScroll;

                // Scroll the RecyclerView by the remaining distance
                dispatchNestedPreScroll(0, dy - remainingScroll, consumed, null, type);
            } else {
                // Scroll the NestedScrollLayout by the full dy
                scrollBy(0, dy);
                consumed[1] = dy;
            }
        } else {
            // If NestedScrollLayout cannot consume more, pass the scroll to RecyclerView
            dispatchNestedPreScroll(0, dy, consumed, null, type);
        }

    }

/*
    private void handleNestedPreScroll(int dy, int[] consumed) {
        boolean hideHeader = dy > 0 && getScrollY() < headerView.getMeasuredHeight();
        if (hideHeader) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }*/

    private RecyclerView getChildRecyclerView(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof RecyclerView && view.getClass() == RecyclerView.class) {
                return (RecyclerView) viewGroup.getChildAt(i);
            } else if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                ViewGroup childRecyclerView = getChildRecyclerView((ViewGroup) viewGroup.getChildAt(i));
                if (childRecyclerView instanceof RecyclerView) {
                    return (RecyclerView) childRecyclerView;
                }
            }
            continue;
        }
        return null;
    }
}
