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

import amvvm.implementations.ViewFactory;
import amvvm.interfaces.IObservableObject;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.observables.PropertyStore;
import amvvm.interfaces.IObjectListener;
import amvvm.interfaces.IPropertyStore;
import amvvm.interfaces.IProxyObservableObject;
import amvvm.interfaces.IProxyViewModel;
import amvvm.interfaces.IViewModel;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Property;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
implements IProxyViewModel, IProxyObservableObject
{
    private Dialog tempDialog;

	/**
	 * Helper
	 */
	private final ViewModelHelper helper = new ViewModelHelper()
	{
		@Override
		public Object getSource() 
		{
			//hijack 'this', makes helper think 'this' is the dialog fragment			
			return DialogViewModel.this;
		}

        @Override
        protected <T extends Activity & IViewModel> T getActivity()
        {
            //get access to the activity for the view.
            //IT MUST ALSO BE AN ACTIVITY THAT IMPLEMENTS IViewModel!!!!
            if (DialogViewModel.this.getActivity() instanceof IViewModel)
                return (T)DialogViewModel.this.getActivity();
            return null;
        }

        @Override
        protected void invalidateMenu()
        {
            if (getDialog() != null)
                getDialog().invalidateOptionsMenu();
        }

        @Override
        public void setMenuLayout(int id)
        {
            setHasOptionsMenu(id > 0);
            super.setMenuLayout(id);
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        tempDialog = new Dialog(getActivity(), getTheme())
        {
            @Override
            public boolean onCreateOptionsMenu(Menu menu)
            {
                return helper.onCreateOptionsMenu(menu);
            }
        };

        helper.invalidateMenu();
        return tempDialog;
    }

    @Override
    public Dialog getDialog()
    {
        Dialog d = super.getDialog();
        if (d != null)
            tempDialog = null;

        return (d == null) ? tempDialog : d;
    }

    @Override
	public IObservableObject getProxyObservableObject()
	{
		return helper;
	}

    @Override
    public IViewModel getProxyViewModel()
    {
        return helper;
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
		return helper.inflateView(helper.getContentView(), container, true);
	}
		
	@Override
	public void onDestroyView() 
	{		
		super.onDestroyView();	
		ViewFactory.DetachContext(getView());
	}

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        helper.registerFragmentToActivity(this, activity);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        helper.unregisterFragmentFromActivity(this, getActivity());
    }
}
