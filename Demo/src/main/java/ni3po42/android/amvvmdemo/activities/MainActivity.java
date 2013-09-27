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

import amvvm.implementations.observables.Command;
import amvvm.implementations.observables.SelectedArgument;
import amvvm.implementations.observables.SimpleCommand;
import amvvm.interfaces.IObjectListener;
import ni3po42.android.amvvmdemo.R;
import ni3po42.android.amvvmdemo.viewmodels.MainViewModel;
import amvvm.viewmodels.ActivityViewModel;
import amvvm.interfaces.IViewModel;
import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.models.DemoViewModelChoice;


public class MainActivity extends ActivityViewModel
{
	public ViewModel getMainViewModel()
	{
		return getViewModel("MainViewModel");
	}

	public void setMainViewModel(ViewModel mainViewModel)
	{
		setViewModel("MainViewModel", mainViewModel);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		if (savedInstanceState != null)
			return;
		
		boolean noMultiViewModelSupport =  getMainViewModel() == null;
			
		if (noMultiViewModelSupport)
		{
            MainViewModel mvm = new MainViewModel();
            Bundle args = new Bundle();
            args.putBoolean(MainViewModel.NoMultiViewModelSupport, true);
            mvm.setArguments(args);

            setMainViewModel(mvm);

			getFragmentManager()
				.beginTransaction()
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.replace(R.id.main_view_id, getMainViewModel())
				.commit();
		}
	}
}
