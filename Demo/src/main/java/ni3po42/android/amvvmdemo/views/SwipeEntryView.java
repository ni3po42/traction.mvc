package ni3po42.android.amvvmdemo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import amvvm.implementations.AttributeBridge;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ui.UIHandler;
import amvvm.interfaces.IViewBinding;
import ni3po42.android.amvvmdemo.R;

public class SwipeEntryView
    extends LinearLayout
    implements IViewBinding
{

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

    private void init(Context context)
    {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.entryitem, this, true);
    }

    @Override
    public void initialise(View v, AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {

    }

    @Override
    public String getBasePath() {
        return null;
    }

    @Override
    public void detachBindings() {

    }
}
