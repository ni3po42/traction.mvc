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

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import amvvm.implementations.AttributeBridge;
import amvvm.implementations.ui.UIBindedEvent;
import amvvm.implementations.ui.UIBindedProperty;
import amvvm.implementations.ui.UIHandler;
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
implements MenuItem.OnMenuItemClickListener, IViewBinding
{
	public final UIBindedProperty<Boolean> IsVisible = new UIBindedProperty<Boolean>(this, R.styleable.Menu_IsVisible);
	public final UIBindedEvent<Object> OnClick = new UIBindedEvent<Object>(this, R.styleable.Menu_OnClick);
	
	private final WeakReference<MenuItem> menuItem;
	private final WeakReference<BindingInventory> inventoryReference;
	
	public MenuBinding(MenuItem item, TypedArray attrs, BindingInventory inventory)
	{
		menuItem = new WeakReference<MenuItem>(item);
		inventoryReference = new WeakReference<BindingInventory>(inventory);
		item.setOnMenuItemClickListener(this);	
		
		OnClick.initialize(attrs, inventory, null);
		IsVisible.initialize(attrs, inventory, null);
		
		IsVisible.setUIUpdateListener(new IUIElement.IUIUpdateListener<Boolean>()
		{			
			@Override
			public void onUpdate(Boolean value)
			{
				if (menuItem.get() == null || value == null)
					return;
				menuItem.get().setVisible(value);
			}
		});
	}

	@Override
	public boolean onMenuItemClick(MenuItem arg0) 
	{
		if (inventoryReference.get() == null)
			return false;
		OnClick.execute(null);
		return true;
	}

    @Override
    public void initialise(View v, AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {
        //not used
    }

    @Override
	public String getBasePath()
	{
		return null;
	}
	
	@Override
	public void detachBindings() 
	{
		if (menuItem.get() != null)
		{
			menuItem.get().setOnMenuItemClickListener(null);
			menuItem.clear();
		}
		if (inventoryReference.get() != null)
		{			
			inventoryReference.clear();
		}
		
		IsVisible.setUIUpdateListener(null);
	}


}
