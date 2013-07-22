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

package ni3po42.android.amvvm.implementations.ui.viewbinding;

import ni3po42.android.amvvm.interfaces.IObservableObject;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Extends the AdapterViewBinding to handle binding to a Spinner.
 * @author Tim Stratton 
 *
 * @param <T> : type of item in the spinner
 */
public class SpinnerViewBinding<T extends IObservableObject>
extends AdapterViewBinding<T, Spinner, SpinnerAdapter>
implements OnItemSelectedListener
{	
	//There was some quirkyness to getting the spinner to work properly. The biggest issue was the wrong value
	//being set as the selected value. With how Android handles creating the views for it and calling the item selected
	//listener when a non ui selection was made, it was needed to add and remove the selection listener at different points
	//to keep the correct item selected in the spinner and avoid weird infinite tasks queueing to be executed.
	
	@Override
	public void detachBindings()
	{
		getWidget().setOnItemSelectedListener(null);
		super.detachBindings();
	}

	@Override
	protected void disableListeners()
	{
		if (getWidget() == null)
			return;
		getWidget().setOnItemSelectedListener(null);
	}
	
	@Override
	protected void enableListeners()
	{
		if (getWidget() == null)
			return;
		getWidget().setOnItemSelectedListener(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onItemSelected(AdapterView<?> view, View arg1, int position, long arg3)
	{
		Object obj = view.getItemAtPosition(position);	
		SelectedItem.sendUpdate((T)obj);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{		
		SelectedItem.sendUpdate(null);	
	}
}
