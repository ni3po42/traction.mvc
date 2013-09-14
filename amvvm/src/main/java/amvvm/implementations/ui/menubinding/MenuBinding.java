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

import android.view.MenuItem;
import android.view.View;

import amvvm.implementations.observables.ResourceArgument;
import amvvm.implementations.ui.UIEvent;
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IAttributeBridge;
import amvvm.implementations.ui.UIHandler;
import amvvm.interfaces.IAttributeGroup;
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
implements MenuItem.OnMenuItemClickListener, MenuItem.OnActionExpandListener, IViewBinding
{
	public final UIProperty<Boolean> IsVisible = new UIProperty<Boolean>(this, R.styleable.Menu_IsVisible);
	public final UIEvent<ResourceArgument> OnClick = new UIEvent<ResourceArgument>(this, R.styleable.Menu_OnClick);

    private int commandValueResourceId = -1;

	private final WeakReference<MenuItem> menuItem;
	private final WeakReference<BindingInventory> inventoryReference;
	
	public MenuBinding(MenuItem item, IAttributeGroup attrs, BindingInventory inventory)
	{
		menuItem = new WeakReference<MenuItem>(item);
		inventoryReference = new WeakReference<BindingInventory>(inventory);
		item.setOnMenuItemClickListener(this);	
		
		OnClick.initialize(attrs);
		IsVisible.initialize(attrs);

        commandValueResourceId = attrs.getResourceId(R.styleable.Button_CommandValue, -1);

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

        ResourceArgument arg = null;
        if (commandValueResourceId > 0)
            arg = new ResourceArgument(OnClick.getPropertyName(), commandValueResourceId);

		return OnClick.execute(arg);
	}

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem)
    {
        //nothing yet
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem)
    {
        //nothing yet...
        return false;
    }

    @Override
    public void initialise(View v, IAttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory, int flags)
    {
        //not used
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

    @Override
    public BindingInventory getBindingInventory()
    {
        if (inventoryReference.get() != null)
            return  inventoryReference.get();
        return null;
    }

    @Override
    public UIHandler getUIHandler()
    {
        //not used
        return null;
    }

    @Override
    public int getBindingFlags() {
        return Flags.NO_FLAGS;
    }

    @Override
    public boolean isSynthetic()
    {
        return false;
    }

    @Override
    public void markAsSynthetic(BindingInventory inventory)
    {

    }

}
