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

import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IUIElement.IUIUpdateListener;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.BaseAdapter;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

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

	public final UIProperty<ProxyAdapter<T>> Items = new UIProperty<ProxyAdapter<T>>(this, "Items");

	//layout to use for child views
	private int itemTemplateId = -1;
	private BaseAdapter internalAdapter;

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

                Items.setTempValue(value);

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
	protected void initialise() throws Exception
	{	
		super.initialise();
        if (getWidget() == null)
            return;

        Context context = getWidget().getContext();

        String resourceName = getTagProperties().getString("ItemTemplate");
        if (resourceName == null) return;

        String[] parts = resourceName.split(":");
        String packageName = parts.length == 1 ? context.getPackageName() : parts[0];
        String resource = parts.length == 1 ? resourceName : parts[1];
        parts = resource.split("\\/");
        String defType = parts.length == 1 ? "layout" : parts[0];
        String entityName = parts.length == 1 ? resourceName : parts[1];
        defType = defType.replace("@", "");
        itemTemplateId = context.getResources().getIdentifier(entityName, defType, packageName);
	}
	
	
	@Override
	public void detachBindings()
	{
		super.detachBindings();
        if (getWidget() != null)
		    getWidget().setAdapter(null);
	}

}
