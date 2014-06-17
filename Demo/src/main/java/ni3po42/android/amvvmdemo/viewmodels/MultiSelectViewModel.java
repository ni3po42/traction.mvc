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

import amvvm.implementations.observables.ObservableList;
import amvvm.implementations.observables.SimpleCommand;
import ni3po42.android.amvvmdemo.R;
import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.models.SelectableItem;

public class MultiSelectViewModel extends ViewModel
{
	
	public final ObservableList<SelectableItem> Items =
			new ObservableList<SelectableItem>(new ArrayList<SelectableItem>());

	public MultiSelectViewModel()
	{
		Items.clear();
        for(int i=0;i<12;i++)
            Items.add(new SelectableItem());
	}

    private int selectedCount;
    public int getSelectedCount()
    {
        return selectedCount;
    }

    public int getSelectedIndex(){return -1;}
    public void setSelectedIndex(int index)
    {
        if (Items.getCount() <= index)
            return;
        if (!Items.get(index).isSelected())
            selectedCount++;
        else
            selectedCount--;
        Items.get(index).setSelected(!Items.get(index).isSelected());
        notifyListener("SelectedCount");
    }


	public final SimpleCommand CountSelected = new SimpleCommand()
	{
        @Override
        protected void onExecuted(CommandArgument commandArgument)
        {
            String txt = myString == null ? "" : myString;
            Toast.makeText(getActivity(), "There are "+ selectedCount+" item(s) selected. "+txt, Toast.LENGTH_SHORT).show();
        }
    };

    public int getExampleIcon()
    {
        return R.drawable.ic_launcher;
    }

    private String myString;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.multiselection);
		setMenuLayout(R.menu.multiselectoptions);
	}

    public String getMyString() {
        return myString;
    }

    public void setMyString(String myString) {
        this.myString = myString;
        notifyListener("MyString");
    }
}
