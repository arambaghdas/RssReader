package com.rssreader.rssinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.rssreader.R;

public class RssDetail extends Activity {

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    TextView descTextView, dateTextView, linkTextView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rss_detail);

        context = this;
        Intent i = getIntent();
        RssObject rssObject = (RssObject)i.getSerializableExtra("rssObject");
        String strDescription = Html.fromHtml(rssObject.description).toString();

        descTextView = (TextView) findViewById(R.id.descTextView);
        descTextView.setText(strDescription);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        dateTextView.setText(rssObject.pubDate);
        linkTextView = (TextView) findViewById(R.id.linkTextView);
        linkTextView.setText(rssObject.link);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        gestureDetector = new GestureDetector(this, customGestureDetector);
        gestureDetector.setOnDoubleTapListener(customGestureDetector);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        scaleGestureDetector.onTouchEvent(ev);
        gestureDetector.onTouchEvent(ev);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.
            SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float size = descTextView.getTextSize();
            float factor = detector.getScaleFactor();
            float scale = size * factor;
            descTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scale);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }

    private class CustomGestureDetector implements GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            finishAfterTransition();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
         }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
             return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (diffX > 0) {
                        onSwipeRight(diffX);
                    } else {
                        onSwipeLeft(diffX);
                    }
                    result = true;
                } else {
                    if (diffY > 0) {
                        onSwipeTop(diffY);
                    } else {
                        onSwipeBottom(diffY);
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        void onSwipeRight(float diff) {
            RelativeLayout.LayoutParams descLayoutParams = (RelativeLayout.LayoutParams) descTextView.getLayoutParams();
            descLayoutParams.rightMargin += diff;
            descTextView.setLayoutParams(descLayoutParams);
        }

        void onSwipeLeft(float diff) {
            RelativeLayout.LayoutParams descLayoutParams = (RelativeLayout.LayoutParams) descTextView.getLayoutParams();
            descLayoutParams.leftMargin += diff;
            descTextView.setLayoutParams(descLayoutParams);
        }

        void onSwipeTop(float diff) {
            RelativeLayout.LayoutParams descLayoutParams = (RelativeLayout.LayoutParams) descTextView.getLayoutParams();
            descLayoutParams.topMargin += diff;
            descTextView.setLayoutParams(descLayoutParams);
        }

        void onSwipeBottom(float diff) {
            RelativeLayout.LayoutParams descLayoutParams = (RelativeLayout.LayoutParams) descTextView.getLayoutParams();
            descLayoutParams.bottomMargin += diff;
            descTextView.setLayoutParams(descLayoutParams);
        }
    }

}