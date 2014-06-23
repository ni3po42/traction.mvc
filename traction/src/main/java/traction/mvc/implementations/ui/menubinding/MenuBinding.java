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

package traction.mvc.implementations.ui.menubinding;

import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import traction.mvc.observables.BindingInventory;
import traction.mvc.implementations.ui.UIEvent;
import traction.mvc.implementations.ui.UIHandler;
import traction.mvc.implementations.ui.UIProperty;
import traction.mvc.implementations.ui.viewbinding.ViewBindingHelper;
import traction.mvc.interfaces.IProxyViewBinding;
import traction.mvc.interfaces.IUIElement;
import traction.mvc.interfaces.IViewBinding;

/**
 * Defines the ui elements that can bind a model/view-model to a menu item
 * @author Tim Stratton
 *
 */
public class MenuBinding 
implements MenuItem.OnMenuItemClickListener, IProxyViewBinding
{
    public final UIProperty<Boolean> IsVisible;
    public final UIEvent OnClick;
    private final MenuItem menuItem;
    private JSONObject tagProperties;
    private final ViewBindingHelper<View> helper;

    public MenuBinding(MenuItem item, String tag)
    {
        this.menuItem = item;
        try
        {
            this.tagProperties = new JSONObject(tag);
        }
        catch (JSONException ex)
        {
        }
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
            public void initialise(View notUsed, UIHandler uiHandler, BindingInventory inventory, int bindingFlags)
            {
                super.initialise(notUsed, uiHandler, inventory, bindingFlags);

                try {
                    initialize();
                }
                catch (Exception exception){}
            }

            public JSONObject getTagProperties()
            {
                return MenuBinding.this.tagProperties;
            }

            @Override
            protected Object getGenericBindingSource() {
                return menuItem;
            }

        };

        IsVisible = new UIProperty<Boolean>(this,"IsVisible");
        OnClick = new UIEvent(this, "OnClick");

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

    protected void initialize() throws Exception
    {
        OnClick.initialize();
        IsVisible.initialize();
    }

    @Override
    public boolean onMenuItemClick(MenuItem arg0)
    {
        return OnClick.execute(null);
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
