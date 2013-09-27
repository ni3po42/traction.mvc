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
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import amvvm.implementations.SyntheticAttributes.Builder;
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IAttributeBridge;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ViewFactory;
import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.ui.viewbinding.ViewBindingHelper;
import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IProxyViewBinding;
import amvvm.interfaces.IUIElement;
import amvvm.interfaces.IViewBinding;
import ni3po42.android.amvvmdemo.R;

public class SwipeEntryView
    extends LinearLayout
    implements IProxyViewBinding, SwipableListView.ISwipable, Animator.AnimatorListener
{
    private TextView content;
    private TextView id;

    private final ViewBindingHelper helper = new ViewBindingHelper<SwipeEntryView>()
    {
        @Override
        public SwipeEntryView getWidget()
        {
            return SwipeEntryView.this;
        }

        @Override
        public void detachBindings()
        {
            super.detachBindings();
            SwipeEntryView.this.detachBindings();
        }

        @Override
        public void initialise(View v, IAttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory, int bindingFlags)
        {
            super.initialise(v, attributeBridge, uiHandler, inventory, bindingFlags);
            SwipeEntryView.this.initialise(attributeBridge, uiHandler, inventory);
        }
    };

    public UIProperty<Boolean> Active = new UIProperty<Boolean>(this, R.styleable.SwipeEntryView_Active);

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

        Active.setUIUpdateListener(new IUIElement.IUIUpdateListener<Boolean>() {
            @Override
            public void onUpdate(Boolean value) {
                if (value == null)
                    return;
                setBackgroundColor(value ? 0x00000000 : 0xFFCCCCCC);
            }
        });
    }

    private void initialise(IAttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {
        //when this view is initialised, the internal views must also be initialised so they
        //can take advantage of binding
        IAttributeGroup ta = attributeBridge.getAttributes(R.styleable.SwipeEntryView);
        Active.initialize(ta);
        ta.recycle();

        IViewBinding contentBinding = helper.createSyntheticFor(content, null, inventory);
        IViewBinding idBinding = helper.createSyntheticFor(id, null, inventory);

        IAttributeBridge contentBridge = Builder
            .begin()
            .addAttribute(R.styleable.TextView, R.styleable.TextView_Text, "Content", Builder.TYPE_STRING)
            .build();

        IAttributeBridge idBridge = Builder
            .begin()
            .addAttribute(R.styleable.TextView, R.styleable.TextView_Text, "Id", Builder.TYPE_STRING)
            .build();

        contentBinding.initialise(content, contentBridge, uiHandler, inventory, IViewBinding.Flags.NO_FLAGS);
        idBinding.initialise(id, idBridge, uiHandler, inventory, IViewBinding.Flags.NO_FLAGS);
    }

    private void detachBindings()
    {
        if (content != null)
            ViewFactory.getViewBinding(content).detachBindings();
        if (id != null)
            ViewFactory.getViewBinding(id).detachBindings();
    }

    @Override
    public void onLeftSwipe()
    {
        ObjectAnimator outAnimator = ObjectAnimator
                .ofFloat(this, "x", -getWidth())
                .setDuration(250);
        outAnimator.addListener(this);
        outAnimator.start();
    }

    @Override
    public void onRightSwipe()
    {

    }

    @Override
    public IViewBinding getProxyViewBinding() {
        return helper;
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        ObjectAnimator inAnimator = ObjectAnimator
                .ofFloat(this, "x", this.getWidth(), 0)
                .setDuration(250);

        Boolean value = Active.dereferenceValue();
        if (value != null)
        {
            Active.sendUpdate(!value);
            setBackgroundColor( !value ? 0x00000000 : 0xFFCCCCCC);
            inAnimator.start();
        }
    }

    @Override
    public void onAnimationStart(Animator animator) {
    }
    @Override
    public void onAnimationCancel(Animator animator) {
    }
    @Override
    public void onAnimationRepeat(Animator animator) {
    }
}
