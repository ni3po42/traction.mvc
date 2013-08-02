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

import amvvm.implementations.AttributeBridge;
import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ViewFactory;
import amvvm.implementations.ViewFactory.ViewHolder;
import amvvm.interfaces.IProxyObservableObject;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import amvvm.R;

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
public class ListViewBinding<T extends IProxyObservableObject>
extends AdapterViewBinding<T, AbsListView, ListAdapter>
implements OnItemClickListener
{	
	//stores the path to the property that denotes if the item is selected.
	private String selectionPath;
	private String enabledPath;
    private Boolean enabledOverride;

	public ListViewBinding()
	{
		
	}

    @Override
    protected void initialise(AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {
        super.initialise(attributeBridge, uiHandler, inventory);

        TypedArray ta = attributeBridge.getAttributes(R.styleable.ListView);
		//this selected attribute is only valid if the choice mode is not single or none, only multiple.
		if (getWidget().getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE ||
				getWidget().getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE)
		{
			selectionPath = ta.getString(R.styleable.ListView_Selected);
		}

        if (ta.hasValue(R.styleable.ListView_SelectionEnabled))
        {
            enabledPath = ta.getString(R.styleable.ListView_SelectionEnabled);
            if (enabledPath == null || enabledPath.equals("") ||
                    enabledPath.toLowerCase().equals("true") || enabledPath.toLowerCase().equals("false"))//if must be a boolean
            {
                enabledPath = null;
                enabledOverride = ta.getBoolean(R.styleable.ListView_SelectionEnabled, true);
            }
        }

        ta.recycle();
		getWidget().setOnItemClickListener(this);
	}
	
	@Override
	protected boolean isSelectionEnabled()
	{	
		//this effectively disables the 'SelectedItem' element and allows the binding to instead react to changes in multiple items
		//instead of just one.
		return getWidget().getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE ||
				getWidget().getChoiceMode() == AbsListView.CHOICE_MODE_NONE;
	}

    @Override
    protected boolean isSelectionEnabledAt(int position)
    {
        if (enabledPath == null && enabledOverride != null)
            return enabledOverride;
        else if (enabledPath != null)
        {
            View view = getWidget().getChildAt(position);
            ViewHolder vh = ViewFactory.getViewHolder(view);
            Object value = vh.inventory.DereferenceValue(enabledPath);
            if (value instanceof Boolean)
                return (Boolean)value;
        }
        return super.isSelectionEnabledAt(position);
    }

    @Override
	public void detachBindings()
	{
		getWidget().setOnItemClickListener(null);
		super.detachBindings();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(final AdapterView<?> view, final View childView, final int position, final long arg3)
	{
		Object obj = view.getItemAtPosition(position);
		
		//if SelectedItem is not active...
		if (!isSelectionEnabled())
		{
			//get the binding inventory for the child item..
			ViewHolder vh = ViewFactory.getViewHolder(childView);
			if (vh != null && vh.inventory != null)
			{
				//look up the current value for that property
				Object b = vh.inventory.DereferenceValue(selectionPath);
				
				//confirm it's a boolean property..
				if (b != null && (b instanceof Boolean || b.getClass().equals(boolean.class)))
				{
					//..flip it's state
					Boolean bb = (Boolean)b;
					vh.inventory.sendUpdate(selectionPath, !bb);
				}
			}
		}
		//..however is SelectedItem is active...
		else
		{		
			//..just do your normal thing.
			SelectedItem.sendUpdate((T)obj);
		}
	}

}
