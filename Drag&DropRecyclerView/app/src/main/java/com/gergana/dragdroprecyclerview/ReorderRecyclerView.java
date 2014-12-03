package com.gergana.dragdroprecyclerview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by gergana on 12/2/14.
 */
public class ReorderRecyclerView extends RecyclerView {

    private final static int INVALID_VALUE = -1;
    private final int SMOOTH_SCROLL_AMOUNT_AT_EDGE = 15;


    private LinearLayoutManager layoutManager;
    private RecyclerViewAdapter myAdapter;

    private int mDownY = INVALID_VALUE;
    private int mDownX = INVALID_VALUE;

    private long mAboveItemId = INVALID_VALUE;
    private long mDragItemId = INVALID_VALUE;
    private long mBelowItemId = INVALID_VALUE;
    private int mActivePointerId = INVALID_VALUE;
    private int mLastEventY = INVALID_VALUE;

    private boolean cellOnTheMove = false;
    private boolean mIsMobileScrolling = false;
    private boolean mIsWaitingForScrollFinish = false;

    //  private BitmapDrawable mHoverCell;
//    private Rect mHoverCellCurrentBounds;
    private Rect mHoverCellOriginalBounds;

    private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;
    private int mTotalOffset = 0;
    private int mSmoothScrollAmountAtEdge = 100;
    private View draggedChild;

    private boolean isDown;
    private boolean isUp;

    private int previousMovedPosition = INVALID_VALUE;


    public ReorderRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public ReorderRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReorderRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    public void init(Context context) {
        /*Set LayoutManager */
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        setLayoutManager(layoutManager);


        /* Add OnTouchListener for long click of the RecyclerView items */
        addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemLongClick(MotionEvent e) {

                        myAdapter = (RecyclerViewAdapter) getAdapter();

                        mDownX = (int) e.getX();
                        mDownY = (int) e.getY();

                        /*Get child view on we have made long click*/
                        draggedChild = findChildViewUnder(mDownX, mDownY);
                        //TODO:Add proper decoration
                        draggedChild.setBackgroundColor(Color.DKGRAY);


                        /*Get child position and ID*/
                        int childPosition = layoutManager.getPosition(draggedChild);
                        mDragItemId = getAdapter().getItemId(childPosition);


                        int w = draggedChild.getWidth();
                        int h = draggedChild.getHeight();
                        int top = draggedChild.getTop();
                        int left = draggedChild.getLeft();
                        /*Get cell bounds*/
                        mHoverCellOriginalBounds = new Rect(left, top, left + w, top + h);


                        /*Set ids of above and bellow items */
                        updateNeighborViewsForID(mDragItemId);


                        cellOnTheMove = true;

                    }
                }));
//        DisplayMetrics
//                metrics = context.getResources().getDisplayMetrics();
//        mSmoothScrollAmountAtEdge = (int) (SMOOTH_SCROLL_AMOUNT_AT_EDGE / metrics.density);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                mActivePointerId = event.getPointerId(0);

                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_VALUE) {
                    break;
                }

                int pointerIndex = event.findPointerIndex(mActivePointerId);

                mLastEventY = (int) event.getY(pointerIndex);
                int deltaY = mLastEventY - mDownY;

                if (cellOnTheMove) {

                    mIsMobileScrolling = false;

                    /* Scroll the RecyclerView */
                    smoothScrollBy(mDownX, deltaY);

                    /* Handle movement*/
                    handleMoveEvent();

                    mIsMobileScrolling = false;

                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchEventsEnded();
                break;
            case MotionEvent.ACTION_CANCEL:
                touchEventsCancelled();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                        MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    touchEventsEnded();
                }
                break;
            default:
                break;
        }

        return super.onInterceptTouchEvent(event);
    }


    private void touchEventsCancelled() {
        if (mDragItemId > 0) {
            View mobileView = getViewFromID(mDragItemId);
            if (cellOnTheMove) {
                mAboveItemId = INVALID_VALUE;
                mDragItemId = INVALID_VALUE;
                mBelowItemId = INVALID_VALUE;
                mobileView.setVisibility(VISIBLE);
                draggedChild.setBackgroundColor(Color.TRANSPARENT);

                invalidate();
            }
            cellOnTheMove = false;
            mIsMobileScrolling = false;
            mActivePointerId = INVALID_VALUE;
        }
    }

    private void touchEventsEnded() {

        if (cellOnTheMove) {
            cellOnTheMove = false;
            mAboveItemId = INVALID_VALUE;
            mDragItemId = INVALID_VALUE;
            mBelowItemId = INVALID_VALUE;
            draggedChild.setVisibility(View.VISIBLE);
            draggedChild.setBackgroundColor(Color.TRANSPARENT);

            if (mScrollState != RecyclerView.SCROLL_STATE_IDLE) {
                mIsWaitingForScrollFinish = true;
                return;
            }

        }
    }

    private void handleMoveEvent() {
        final int deltaY = mLastEventY - mDownY;
        int deltaYTotal = mHoverCellOriginalBounds.top + mTotalOffset + deltaY;

        View belowView = getViewFromID(mBelowItemId);
        View dragView = getViewFromID(mDragItemId);
        View aboveView = getViewFromID(mAboveItemId);

        /* Determine if the we move up ot down */
        isDown = (belowView != null) && (deltaYTotal > belowView.getTop());
        isUp = (aboveView != null) && (deltaYTotal < aboveView.getTop());

        if (isDown || isUp) {

            /* Get the id of the view we are going to swap with the dragView */
            final long switchItemID = isDown ? mBelowItemId : mAboveItemId;

            int originalItem = getPositionFromID(mDragItemId);
            int swapWithChild = getPositionFromID(switchItemID);


            if (switchItemID == INVALID_VALUE) {

                return;
            }


            if (swapWithChild != INVALID_VALUE && originalItem != INVALID_VALUE) {

               /* Swap elements */
                swapElements(myAdapter.mDataset, originalItem, swapWithChild);


                /* !!!!!!!!!!!!!! Change dragItemId with id of the item we made swap !!!!!!!!!!!!!!!*/
                mDragItemId = getAdapter().getItemId(swapWithChild);

                /* Update neighbors ids */
                updateNeighborViewsForID(mDragItemId);

                dragView.setVisibility(View.VISIBLE);


            }


        }
    }


    /**
     * Retrieves the view in the list corresponding to itemID
     */
    public View getViewFromID(long itemID) {

        int firstVisiblePosition = layoutManager.getPosition(layoutManager.getChildAt(0));
        for (int i = 1; i < getChildCount(); i++) {
            View v = getChildAt(i);
            int position = firstVisiblePosition + i;

            long id = myAdapter.getItemId(position);

            if (id == itemID) {

                return v;
            }
        }
        return null;
    }

    /**
     * Retrieves the position in the list corresponding to itemID
     */
    public int getPositionFromID(long itemID) {
        View v = getViewFromID(itemID);
        if (v == null) {
            return -1;
        } else {
            return getChildPosition(v);
        }
    }

    /**
     * Stores a reference to the views above and below the item currently
     * corresponding to the moving cell. It is important to note that if this
     * item is either at the top or bottom of the list, mAboveItemId or mBelowItemId
     * may be invalid.
     */
    private void updateNeighborViewsForID(long itemID) {
        int position = getPositionFromID(itemID);

         /* Handle neighbor position for all items except first and last one*/
        if (position > 0 && position < myAdapter.getItemCount() - 1) {

            mAboveItemId = myAdapter.getItemId(position - 1);
            mBelowItemId = myAdapter.getItemId(position + 1);

        }


          /* Handle neighbor position for last item */
        if (position == myAdapter.getItemCount() - 1) {
            mAboveItemId = myAdapter.getItemId(position - 1);
            mBelowItemId = INVALID_VALUE;
        }
    }


    /**
     * Swap RecyclerView Items
     *
     * @param arrayList
     * @param indexOne
     * @param indexTwo
     */
    private void swapElements(ArrayList arrayList, int indexOne, int indexTwo) {
        if (indexOne != indexTwo && indexOne != INVALID_VALUE && indexTwo != INVALID_VALUE) {
            myAdapter.notifyItemMoved(indexOne, indexTwo);
            /* Subtract 1 because we have header view */
            Collections.swap(arrayList, indexOne - 1, indexTwo - 1);

        }
    }
}
