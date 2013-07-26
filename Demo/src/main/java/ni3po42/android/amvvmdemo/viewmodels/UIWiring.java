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
import android.widget.Toast;
import ni3po42.android.amvvmdemo.R;
import amvvm.implementations.observables.Command;
import amvvm.viewmodels.ViewModel;

public class UIWiring extends ViewModel
{
	private boolean mainFlag;
	private int upperBound;
	private int currentInteger;
	
	public final int Smallest = 0;
	public final int Largest = 20;
	
	public UIWiring()
	{
		addReaction("Image", "MainFlagOn");
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uiwiring);
	}
	
	public int getImage()
	{
		if (isMainFlagOn())
			return R.drawable.ic_launcher;
		else
			return 0;
	}
		
	public boolean isMainFlagOn()
	{
		return mainFlag;
	}

	public void setMainFlagOn(boolean mainFlag)
	{
		this.mainFlag = mainFlag;
		notifyListener("MainFlagOn");
	}
	
	public int getUpperBound()
	{
		return upperBound;
	}

	public void setUpperBound(int upperBound)
	{
		this.upperBound = upperBound;
		notifyListener("UpperBound");
	}

	public int getCurrentInteger()
	{
		return currentInteger;
	}

	public void setCurrentInteger(int currentInteger)
	{
		this.currentInteger = currentInteger;
		notifyListener("CurrentInteger");
	}

	public final Command<Object> MyEvent = new Command<Object>()
	{	{registerAs("MyEvent", UIWiring.this);}		
		@Override
		protected void onExecuted(Object arg)
		{
			Toast
				.makeText(getActivity(), "MainFlag is " + (isMainFlagOn() ? "on" : "off"), Toast.LENGTH_SHORT)
				.show();
		}
	};
	
}
