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

import ni3po42.android.amvvm.R;
import ni3po42.android.amvvm.implementations.BindingInventory;
import ni3po42.android.amvvm.implementations.ui.UIBindedList;
import ni3po42.android.amvvm.implementations.ui.UIBindedProperty;
import ni3po42.android.amvvm.implementations.ui.UIHandler;
import ni3po42.android.amvvm.interfaces.IObservableList;
import ni3po42.android.amvvm.interfaces.IObservableObject;
import ni3po42.android.amvvm.interfaces.IUIElement.IUIUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

/**
 * Base view binding for AdapterViews.
 * 
 * Exposes the following properties:
 * Items - binds to IObservableList<T>
 * SelectedItem - sets a property of type <T> if view's choiceMode is single or none
 * Selected - path refers absolutely to property chain of the items in the Items collection if view's choiceMode is multiple or multiple modal
 * ItemTemplateId - id of layout for child view (at this time, only supports single type of view)
 * @author Tim Stratton
 *
 * @param <T> : item type
 * @param <V> : subtype of AdapterView<Q>
 * @param <Q> : subtype of Adapter
 */
public class AdapterViewBinding<T extends IObservableObject, V extends AdapterView<Q>, Q extends Adapter>
extends GenericViewBinding<V>
{	
	public final UIBindedList<T> Items = new UIBindedList<T>(this, R.styleable.AdapterView_Items);
	public final UIBindedProperty<T> SelectedItem = new UIBindedProperty<T>(this, R.styleable.AdapterView_SelectedItem);
	
	//layout to use for child views
	private int itemTemplateId = -1;
	private BaseAdapter internalAdapter;
	
	//local reference to this bounded list
	private IObservableList<T> currentList;
	
	//local reference to the selected item
	private T currentSelection;
	private int currentIndex=-1;
	
	/**
	 * get adapter for the adapterview
	 * @return
	 */
	protected BaseAdapter getInternalAdapter()
	{
		if (internalAdapter == null)
			internalAdapter = new InternalAdapter<T, IObservableList<T>>()
			{
				@Override protected IObservableList<T> getList()
				{
					return currentList;
				}

				@Override
				protected int getItemTemplateId()
				{
					return itemTemplateId;
				}
			
			};
		return internalAdapter;
	}
	
	//not used yet
	@Override
	public String getBasePath()
	{
		return Items.getPath();
	}
	
	/**
	 * gets type safe adapter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Q getAdapter()
	{
		return (Q)getInternalAdapter();
	}
	
	public AdapterViewBinding()
	{
		Items.setUIUpdateListener(new IUIUpdateListener<IObservableList<T>>()
		{
			@Override
			public void onUpdate(IObservableList<T> value)
			{
				//disable events that might cause an infinite loop...
				SelectedItem.disableRecieveUpdates();
				disableListeners();
				
				boolean callSetChanged = currentList != value;
				currentList = value;
				
				if (callSetChanged)
				{			
					//find index of new selection
					currentIndex = currentList.indexOf(currentSelection);
					if (currentIndex >= 0)
					{	
						getWidget().setSelection(currentIndex);
					}
					getInternalAdapter().notifyDataSetChanged();
				}
				
				//re-enable events
				enableListeners();
				SelectedItem.enableRecieveUpdates();
				//else
					//getInternalAdapter().notifyDataSetInvalidated();
			}
			
		});
		
		SelectedItem.setUIUpdateListener(new IUIUpdateListener<T>()
		{
			@Override
			public void onUpdate(T value)
			{
				if (getWidget() == null || currentList == null)
				{
					//even if the current view or list is not bound yet, keep track of current
					//item, so when the list and view are available, it will know what item is 
					//selected
					currentSelection = value;					
					return;
				}
				
				//disable to avoid infinite loops
				Items.disableRecieveUpdates();
				disableListeners();
				
				final V w = getWidget();				
				currentIndex = currentList.indexOf(value);
				currentSelection = value;
				if (currentIndex >= 0)
				{	
					w.setSelection(currentIndex);
				}
				
				//re-enable
				enableListeners();
				Items.enableRecieveUpdates();
			}
		});
	
	}
	
	/**
	 * This are stubbed out incase a subclass of view binding has extra things to disable
	 */
	protected void disableListeners()
	{
		
	}
	
	/**
	 * analogous to the above method
	 */
	protected void enableListeners()
	{
		
	}
	
	/**
	 * sub classes can override this to control what it means for selection to be enabled
	 * @return : true if the SelectItems ui element is active
	 */
	protected boolean isSelectionEnabled()
	{
		return true;
	}
	
	@Override
	protected void initialise(AttributeSet attrs, Context context, UIHandler uiHandler, BindingInventory inventory)
	{	
		super.initialise(attrs, context, uiHandler, inventory);
		
		//sets the internal adapter
		getWidget().setAdapter(getAdapter());
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AdapterView);
		
		//sets the item template
		itemTemplateId = ta.getResourceId(R.styleable.AdapterView_ItemTemplate, -1);
		
		Items.initialize(ta, inventory, uiHandler);
		if (isSelectionEnabled())
			SelectedItem.initialize(ta, inventory, uiHandler);
		ta.recycle();
	}
	
	
	@Override
	public void detachBindings()
	{
		super.detachBindings();
		getWidget().setAdapter(null);
	}

}
