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

package ni3po42.android.amvvm.implementations.ui.viewbinding;

import ni3po42.android.amvvm.implementations.ViewFactory;
import ni3po42.android.amvvm.interfaces.IObservableList;
import ni3po42.android.amvvm.interfaces.IObservableObject;
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
public abstract class InternalAdapter<T extends IObservableObject, S extends IObservableList<T>> 
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
		//ViewFactory.DetachContext(convertView);
		
		if (convertView == null)
		{	
			convertView = getLayoutInflater(parent).inflate(getItemTemplateId(), parent,false);			
		}	
		ViewFactory.RegisterContext(convertView, getList().get(position));
		
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