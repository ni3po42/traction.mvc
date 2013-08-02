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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
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
    implements IViewBinding, SwipableListView.ISwipable
{
    private TextView content;
    private TextView id;

    public UIBindedProperty<Boolean> Active = new UIBindedProperty<Boolean>(this, R.styleable.SwipeEntryView_Active);

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

        addView(id, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, MarginLayoutParams.MATCH_PARENT));
        addView(content, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, MarginLayoutParams.MATCH_PARENT));

        Active.setUIUpdateListener(new IUIElement.IUIUpdateListener<Boolean>()
        {
            @Override
            public void onUpdate(Boolean value)
            {
                if (value == null)
                    return;
                setBackgroundColor( value ? 0x00000000 : 0xFFCCCCCC);
            }
        });

    }

    @Override
    public void initialise(View notUsed, AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {
        //when this view is initialised, the internal views must also be initialised so they
        //can take advantage of binding
        TypedArray ta = attributeBridge.getAttributes(R.styleable.SwipeEntryView);
        Active.initialize(ta, inventory, uiHandler);
        ta.recycle();

        //for now, the bindings are hard coded, this should rely on the viewfactory to determine this
        TextViewBinding contentBinding = new TextViewBinding();
        TextViewBinding idBinding = new TextViewBinding();


        //the order of these are important. will try to make this process a little more
        //simple in the future.
        ViewFactory.createViewHolderFor(content, inventory, contentBinding, uiHandler);
        ViewFactory.createViewHolderFor(id, inventory, idBinding, uiHandler);

        contentBinding.initialise(content, attributeBridge, uiHandler, inventory);
        idBinding.initialise(id, attributeBridge, uiHandler, inventory);

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
    public void detachBindings()
    {
        ViewFactory.getViewHolder(content).viewBinding.detachBindings();
        ViewFactory.getViewHolder(id).viewBinding.detachBindings();
    }

    @Override
    public void onLeftSwipe()
    {
        Object value = Active.getBindingInventory().DereferenceValue(Active.getPath());
        if (value instanceof Boolean)
        {
             Active.sendUpdate(!(Boolean)value);
            performAnimation(!(Boolean)value);
        }
    }

    @Override
    public void onRightSwipe()
    {

    }

    private void performAnimation(final boolean isActive)
    {
        ObjectAnimator outAnimator = ObjectAnimator
                .ofFloat(this, "x", -getWidth())
                .setDuration(250);
        outAnimator
                .addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animator)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator)
                    {
                        ObjectAnimator inAnimator = ObjectAnimator
                                .ofFloat(SwipeEntryView.this, "x", SwipeEntryView.this.getWidth(), 0)
                                .setDuration(250);

                        setBackgroundColor( isActive ? 0x00000000 : 0xFFCCCCCC);
                        inAnimator.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator)
                    {

                    }
                });
        outAnimator.start();
    }
}
