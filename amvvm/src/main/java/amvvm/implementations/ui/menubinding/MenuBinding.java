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

package amvvm.implementations.ui.menubinding;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import amvvm.implementations.observables.ResourceArgument;
import amvvm.implementations.ui.UIEvent;
import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.ui.UIProperty;
import amvvm.implementations.ui.viewbinding.ViewBindingHelper;
import amvvm.interfaces.IAttributeBridge;
import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IProxyViewBinding;
import amvvm.interfaces.IUIElement;
import amvvm.interfaces.IViewBinding;
import amvvm.implementations.BindingInventory;
import amvvm.R;

/**
 * Defines the ui elements that can bind a model/view-model to a menu item
 * @author Tim Stratton
 *
 */
public class MenuBinding 
implements MenuItem.OnMenuItemClickListener, IProxyViewBinding
{
    public final UIProperty<Boolean> IsVisible;
    public final UIEvent<ResourceArgument> OnClick;
    private final MenuItem menuItem;
    private final ViewBindingHelper<View> helper;
    private int commandValueResourceId = -1;

    public MenuBinding(MenuItem item)
    {
        this.menuItem = item;
        helper = new ViewBindingHelper<View>()
        {
            @Override
            public View getWidget()
            {
                return menuItem.getActionView();
            }

            @Override
            public void detachBindings() {
                super.detachBindings();
                MenuBinding.this.detachBindings();
            }

            @Override
            public void initialise(View v, IAttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory, int bindingFlags)
            {
                super.initialise(v, attributeBridge, uiHandler, inventory, bindingFlags);
                initialize(attributeBridge);
            }

            @Override
            protected Object getGenericBindingSource() {
                return menuItem;
            }

            @Override
            protected int[] getDeclaredStyleAttributeGroup() {
                return R.styleable.Menu;
            }

            @Override
            protected int getGenericBindingsAttribute() {
                return R.styleable.Menu_GenericBindings;
            }
        };

        IsVisible = new UIProperty<Boolean>(this, R.styleable.Menu_IsVisible);
        OnClick = new UIEvent<ResourceArgument>(this, R.styleable.Menu_OnClick);

        menuItem.setOnMenuItemClickListener(this);

        IsVisible.setUIUpdateListener(new IUIElement.IUIUpdateListener<Boolean>()
        {
            @Override
            public void onUpdate(Boolean value)
            {
                if (value == null)
                    return;
                menuItem.setVisible(value);
            }
        });
    }

    protected void initialize(IAttributeBridge attrs)
    {
        IAttributeGroup ag = attrs.getAttributes(R.styleable.Menu);
        OnClick.initialize(ag);
        IsVisible.initialize(ag);
        commandValueResourceId = ag.getResourceId(R.styleable.Button_CommandValue, -1);
        ag.recycle();
    }

    @Override
    public boolean onMenuItemClick(MenuItem arg0)
    {
        ResourceArgument arg = null;
        if (commandValueResourceId > 0)
            arg = new ResourceArgument(OnClick.getPropertyName(), commandValueResourceId);

        return OnClick.execute(arg);
    }

    private void detachBindings()
    {
        menuItem.setOnMenuItemClickListener(null);
        IsVisible.setUIUpdateListener(null);
    }

    @Override
    public IViewBinding getProxyViewBinding() {
        return helper;
    }
}
