package ni3po42.android.amvvmdemo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import amvvm.implementations.AttributeBridge;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ViewFactory;
import amvvm.implementations.ui.UIBindedProperty;
import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.ui.viewbinding.TextViewBinding;
import amvvm.interfaces.IUIElement;
import amvvm.interfaces.IViewBinding;
import ni3po42.android.amvvmdemo.R;
import ni3po42.android.amvvmdemo.models.EntryItem;

public class SwipeEntryView
    extends LinearLayout
    implements IViewBinding
{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private TextView content;
    private TextView id;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float notUsed)
        {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                onLeftSwipe();
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                onRightSwipe();
            }
            return true;
        }
    };

    private GestureDetector gestureDetector;

    public UIBindedProperty<Boolean> Active = new UIBindedProperty<Boolean>(this);

    public SwipeEntryView(Context context)
    {
        super(context);
        init(context);
    }

    public SwipeEntryView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public SwipeEntryView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return gestureDetector.onTouchEvent(event);
    }

    private void init(Context context)
    {
        gestureDetector = new GestureDetector(context, gestureListener);
        setOrientation(HORIZONTAL);
        content = new TextView(context);
        id = new TextView(context);

        addView(id, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, MarginLayoutParams.MATCH_PARENT));
        addView(content, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, MarginLayoutParams.MATCH_PARENT));
    }

    @Override
    public void initialise(View notUsed, AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {
        TextViewBinding contentBinding = new TextViewBinding();
        TextViewBinding idBinding = new TextViewBinding();

        ViewFactory.createViewHolderFor(content, inventory, contentBinding, uiHandler);
        ViewFactory.createViewHolderFor(id, inventory, idBinding, uiHandler);

        contentBinding.initialise(content, attributeBridge, uiHandler, inventory);
        idBinding.initialise(id, attributeBridge, uiHandler, inventory);

        contentBinding.Text.setPath("Content");
        idBinding.Text.setPath("Id");
        contentBinding.Text.initialize(null, inventory, uiHandler);
        idBinding.Text.initialize(null, inventory, uiHandler);
    }

    protected void onLeftSwipe()
    {
        Object value = Active.getBindingInventory().DereferencePropertyType(Active.getPath());
        if (value instanceof Boolean && (Boolean)value)
        {
             Active.sendUpdate(!(Boolean)value);

        }

    }

    protected void onRightSwipe()
    {
        Object value = Active.getBindingInventory().DereferencePropertyType(Active.getPath());
        if (value instanceof Boolean && !(Boolean)value)
        {
            Active.sendUpdate(!(Boolean)value);

        }
    }

    @Override
    public String getBasePath() {
        return null;
    }

    @Override
    public void detachBindings()
    {
        ViewFactory.getViewHolder(content).viewBinding.detachBindings();
        ViewFactory.getViewHolder(id).viewBinding.detachBindings();
    }

}
