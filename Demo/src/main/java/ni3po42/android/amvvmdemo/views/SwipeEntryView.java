package ni3po42.android.amvvmdemo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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

    private TextView content;
    private TextView id;

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

        content = new TextView(context);
        id = new TextView(context);

        addView(content, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT));
        addView(id, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT));
    }

    @Override
    public void initialise(View notUsed, AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {
        ViewFactory.ViewHolder contentViewHolder = ViewFactory.createViewHolderFor(content, null);
        ViewFactory.ViewHolder idViewHolder = ViewFactory.createViewHolderFor(content, null);

        TextViewBinding contentBinding = new TextViewBinding();
        TextViewBinding idBinding = new TextViewBinding();

        contentViewHolder.isRoot = false;
        contentViewHolder.inventory = inventory;
        contentViewHolder.viewBinding = contentBinding;
        contentViewHolder.ignoreChildren = false;

        idViewHolder.isRoot = false;
        idViewHolder.inventory = inventory;
        idViewHolder.viewBinding = idBinding;
        contentViewHolder.ignoreChildren = false;

        contentBinding.Text.setPath("Content");
        idBinding.Text.setPath("Id");

        contentBinding.Text.initialize(null, inventory, uiHandler);
        idBinding.Text.initialize(null, inventory, uiHandler);
    }

    @Override
    public String getBasePath() {
        return null;
    }

    @Override
    public void detachBindings() {

    }
}
