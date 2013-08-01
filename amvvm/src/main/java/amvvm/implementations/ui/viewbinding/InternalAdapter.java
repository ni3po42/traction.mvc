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

import amvvm.implementations.ViewFactory;
import amvvm.interfaces.IObservableList;
import amvvm.interfaces.IProxyObservableObject;

import android.app.Activity;
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
public abstract class InternalAdapter<T extends IProxyObservableObject, S extends IObservableList<T>> 
extends BaseAdapter
{	
	/**
	 * This class follows a template patter, requiring access to a list and a resource id for the layout of the item
	 */
	
	/**
	 * internal reference to the current list
	 * @return
	 */
	protected abstract S getList();
	
	/**
	 * reference to a resource id for a layout
	 * @return
	 */
	protected abstract int getItemTemplateId();
		
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
        if (ViewFactory.getViewHolder(parent) == null)
        {
            ViewFactory.ViewHolder vh = ViewFactory.createViewHolderFor(parent,null);
            vh.isRoot = true;
            vh.ignoreChildren = false;
            ViewFactory.RegisterContext(parent, getList());
        }

		if (convertView == null)
		{	
			convertView = getLayoutInflater(parent).inflate(getItemTemplateId(), parent ,false);
		}	
		ViewFactory.RegisterContext(convertView, getList().get(position));

        //now to clean up. If a synthetic viewholder was created, detach the list from the 'root'
        if (ViewFactory.getViewHolder(parent).isSynthetic())
            ViewFactory.DetachContext(parent);

		return convertView;
	}



	
	@Override
	public int getCount()
	{
		if (getList() == null)
			return 0;
		return getList().size();
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