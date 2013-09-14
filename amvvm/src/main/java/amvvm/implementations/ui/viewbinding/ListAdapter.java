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

import amvvm.R;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ViewBindingFactory;
import amvvm.implementations.ViewFactory;
import amvvm.implementations.observables.SelectedArgument;
import amvvm.implementations.ui.UIEvent;
import amvvm.interfaces.IIndexable;
import amvvm.interfaces.IObservableList;
import amvvm.interfaces.IProxyObservableObject;
import amvvm.interfaces.IViewBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * A specialised adapter for handling IObservableLists
 * @author Tim Stratton
 *
 * @param <T> : item type of list
 * @param <S> : type of list 
 */
public abstract class ListAdapter<T, S extends IObservableList<T>>
extends BaseAdapter
    implements ProxyAdapter.IAdapterLayout
{

    private int templateId = -1;

    private final BindingInventory parentInventory;

    public ListAdapter(BindingInventory parentInventory)
    {
        this.parentInventory = parentInventory;
    }

    protected abstract S getList();

    @Override
    public void setLayoutId(int layoutId)
    {
        this.templateId = layoutId;
    }

    private ViewBindingFactory factory = new ViewBindingFactory();
	private LayoutInflater inflater;
	
	protected LayoutInflater getLayoutInflater(View parentView)
	{		
		if (inflater == null)
			inflater = LayoutInflater.from(parentView.getContext());
		return inflater;
	}
	
	/*
	 * when the adapter is requested to provide a view, it will bind that object to the view, like how the view-model or model
	 * itself is bounded
	 */
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
        //the adapter is expecting the parent to have a view holder. it should for the most part..
        //except the weird Spinner. There are at least two different parent ViewGroups that can
        //show up; the spinner and it's popup window. In the case the popup window (or some other
        //viewgroup) comes up, this will synthetically add a viewHolder with the proper objects
        //and register the list as the root context.

        if (ViewFactory.getViewBinding(parent) == null)
        {
            factory.createSyntheticFor(parent,null, parentInventory);
            //ViewFactory.RegisterContext(parent, c);
        }

		if (convertView == null)
		{	
			convertView = getLayoutInflater(parent).inflate(templateId, parent ,false);
		}

        if (ViewFactory.getViewBinding(parent).equals(ViewFactory.getViewBinding(convertView)))
        {
            throw new IllegalStateException("Possible child view not marked 'IsRoot'. Templates of AdapterViews must be marked 'IsRoot' = true.");
        }

		ViewFactory.RegisterContext(convertView, getList().get(position));

        //now to clean up. If a synthetic viewholder was created, detach the list from the 'root'
        if (ViewFactory.getViewBinding(parent).isSynthetic())
            ViewFactory.removeViewBinding(parent);

		return convertView;
	}

    @Override
	public int getCount()
	{
        S c = getList();
		if (c == null)
			return 0;
		return c.size();
	}
	
	@Override
	public Object getItem(int position)
	{
		if (getList() == null || position < 0 || position >= getList().size())
			return null;
		return getList().get(position);
	}
	
	@Override
	public long getItemId(int position)
	{
		if (getList() == null || position < 0 || position >= getList().size())
			return -1;
		return getList().get(position).hashCode();
	}

}