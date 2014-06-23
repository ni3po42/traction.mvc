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

package traction.mvc.implementations.ui.viewbinding;

import traction.mvc.implementations.ui.UIProperty;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Binding for a list view; it just extends the AdapterViewBinding to add a special attribute: 'Selected'.
 * The 'Selected' attribute is an 'item' relative member path, letting the listview know which property to look for when determining
 * if it is marked selected or not. It didn't need an actual ui element because the path is not dynamic.
 * @author Tim Stratton
 *
 * 
 * Exposes the following properties:
 * SelectedItem - Path to the child item that is selected
 * SelectedItemIndex - Path to child item's index that was selected
 */
public class ListViewBinding
extends AdapterViewBinding
implements OnItemClickListener
{
    public final UIProperty<Integer> SelectedItemIndex = new UIProperty<Integer>(this, "SelectedItemIndex");
    public final UIProperty<Object> SelectedItem = new UIProperty<Object>(this, "SelectedItem");

    @Override
    protected void initialise() throws Exception
    {
        super.initialise();
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
        SelectedItemIndex.sendUpdate(position);
        SelectedItem.sendUpdate(view.getItemAtPosition(position));
	}

}
