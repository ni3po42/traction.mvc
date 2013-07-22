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
import ni3po42.android.amvvm.implementations.observables.ObservableObject;
import ni3po42.android.amvvm.implementations.observables.PropertyStore;
import ni3po42.android.amvvm.implementations.ui.menubinding.MVVMMenuInflater;
import ni3po42.android.amvvm.interfaces.IAccessibleFragmentManager;
import ni3po42.android.amvvm.interfaces.IObjectListener;
import ni3po42.android.amvvm.interfaces.IObservableObject;
import ni3po42.android.amvvm.interfaces.IViewModel;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Implements all logic for a view model to function. Activities and Fragments can wrap this logic to become the base object to handle
 * the AMVVM framework. See {@code ActivityViewModel}, {@code ViewModel} and {@code DialogViewModel} for examples on how to implement them
 * in your own base, or just use these classes as your base yourself.
 * @author Tim Stratton
 *
 * @param <T>
 */
public abstract class ViewModelHelper<T extends Activity & IViewModel>
extends ObservableObject
implements IAccessibleFragmentManager
{
	/**
	 * property cache
	 */
	private PropertyStore store = new PropertyStore();
	
	/**
	 * menu inflator
	 */
	private MVVMMenuInflater menuInflater;
	
	/**
	 * menu layout id
	 */
	private int menuLayoutId;
	
	/**
	 * access to layout inflator with custom viewfactory2 added
	 */
	private LayoutInflater injectedInflater;
	
	/**
	 * Access to an activity implementing IviewModel
	 * @return
	 */
	protected abstract T getActivity();
	
	/**
	 * forces menu to invalidate on activity
	 */
	private IObjectListener invalidateMenuListener = new IObjectListener()
	{
		@Override
		public void onEvent(Object source, EventArg arg)
		{
			getActivity().invalidateOptionsMenu();
		}		
	};
	
	/**
	 * runnable used to force pending fragment transaction to execute immediately
	 */
	private Runnable runTransactionNow = new Runnable()
	{	
		@Override
		public void run()
		{
			getActivity().getFragmentManager().executePendingTransactions();
		}
	};
	
	/**
	 * By default, the hijacked 'this' will be the activity. for fragments, you can override this and return a
	 * fragment implementing IViewModel
	 */
	@Override
	public IObservableObject getSource()
	{
		return getActivity();
	}
	
	/**
	 * inflates a view and registers the view-model
	 * @param layoutResID
	 * @param parent : parent view. May be null in certain cases
	 * @return : a inflated view or null if inflation can not happen
	 */
	public View inflateView(int layoutResID, ViewGroup parent)
	{
		View v = getActivity().getLayoutInflater().inflate(layoutResID, parent, false);		
		ViewFactory.RegisterContext(v, getSource());
		return v;
	}
		
	/**
	 * links fragments registered to the binding inventory 
	 */
	@Override
	public void linkFragments(BindingInventory inventory)
	{
		inventory.linkFragments(getActivity().getFragmentManager());
	}
	
	@Override
	public PropertyStore getPropertyStore()
	{
		return store;
	}
	
	public MenuInflater getMenuInflater()
	{
		if (menuInflater == null)
			menuInflater = new MVVMMenuInflater(getActivity(),getSource());
		return menuInflater;
	}
	
	/**
	 * The framework assumes all viewmodel-fragments are maintained in the fragment manager. use this method to access view-models
	 * @param memberName
	 * @return : view-model if found
	 */
	public ViewModel getViewModel(String memberName)
	{
		Fragment f = getActivity().getFragmentManager().findFragmentByTag(memberName);
		if (!(f instanceof ViewModel))
			return null;
		return (ViewModel)getActivity().getFragmentManager().findFragmentByTag(memberName);
	}
	
	/**
	 * The framework assumes all viewmodel-fragments are maintained in the fragment manger. use this method to set view-models
	 * @param memberName
	 * @param newViewModel
	 */
	public void setViewModel(String memberName, ViewModel newViewModel)
	{
		ViewModel oldViewModel = getViewModel(memberName);
	
		FragmentTransaction trans = getActivity().getFragmentManager().beginTransaction();
		if (oldViewModel != null && !oldViewModel.equals(newViewModel))			
			trans.remove(oldViewModel);
		if (!newViewModel.equals(oldViewModel))
			trans.add(newViewModel, memberName);
		trans.commit();
		
		getActivity().runOnUiThread(runTransactionNow);
		
		notifyListener(memberName, oldViewModel, newViewModel);
	}
	
	/**
	 * Re-sync the menu with it's model/view-model data
	 * @param menu
	 * @return
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (menuLayoutId == 0)
			return false;
		
		getMenuInflater().inflate(menuLayoutId, menu);
		getSource().unregisterListener(null, invalidateMenuListener);
		getSource().notifyListener();
		getSource().registerListener(null, invalidateMenuListener);
		
		return true;
	}
	
	/**
	 * Gets menu layout
	 * @return
	 */
	public int getMenuLayoutId()
	{
		return menuLayoutId;
	}
	
	/**
	 * sets menu layout
	 * @param i
	 */
	public void setMenuLayoutId(int i)
	{
		menuLayoutId = i;
		getActivity().invalidateOptionsMenu();
	}
	
	/**
	 * Hijack of Activity's getSystemService in intercept calls to get the layout inflater. Calls the default
	 * getSystemService if the call is not for the Layoutinflater.
	 * @param serviceName
	 * @return
	 */
	public Object getSystemService(String serviceName)
	{
		//if cache exist
		if (serviceName == Context.LAYOUT_INFLATER_SERVICE && injectedInflater != null)
		{
			return injectedInflater;
		}
		//if no cache yet...
		else if (serviceName == Context.LAYOUT_INFLATER_SERVICE && injectedInflater == null)
		{				
			LayoutInflater inflater = ((LayoutInflater)getActivity().getDefaultActivityService(serviceName)).cloneInContext(getActivity());
			
			//custom ViewFactory for building BindingInventory and what not..
			ViewFactory vf = new ViewFactory(inflater);
            inflater.setFactory2(vf);
            injectedInflater = inflater;			
			return injectedInflater;
		}
		//if anything other then layout inflater, go ahead and use the default.
		return getActivity().getDefaultActivityService(serviceName);
	}
		
}