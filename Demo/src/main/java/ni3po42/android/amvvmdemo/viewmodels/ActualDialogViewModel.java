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

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import amvvm.implementations.observables.SimpleCommand;
import amvvm.interfaces.ICommand;
import ni3po42.android.amvvmdemo.R;
import amvvm.implementations.observables.Command;
import amvvm.viewmodels.DialogViewModel;

public class ActualDialogViewModel extends DialogViewModel
{
	private String someText;
	public String getSomeText()
	{
		return someText;
	}
	
	public void setSomeText(String t)
	{
		someText = t;
		notifyListener("SomeText");
	}
	
	public final SimpleCommand Close = new SimpleCommand()
	{
        @Override
        protected void onExecuted(CommandArgument commandArgument)
        {
            dismiss();
        }
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setSomeText("You opened a Dialog!");
		setContentView(R.layout.dialogviewmodel);
	};
	
	
}
