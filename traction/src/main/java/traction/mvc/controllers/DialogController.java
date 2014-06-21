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

package traction.mvc.controllers;

import traction.mvc.implementations.ViewFactory;
import traction.mvc.interfaces.IViewBinding;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
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
public abstract class DialogController
extends DialogFragment
{
    private Dialog tempDialog;

	/**
	 * Helper
	 */
	protected final ControllerHelper View = new ControllerHelper(this)
	{
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
                return View.onCreateOptionsMenu(menu);
            }
        };

        View.invalidateMenu();
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
	public void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater notUsed, ViewGroup container, Bundle savedInstanceState) 
	{
        View v = View.inflateView(View.getContentView(), container, true);
        IViewBinding vb = ViewFactory.getViewBinding(v);
        if (vb != null) {
            View.ensureMenuInflator(vb.getBindingInventory());
        }
        return v;
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
        View.registerFragmentToActivity(this, activity);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        View.unregisterFragmentFromActivity(this, getActivity());
    }
}
