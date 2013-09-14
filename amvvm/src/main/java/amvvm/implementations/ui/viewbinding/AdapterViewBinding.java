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
import amvvm.interfaces.IAttributeBridge;
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IProxyObservableObject;
import amvvm.interfaces.IUIElement.IUIUpdateListener;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.BaseAdapter;


import amvvm.R;

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
 */
public class AdapterViewBinding<T>
extends GenericViewBinding<AdapterView<BaseAdapter>>
{	
	public final UIProperty<ProxyAdapter<T>> Items = new UIProperty<ProxyAdapter<T>>(this, R.styleable.AdapterView_Items);
	//public final UIProperty<T> SelectedItem = new UIProperty<T>(this, R.styleable.AdapterView_SelectedItem);

	//layout to use for child views
	private int itemTemplateId = -1;
	private BaseAdapter internalAdapter;

	//local reference to the selected item
	private T currentSelection;
	private int currentIndex=-1;

    private ProxyAdapter<T> adapterCache;

	public AdapterViewBinding()
	{
		Items.setUIUpdateListener(new IUIUpdateListener<ProxyAdapter<T>>()
		{
			@Override
			public void onUpdate(ProxyAdapter<T> value)
			{

                if (getWidget() == null)
                    return;

                BaseAdapter adapter = null;
                if (value != null)
                {
                    ProxyAdapter.ProxyAdapterArgument arg =
                        new ProxyAdapter.ProxyAdapterArgument()
                        .setContext(getWidget().getContext())
                        .setLayoutId(itemTemplateId)
                        .setInventory(getBindingInventory());
                    adapter = value.getProxyAdapter(arg);
                }


                boolean callSetChanged = getWidget().getAdapter() != adapter;

                if (!callSetChanged)
                    return;

                //if (adapterCache != null)
                //   adapterCache.clearProxyAdapter();
                adapterCache = value;

                if (adapter instanceof ProxyAdapter.IAdapterLayout)
                    ((ProxyAdapter.IAdapterLayout)adapter).setLayoutId(itemTemplateId);

                getWidget().setAdapter(adapter);
                onAdapterChanged();

			}
			
		});
	}

    protected void onAdapterChanged()
    {

    }

	@Override
	protected void initialise(IAttributeBridge attributeBridge)
	{	
		super.initialise(attributeBridge);

        IAttributeGroup ta = attributeBridge.getAttributes(R.styleable.AdapterView);
		
		//sets the item template
		itemTemplateId = ta.getResourceId(R.styleable.AdapterView_ItemTemplate, -1);

        Items.initialize(ta);
		ta.recycle();
	}
	
	
	@Override
	public void detachBindings()
	{
		super.detachBindings();
        if (getWidget() != null)
		    getWidget().setAdapter(null);
	}

}
