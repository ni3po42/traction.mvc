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

package ni3po42.android.amvvmdemo.viewmodels;

import android.os.Bundle;
import ni3po42.android.amvvmdemo.R;
import amvvm.implementations.observables.Command;
import amvvm.viewmodels.ViewModel;

public class DemoADialogViewModel extends ViewModel
{
	public final Command<Object> OpenDialog = new Command<Object>()
	{	
		@Override
		protected void onExecuted(Object arg)
		{
			ActualDialogViewModel dialog = new ActualDialogViewModel();
			dialog.show(getFragmentManager(), "dialog");
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialoglauncher);
	};
}
