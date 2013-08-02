/* Copyright 2013 Tim Stratton

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package ni3po42.android.amvvmdemo.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class SwipableListView
    extends ListView
{

    public static interface ISwipable
    {
        void onLeftSwipe();
        void onRightSwipe();
    }

    private final ISwipable nullSwipe = new ISwipable()
    {
        @Override
        public void onLeftSwipe()
        {

        }

        @Override
        public void onRightSwipe()
        {

        }
    };

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private final OnTouchListener touchListener = new OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    };

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float notUsed)
        {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

            int x = (int)e1.getRawX();
            int y = (int)e1.getRawY();

            if(x - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                getSwipedByHitTest(x,y).onLeftSwipe();
            }
            else if (e2.getX() - x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                getSwipedByHitTest(x,y).onRightSwipe();
            }
            return true;
        }
    };

    private GestureDetector gestureDetector;

    public SwipableListView(Context context)
    {
        super(context);
        init(context);
    }

    public SwipableListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public SwipableListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        setOnTouchListener(touchListener);
        gestureDetector = new GestureDetector(context, gestureListener);
    }

    private ISwipable getSwipedByHitTest(int x, int y)
    {
        int[] point = new int[2];
        this.getLocationOnScreen(point);
        x = x - point[0];
        y = y - point[1];
        int viewCount = getChildCount();
        Rect box = new Rect();
        for(int i=0;i<viewCount;i++)
        {
            if (!(getChildAt(i) instanceof ISwipable))
                continue;
            getChildAt(i).getHitRect(box);
            if (box.contains(x,y))
                return (ISwipable)getChildAt(i);
        }
        return nullSwipe;
    }

}
