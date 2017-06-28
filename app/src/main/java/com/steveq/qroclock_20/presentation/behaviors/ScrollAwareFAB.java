package com.steveq.qroclock_20.presentation.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Adam on 2017-06-26.
 */

public class ScrollAwareFAB extends FloatingActionButton.Behavior {
    private static final String TAG = ScrollAwareFAB.class.getSimpleName();
    public ScrollAwareFAB(){};

    public ScrollAwareFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, String.valueOf(child.getVisibility()));
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if(dyConsumed > 0 && child.getVisibility() == View.VISIBLE){
            child.hide();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE){
            child.show();
        }
    }
}
