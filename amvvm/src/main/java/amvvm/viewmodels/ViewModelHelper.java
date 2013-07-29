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

import amvvm.implementations.BindingInventory;
import amvvm.implementations.ViewFactory;
import amvvm.implementations.observables.ObservableObject;
import amvvm.implementations.observables.PropertyStore;
import amvvm.implementations.ui.menubinding.MVVMMenuInflater;
import amvvm.interfaces.IViewModel;
import amvvm.interfaces.IAccessibleFragmentManager;
import amvvm.interfaces.IObjectListener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.AttributeSet;
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
	public Object getSource()
	{
		return getActivity();
	}


    public void registerFragmentToActivity(Fragment fragment, Activity activity)
    {
        if (!(fragment instanceof IViewModel) || !(activity instanceof IViewModel))
            return;

        String tag = fragment.getTag();
        if (tag == null)
            return;

        ((IViewModel)fragment).getProxyObservableObject().registerAs(tag, (IViewModel)activity);
    }

    public void unregisterFragmentFromActivity(Fragment fragment, Activity activity)
    {
        if (!(fragment instanceof IViewModel) || !(activity instanceof IViewModel))
            return;

        String tag = fragment.getTag();
        if (tag == null)
            return;

        ((IViewModel)fragment).getProxyObservableObject().unregisterListener(tag, ((IViewModel) activity).getProxyObservableObject());
    }
	
	/**
	 * inflates a view and registers the view-model
	 * @param layoutResID
	 * @param parent : parent view. May be null in certain cases
	 * @return : a inflated view or null if inflation can not happen
	 */
	public View inflateView(int layoutResID, ViewGroup parent, boolean attacheToContext)
	{
		View v = getActivity().getLayoutInflater().inflate(layoutResID, parent, false);
        if (attacheToContext)
		    ViewFactory.RegisterContext(v, this);
		return v;
	}

    public void connectFragmentViewToParentView(Fragment fragment)
    {
        ViewFactory.ViewHolder vh = ViewFactory.getViewHolder(fragment.getView());
        if (vh == null || vh.inventory.getParentInventory() != null)
            return;

        ViewFactory.ViewHolder parentVH = ViewFactory.getViewHolder((View)fragment.getView().getParent());

        vh.inventory.setParentInventory(parentVH.inventory);
        ViewFactory.RegisterContext(fragment.getView(), this);
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
    public FragmentManager getFragmentManager() {
        return getActivity().getFragmentManager();
    }

    @Override
	public PropertyStore getPropertyStore()
	{
		return store;
	}
	
	public MenuInflater getMenuInflater()
	{
		if (menuInflater == null)
			menuInflater = new MVVMMenuInflater(getActivity(),this);
		return menuInflater;
	}
	
	/**
	 * The framework assumes all viewmodel-fragments are maintained in the fragment manager. use this method to access view-models
	 * @param memberName
	 * @return : view-model if found
	 */
	public <T extends IViewModel> T getViewModel(String memberName)
	{
		Fragment f = getActivity().getFragmentManager().findFragmentByTag(memberName);
		if (!(f instanceof IViewModel))
			return null;
		return (T)getActivity().getFragmentManager().findFragmentByTag(memberName);
	}
	
	/**
	 * The framework assumes all viewmodel-fragments are maintained in the fragment manger. use this method to set view-models
	 * @param memberName
	 * @param newViewModel
	 */
	public <T extends IViewModel> void setViewModel(String memberName, T newViewModel)
	{
		IViewModel oldViewModel = getViewModel(memberName);
	
		FragmentTransaction trans = getActivity().getFragmentManager().beginTransaction();
		if (oldViewModel != null && !oldViewModel.equals(newViewModel) && oldViewModel instanceof ViewModel)
			trans.remove((ViewModel)oldViewModel);
		if (!newViewModel.equals(oldViewModel) && newViewModel instanceof ViewModel)
			trans.add((ViewModel)newViewModel, memberName);
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
		unregisterListener(null, invalidateMenuListener);
		notifyListener();
		registerListener(null, invalidateMenuListener);
		
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

    public boolean shouldExecuteDefaultGetSystemService(String serviceName)
    {
        return serviceName != Context.LAYOUT_INFLATER_SERVICE;
    }

	public LayoutInflater getLayoutInflater()
	{
		//if cache exist
		if (injectedInflater != null)
		{
			return injectedInflater;
		}
		//if no cache yet...
		else
		{				
			LayoutInflater inflater = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).cloneInContext(getActivity());
			
			//custom ViewFactory for building BindingInventory and what not..
			ViewFactory vf = new ViewFactory(inflater);
            inflater.setFactory2(vf);
            injectedInflater = inflater;			
			return injectedInflater;
		}
	}

}