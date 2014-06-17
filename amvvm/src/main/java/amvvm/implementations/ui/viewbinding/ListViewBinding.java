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

package amvvm.implementations.ui.viewbinding;

import amvvm.implementations.ui.UIEvent;
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.ICommand;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Binding for a list view; it just extends the AdapterViewBinding to add a special attribute: 'Selected'.
 * The 'Selected' attribute is an 'item' relative member path, letting the listview know which property to look for when determining
 * if it is marked selected or not. It didn't need an actual ui element because the path is not dynamic.
 * @author Tim Stratton
 *
 * @param <T>
 * 
 * Exposes the following properties:
 * Selected - Path to the child items's boolean property to hold it's selection state
 */
public class ListViewBinding<T>
extends AdapterViewBinding<T>
implements OnItemClickListener
{	
	//stores the path to the property that denotes if the item is selected.
	private String enabledPath;

    public final UIEvent<ICommand.CommandArgument> OnSelected = new UIEvent<ICommand.CommandArgument>(this,"OnSelected");
    public final UIProperty<Integer> SelectedIndex = new UIProperty<Integer>(this, "SelectedIndex");

    @Override
    protected void initialise() throws Exception
    {
        super.initialise();
        if (getTagProperties().has("SelectionEnabled"))
        {
            enabledPath = getTagProperties().getString("SelectionEnabled");
            if (enabledPath == null || enabledPath.equals("") ||
                    enabledPath.toLowerCase().equals("true") || enabledPath.toLowerCase().equals("false"))//if must be a boolean
            {
                enabledPath = null;
            }
        }
		getWidget().setOnItemClickListener(this);
	}

    @Override
	public void detachBindings()
	{
        if (getWidget() != null)
		    getWidget().setOnItemClickListener(null);
		super.detachBindings();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(final AdapterView<?> view, final View childView, final int position, final long key)
	{
        SelectedIndex.sendUpdate(position);
        OnSelected.execute(null);
	}

}
