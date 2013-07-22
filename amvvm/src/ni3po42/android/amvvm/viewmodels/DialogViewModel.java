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

package ni3po42.android.amvvm.viewmodels;

import ni3po42.android.amvvm.implementations.BindingInventory;
import ni3po42.android.amvvm.implementations.ViewFactory;
import ni3po42.android.amvvm.implementations.observables.PropertyStore;
import ni3po42.android.amvvm.interfaces.IObjectListener;
import ni3po42.android.amvvm.interfaces.IObservableObject;
import ni3po42.android.amvvm.interfaces.IViewModel;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Base class for view model dialog fragments. In AMVVM, the fragment represents a view-model; it will assign with layout to use and pass
 * model data to the view and access data/command back from the view. It's not necessary to use this class as your base for view-model
 * dialog fragments, but you must use a dialog fragment that implements IViewModel, and you can wrap it around the ViewModelHelper 
 * instead of rewriting all the logic again. Use this as your base class or use it as an example for altering 
 * your current base dialog fragment.  
 * @author Tim Stratton
 *
 */
public abstract class DialogViewModel
extends DialogFragment
implements IViewModel
{	
	private int contentViewId = 0;
	
	/**
	 * Helper
	 */
	private final ViewModelHelper<ActivityViewModel> helper = new ViewModelHelper<ActivityViewModel>()
	{
		@Override
		protected ActivityViewModel getActivity()
		{
			//get access to the activity for the view.
			//IT MUST ALSO BE AN ACTIVITY THAT IMPLEMENTS IViewModel!!!!
			if (DialogViewModel.this.getActivity() instanceof ActivityViewModel)
				return (ActivityViewModel)DialogViewModel.this.getActivity();
			return null;
		}
		
		@Override
		public IObservableObject getSource() 
		{
			//hijack 'this', makes helper think 'this' is the dialog fragment			
			return DialogViewModel.this;
		};
	};
	
	@Override
	public void linkFragments(BindingInventory inventory) 
	{
		inventory.linkFragments(getFragmentManager());
	}
		
	@Override
	public IObservableObject getSource()
	{
		return helper.getSource();
	}

	@Override
	public PropertyStore getPropertyStore()
	{
		return helper.getPropertyStore();
	}
	
	@Override
	public void setContentView(int layoutResID)
	{
		contentViewId = layoutResID;
	}
	
	@Override
	public void setMenuLayout(int id)
	{
		//helper.setMenuLayoutId(id);
		//supported yet...
	}
		
	@Override
	public ViewModel getViewModel(String memberName)
	{
		//return helper.getViewModel(memberName);
		//supported yet...
		return null;
	}
	
	@Override
	public void setViewModel(String memberName, ViewModel viewModel)
	{
		//helper.setViewModel(memberName, viewModel);
		//supported yet...
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
	public void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater notUsed, ViewGroup container, Bundle savedInstanceState) 
	{
		if (contentViewId == 0)//container will be null most likely
			return null;	
		return helper.inflateView(contentViewId, container);
	}
		
	@Override
	public void onDestroyView() 
	{		
		super.onDestroyView();	
		ViewFactory.DetachContext(getView());
	}

	@Override
	public Property<Object, Object> getProperty(String name)
	{
		return helper.getProperty(name);
	}

	@Override
	public void notifyListener(String propertyName, IObservableObject oldPropertyValue, IObservableObject newPropertyValue)
	{
		helper.notifyListener(propertyName, oldPropertyValue, newPropertyValue);
	}

	@Override
	public void notifyListener(String propertyName)
	{
		helper.notifyListener(propertyName);
	}

	@Override
	public void notifyListener()
	{
		helper.notifyListener();
	}

	@Override
	public <T extends IObservableObject> T registerListener(String sourceName, IObjectListener listener)
	{
		return helper.registerListener(sourceName, listener);
	}

	@Override
	public void unregisterListener(String sourceName, IObjectListener listener)
	{
		helper.unregisterListener(sourceName, listener);
	}
	
	@Override
	public void onEvent(Object source, EventArg arg)
	{
		helper.onEvent(source, arg);
	}
	
	@Override
	public <T extends IObservableObject> T registerAs(String propertyName, IObservableObject parentObj)
	{
		return helper.registerAs(propertyName, parentObj);
	}
	
	@Override
	public Object getDefaultActivityService(String name)
	{
		return helper.getActivity().getDefaultActivityService(name);
	}		
}
