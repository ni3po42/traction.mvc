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

package amvvm.viewmodels;

import amvvm.interfaces.IObservableObject;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.observables.PropertyStore;
import amvvm.interfaces.IObjectListener;
import amvvm.interfaces.IProxyObservableObject;
import amvvm.interfaces.IViewModel;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.util.Property;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Base class for view model activities. In AMVVM, the activity is used to manage the different view model (fragments), but can act as
 * a view-model for very simple cases. It's not necessary to use this class as your base for activities, but you must use an activity that
 * implements IViewModel, and you can wrap it around the ViewModelHelper instead of rewriting all the logic again. Use this as your base
 * class or use it as an example for altering your current base activity.  
 * @author Tim Stratton
 *
 */
@SuppressLint("Registered")
public abstract class ActivityViewModel
extends Activity
implements IViewModel, IObservableObject
{	
	/**
	 * Helper object: houses almost all the logic the activity will need 
	 */
	private final ViewModelHelper<ActivityViewModel> helper = new ViewModelHelper<ActivityViewModel>()
	{
		@Override
		protected ActivityViewModel getActivity()
		{
			//get the helper access to this activity
			return ActivityViewModel.this;
		}			
	};
	
	@Override
	public IObservableObject getProxyObservableObject() 
	{
		return helper;
	};
	
	@Override
	public void linkFragments(BindingInventory inventory) 
	{
		helper.linkFragments(inventory);
	};

    @Override
	public Object getDefaultActivityService(String name)
	{
		//note the super: this calls the base activities getSystemService instead of the overridden one here.
		return super.getSystemService(name);
	}
		
	@Override
	public void setContentView(int layoutResID)
	{
		setContentView(helper.inflateView(layoutResID, null, true));
	}
	
	@Override
	public void setMenuLayout(int id)
	{
		helper.setMenuLayoutId(id);
	}


    @Override
    public <T extends IViewModel> T getViewModel(String memberName)
    {
        return helper.getViewModel(memberName);
    }

    @Override
    public <T extends IViewModel> void setViewModel(String memberName, T viewModel)
    {
        helper.<T>setViewModel(memberName, viewModel);
    }

    @Override
	public Object getSystemService(String name)
	{	
		return helper.getSystemService(name);
	}
		
	@Override
	public MenuInflater getMenuInflater() 
	{	
		return helper.getMenuInflater();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return helper.onCreateOptionsMenu(menu);
	}

	/*
	 * All code from this point down are not neccessary, however it's being provided here as a convenience
	 */
	
	@Override
	public void onEvent(Object source, EventArg arg)
	{
		helper.onEvent(source, arg);
	}

	@Override
	public PropertyStore getPropertyStore()
	{
		return helper.getPropertyStore();
	}

	@Override
	public Property<Object, Object> getProperty(String name)
	{
		return helper.getProperty(name);
	}

	@Override
	public <T extends IProxyObservableObject> T registerAs(String propertyName, IProxyObservableObject parentObj)
	{
		return helper.<T>registerAs(propertyName, parentObj);
	}

	@Override
	public void notifyListener()
	{
		helper.notifyListener();
	}

	@Override
	public void notifyListener(String propertyName)
	{
		helper.notifyListener(propertyName);
	}

	@Override
	public void notifyListener(String propertyName, IProxyObservableObject oldPropertyValue, IProxyObservableObject newPropertyValue)
	{
		helper.notifyListener(propertyName, oldPropertyValue, newPropertyValue);
	}

	@Override
	public void addReaction(String localProperty, String reactsTo)
	{
		helper.addReaction(localProperty, reactsTo);
	}

	@Override
	public void clearReactions()
	{
		helper.clearReactions();
	}

	@Override
	public <T extends IProxyObservableObject> T registerListener(String sourceName, IObjectListener listener)
	{
		return helper.<T>registerListener(sourceName, listener);
	}

	@Override
	public void unregisterListener(String sourceName, IObjectListener listener)
	{
		helper.unregisterListener(sourceName, listener);
	}
	
	@Override
	public Object getSource()
	{
		return this;
	}
	
}
