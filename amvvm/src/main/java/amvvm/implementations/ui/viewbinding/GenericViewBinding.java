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

package amvvm.implementations.ui.viewbinding;

import java.lang.ref.WeakReference;

import amvvm.implementations.ui.UIProperty;
import amvvm.implementations.ui.UIHandler;
import amvvm.interfaces.IUIElement;
import amvvm.interfaces.IViewBinding;
import amvvm.implementations.BindingInventory;
import amvvm.util.Log;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The base implementation for all default view bindings in AMVVM. This handles all base ui elements for all views bounded
 * @author Tim Stratton
 * 
 * Exposes the following properties:
 * IsVisible : updates if view is gone, visible or invisible
 * Generic Properties and Events
 *
 * @param <V> : type of view for the binding
 */
public class GenericViewBinding<V extends View>
implements IViewBinding
{
    private final ViewBindingHelper<V> helper = new ViewBindingHelper<V>()
    {
        @Override
        public V getWidget() {
            return GenericViewBinding.this.getWidget();
        }
    };
    private WeakReference<V> widget;

	public final UIProperty<Object> IsVisible = new UIProperty<Object>(this, "IsVisible");


    @Override
    public BindingInventory getBindingInventory()
    {
        return helper.getBindingInventory();
    }

    @Override
    public UIHandler getUIHandler()
    {
        return helper.getUIHandler();
    }

    @Override
    public JSONObject getTagProperties() {return helper.getTagProperties();}

    @Override
    public int getBindingFlags() {
        return helper.getBindingFlags();
    }

    @Override
    public boolean isSynthetic()
    {
        return helper.isSynthetic();
    }

    @Override
    public void markAsSynthetic(BindingInventory inventory)
    {
       helper.markAsSynthetic(inventory);
    }

    @Override
    public String getPathPrefix() {
        return helper.getPathPrefix();
    }

    @Override
    public void setPathPrefix(String prefix) {
        helper.setPathPrefix(prefix);
    }

    @Override
    public void updateBindingInventory(BindingInventory inventory) {
        helper.updateBindingInventory(inventory);
    }

    /**
	 * Gets the view (widget) this view binding is associated with.
	 * @return view (widget) if available. null otherwise.
	 */
	protected V getWidget()
	{
		if (widget == null || widget.get() == null)
			return null;
		return widget.get();
	}

	public GenericViewBinding()
	{
		IsVisible.setUIUpdateListener(new IUIElement.IUIUpdateListener<Object>()
		{
			@Override
			public void onUpdate(Object value)
			{
				if (getWidget()== null)
					return;
				if (value == null)//null will be interpreted as GONE
					getWidget().setVisibility(View.GONE);
				else if (value instanceof Boolean || boolean.class.equals(value.getClass()))//true/false is visible or invisible
                {
                    Boolean b = (Boolean)value;
					getWidget().setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                }
                else if (value instanceof Integer || int.class.equals(value.getClass()))
                {
                    Integer i = (Integer)value;
                    if (i == View.VISIBLE)
                        getWidget().setVisibility(View.VISIBLE);
                    else if (i == View.INVISIBLE)
                        getWidget().setVisibility(View.INVISIBLE);
                    else
                        getWidget().setVisibility(View.GONE);
                }
                else
                {
                    getWidget().setVisibility(View.VISIBLE);
                }
			}
		});
	}
	

	protected void initialise() throws Exception
	{		}

    @Override
    public void initialise(View v, UIHandler uiHandler, BindingInventory inventory, int flags)
    {
        widget = new WeakReference<V>((V)v);

        if (v == null)
            return;

        try
        {
            helper.initialise(v, uiHandler, inventory, flags);
            initialise();
        }
        catch (Exception ex)
        {
            Log.e("", ex);
        }
    }

    @Override
    public void registerUIElement(IUIElement<?> element)
    {
        helper.registerUIElement(element);
    }

    /**
	 * does general cleanup
	 */
	@Override
	public void detachBindings()
	{
        helper.detachBindings();
        widget.clear();
	}

    @Override
    public IViewBinding getProxyViewBinding()
    {
        return this;
    }
}
