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

import amvvm.implementations.observables.SelectedArgument;
import amvvm.implementations.ui.UIEvent;
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IAttributeBridge;
import amvvm.implementations.ViewFactory;
import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IProxyObservableObject;

import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import amvvm.R;
import amvvm.interfaces.IViewBinding;

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
    private Boolean enabledOverride;

    public final UIEvent<SelectedArgument> OnSelected_event = new UIEvent<SelectedArgument>(this, R.styleable.ListView_OnSelected);
    public final UIProperty<Integer> OnSelected_property = new UIProperty<Integer>(this, R.styleable.ListView_OnSelected);

    private Boolean isEvent = null;

    @Override
    protected void initialise(IAttributeBridge attributeBridge)
    {
        super.initialise(attributeBridge);

        AbsListView lv = (AbsListView)(ViewGroup)getWidget();

        IAttributeGroup ta = attributeBridge.getAttributes(R.styleable.ListView);

        OnSelected_event.initialize(ta);
        OnSelected_property.initialize(ta);

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
        if (isEvent == null)
        {
            isEvent = getBindingInventory().isCommand(OnSelected_event.getPath());
        }
        if (isEvent)
        {
            SelectedArgument arg = new SelectedArgument(OnSelected_event.getPropertyName(), position);
            OnSelected_event.execute(arg);
        }
        else
        {
            OnSelected_property.sendUpdate(position);
        }
	}

}
