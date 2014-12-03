package com.gergana.dragdroprecyclerview;

/**
 * Created by gergana on 11/21/14.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;


public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    /* Define custom OnItemClickListener */
    public interface OnItemClickListener {
        public void onItemLongClick(MotionEvent e);
    }

    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        /* Detect long press on item with GestureDetector */
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                /* Invoke onItemLongClick implementation of the custom OnItemClickListener */
                mListener.onItemLongClick(e);
                super.onLongPress(e);
            }


        });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return false;
    }


    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {

    }
}
