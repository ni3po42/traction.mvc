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

package ni3po42.android.amvvmdemo.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import ni3po42.android.amvvmdemo.R;
import ni3po42.android.amvvmdemo.viewmodels.MainViewModel;
import amvvm.viewmodels.ActivityViewModel;
import amvvm.interfaces.IViewModel;
import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.models.DemoViewModelChoice;


public class MainActivity extends ActivityViewModel
{
	private DemoViewModelChoice currentChoice;
	private boolean multiViewModelSupport = false;
		
	public ViewModel getMainViewModel()
	{
		return getViewModel("MainViewModel");
	}

	public void setMainViewModel(ViewModel mainViewModel)
	{
		setViewModel("MainViewModel", mainViewModel);
	}

	public DemoViewModelChoice getCurrentChoice()
	{
		return currentChoice;
	}

	public void setCurrentChoice(DemoViewModelChoice currentChoice)
	{			
		notifyListener("CurrentChoice", this.currentChoice, this.currentChoice = currentChoice);
		showNewChoice(currentChoice);
		this.currentChoice = null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		if (savedInstanceState != null)
			return;
		
		multiViewModelSupport =  getMainViewModel() != null;
			
		if (!multiViewModelSupport)
		{
			setMainViewModel(new MainViewModel());		
			getFragmentManager()
				.beginTransaction()
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.replace(R.id.main_view_id, getMainViewModel())										
				.addToBackStack(null)
				.commit();
		}
	}
	
	private void showNewChoice(DemoViewModelChoice currentChoice)
	{
		if (getCurrentChoice() == null)
			return;
		
		ViewModel viewModel = null;
		try
		{
			viewModel = getCurrentChoice().getViewModelType().newInstance();
		}
		catch (InstantiationException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}
		if (viewModel == null)
			throw new RuntimeException("Cannot find View Model : " + getCurrentChoice().getViewModelType().getName());
		
		getFragmentManager().beginTransaction()
		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
		.replace(R.id.main_view_id, viewModel)
		.addToBackStack(null)
		.commit();	
	}
	
	@Override
	public void onBackPressed()
	{	
		super.onBackPressed();
		getFragmentManager().popBackStack();
		setCurrentChoice(null);
	}
	
}
