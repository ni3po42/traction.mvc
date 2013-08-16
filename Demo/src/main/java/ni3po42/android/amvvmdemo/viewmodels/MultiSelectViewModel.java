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

import java.util.ArrayList;
import android.os.Bundle;
import android.widget.Toast;

import amvvm.implementations.observables.SimpleCommand;
import amvvm.interfaces.ICommand;
import ni3po42.android.amvvmdemo.R;
import amvvm.implementations.observables.Command;
import amvvm.implementations.observables.ObservableList;
import amvvm.interfaces.IObjectListener;
import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.models.SelectableItem;

public class MultiSelectViewModel extends ViewModel
{
	
	public final ObservableList<SelectableItem> Items = 
			new ObservableList<SelectableItem>(new ArrayList<SelectableItem>());

	public MultiSelectViewModel()
	{
		Items.clear();
		for (int i=0;i<12;i++)
		{
			SelectableItem s = new SelectableItem();
			s.registerListener("SubItem", selectectCountListener);
			Items.add(s);
		}
	}
	
	public int getSelectedCount()
	{
		int count = 0;
		for(int i=0;i<Items.getCount();i++)
		{
			if (Items.get(i).isSelected())
				count++;
		}
		return count;
	}
	
	final IObjectListener selectectCountListener = new IObjectListener()
	{				
		@Override
		public void onEvent(EventArg arg)
		{
			notifyListener("SelectedCount");
		}
	};
	
	public final SimpleCommand CountSelected = new SimpleCommand()
	{
        @Override
        protected void onExecuted(CommandArgument commandArgument)
        {
            Toast.makeText(getActivity(), "There are "+getSelectedCount()+" item(s) selected.", Toast.LENGTH_SHORT).show();
        }
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.multiselection);
		setMenuLayout(R.menu.multiselectoptions);
	}
	
}
