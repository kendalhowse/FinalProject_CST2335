package com.example.kendalsasus.finalproject_cst2335;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Chris on 2017-12-29.
 */

/**
 * Use this class to add swiping capabilities (i.e something will happen when you swipe right/left/up/down)
 *
 * To implement...
 *
 * 1. Find a view to attach the listener to
 * 2.
 *      view.setOnTouchListener(new OnSwipeTouchListener(ActivityName.this) {
 *
 *          public void onSwipeLeft() { // when the user swipes left on the view
 *              // do something
 *          }
 *      });
 */
public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {return true;}

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = event2.getY() - event1.getY();
                float diffX = event2.getX() - event1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    public void onSwipeRight() {}
    public void onSwipeLeft() {}
    public void onSwipeTop() {}
    public void onSwipeBottom() {}
}
