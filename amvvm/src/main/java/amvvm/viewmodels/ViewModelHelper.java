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
import amvvm.implementations.ViewBindingFactory;
import amvvm.implementations.ViewFactory;
import amvvm.implementations.observables.ObservableObject;
import amvvm.implementations.observables.PropertyStore;
import amvvm.implementations.ui.menubinding.MenuInflater;
import amvvm.interfaces.IPropertyStore;
import amvvm.interfaces.IProxyViewModel;
import amvvm.interfaces.IViewBinding;
import amvvm.interfaces.IViewModel;
import amvvm.interfaces.IObjectListener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implements all logic for a view model to function. Activities and Fragments can wrap this logic to become the base object to handle
 * the AMVVM framework. See {@code ActivityViewModel}, {@code ViewModel} and {@code DialogViewModel} for examples on how to implement them
 * in your own base, or just use these classes as your base yourself.
 * @author Tim Stratton
 *
 */
public abstract class ViewModelHelper
extends ObservableObject
implements IViewModel
{
	/**
	 * property cache
	 */
	private IPropertyStore store = new PropertyStore();
	
	/**
	 * menu inflator
	 */
	private MenuInflater menuInflater;
	
	/**
	 * menu layout id
	 */
	private int menuLayoutId;

    /**
     * content layout id
     */
    private int contentViewId = 0;

	/**
	 * access to layout inflator with custom viewfactory2 added
	 */
	private LayoutInflater injectedInflater;
	
	/**
	 * Access to an activity implementing IviewModel
	 * @return
	 */
	protected abstract <T extends Activity & IViewModel> T getActivity();

    protected final SparseIntArray toastRootIds = new SparseIntArray();

	/**
	 * forces menu to invalidate on activity
	 */
	private IObjectListener invalidateMenuListener = new IObjectListener()
	{
        @Override
        public void onEvent(String propagationId)
        {
            if (propagationId == null)
                invalidateMenu();
        }
	};

    protected void invalidateMenu()
    {
        if (menuLayoutId > 0)
            getActivity().invalidateOptionsMenu();
    }

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

    /**
     * Registers a fragment to the activity in an IObservable sense.
     * When using Dynamic fragments, the get/setViewModel methods handle registration.
     * This method is for registering static fragments when they are attached to the activity.
     * See onAttach in the ViewModel class.
     * @param fragment : newly attached view-model
     * @param activity : activity containing view-model
     */
    public void registerFragmentToActivity(Fragment fragment, Activity activity)
    {
        if (!(fragment instanceof IViewModel) || !(activity instanceof IViewModel))
            return;

        String tag = fragment.getTag();
        if (tag == null)
            return;

        ((IViewModel)fragment).getProxyObservableObject().registerAs(tag, (IViewModel)activity);
    }

    /**
     * Un-registers a view-model from the activity. See onDetach in the ViewModel class
     * @param fragment : fragment detaching
     * @param activity : activity that's detaching the fragment
     */
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
     * @param attachToContext : true if view should force bind to model right now.
	 * @return : a inflated view or null if inflation can not happen
	 */
	public View inflateView(int layoutResID, ViewGroup parent, boolean attachToContext)
	{
        if (layoutResID <= 0)
            return null;

		View v = getActivity().getLayoutInflater().inflate(layoutResID, parent, false);
        if (attachToContext)
		    ViewFactory.RegisterContext(v, this);
		return v;
	}

    /**
     * The root view of fragments' viewbinding are disconnected from the parent layout's view
     * biding; This will reconnect them.
     * @param fragment
     */
    public void connectFragmentViewToParentView(Fragment fragment)
    {
        IViewBinding vb = ViewFactory.getViewBinding(fragment.getView());
        if (vb == null || vb.getBindingInventory().getParentInventory() != null)
            return;

        IViewBinding parentVB = ViewFactory.getViewBinding((View)fragment.getView().getParent());

        vb.getBindingInventory().setParentInventory(parentVB.getBindingInventory());
        ViewFactory.RegisterContext(fragment.getView(), this);
    }

    @Override
	public IPropertyStore getPropertyStore()
	{
		return store;
	}
	
	public android.view.MenuInflater getMenuInflater()
	{
		if (menuInflater == null)
			menuInflater = new MenuInflater(getActivity(),this);
		return menuInflater;
	}

    @Override
    public void setMenuLayout(int id)
    {
        menuLayoutId = id;
        invalidateMenu();
    }


    public JSONObject getMetaData()
    {
        try
        {
            if (getActivity() != null)
            {
                View v = getActivity().findViewById(android.R.id.content);
                if (v != null)
                {
                    IViewBinding vb = ViewFactory.getViewBinding(v);
                    if (vb.getTagProperties() != null && vb.getTagProperties().has("@meta"))
                    {
                        return vb.getTagProperties().getJSONObject("@meta");
                    }
                }
            }
        }
        catch (JSONException ex)
        {

        }
        return null;
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


    @Override
    public void setContentView(int layoutResID)
    {
        this.contentViewId = layoutResID;
    }

    public int getContentView()
    {
        return this.contentViewId;
    }

	public Object tweakServiceCall(String name, Object obj)
	{
        if (name.equals(Context.LAYOUT_INFLATER_SERVICE))
        {
            if (injectedInflater == null)
            {
                LayoutInflater inflater = ((LayoutInflater)obj).cloneInContext(getActivity());

                //custom ViewFactory for building BindingInventory and what not..
                inflater.setFactory2(new ViewFactory(inflater, new ViewBindingFactory()));
                injectedInflater = inflater;
            }
            return injectedInflater;
        }
        else
            return obj;
	}

    @Override
    public IViewModel getProxyViewModel() {
        return this;
    }

    @Override
    public Toast createBindableToast(int layoutId)
    {
        Toast t = new Toast(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        ViewGroup root = null;
        int rootId = toastRootIds.get(layoutId, -1);
        if (rootId != -1)
            root = (ViewGroup)  getActivity().findViewById(rootId);

        View layout = inflater.inflate(layoutId, root);
        ViewFactory.RegisterContext(layout, getSource());

        t.setView(layout);

        return t;
    }
}