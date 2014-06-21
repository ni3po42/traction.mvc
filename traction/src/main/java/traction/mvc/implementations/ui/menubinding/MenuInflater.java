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

package traction.mvc.implementations.ui.menubinding;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParser;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;

import traction.mvc.implementations.BindingInventory;
import traction.mvc.implementations.ViewFactory;
import traction.mvc.interfaces.IViewBinding;

/**Custom menu-inflater to bind it with view-model. This is more complex then the ViewFactory implementation because there is no
 * inherent hook when a the menu is inflating. 
 * @author Tim Stratton
 *
 */
public class MenuInflater extends android.view.MenuInflater
{
	private static final String androidRESNamespace = "http://schemas.android.com/apk/res/android";

	private final WeakReference<Context> weakContextRef;
	private final LinkedList<MenuBinding> menuBindings = new LinkedList<MenuBinding>();
	private final BindingInventory inventory;
	
	public MenuInflater(Context context, BindingInventory inventory)
	{
		super(context);
		
		//hold a reference to current context, we'll need it
		this.weakContextRef = new WeakReference<Context>(context);
		//the menu's context could change at any time, and we need a way after the inflater is created
		//to still bind to it, so we pass the context as a property to inject new contexts later
		this.inventory = inventory;
	}
		
	@Override
	public void inflate(int menuRes, Menu menu) 
	{		
		//perform normal inflate like usual...		
		super.inflate(menuRes, menu);
		
		if (weakContextRef.get() == null)
			return;
		
		Context context = weakContextRef.get();
		
		//prepare bindings
		for(int i=0;i<menuBindings.size();i++)
			menuBindings.get(i).getProxyViewBinding().detachBindings();
		menuBindings.clear();

		XmlResourceParser parser = null;

        SparseArray<String> tagTracker = new SparseArray<String>();
        try
        {
        	//get parser and attributes for the menu layout
            parser = context.getResources().getLayout(menuRes);
            AttributeSet attrs = Xml.asAttributeSet(parser);

            int eventType = parser.getEventType();
            
            //while the xml document has not ended...
            while (eventType != XmlPullParser.END_DOCUMENT) 
            {
            	if (eventType == XmlPullParser.START_TAG && parser.getName().equals("item"))
            	{
            		String idStr = attrs.getAttributeValue(androidRESNamespace, "id");
                    String tag = attrs.getAttributeValue(androidRESNamespace, "tag");
                    int id = idStr == null ? 0 : Integer.parseInt(idStr.replace("@",""));
                    if (id != 0 && tag != null)
                    {
                        tagTracker.put(id, tag);
                    }
                }	                
                eventType = parser.next();
            }
        } 
        catch (Exception e) 
        {
            throw new InflateException("Error inflating menu XML", e);
        } 
        finally 
        {
        	//close parser
            if (parser != null) parser.close();
        }

        for(int i=0;i<menu.size();i++)
        {
            //get menu item, create bindings from it
            MenuItem m = menu.getItem(i);
            //update action view to use menu inventory
            if (m.getActionView() != null)
            {
                IViewBinding actionViewBinding = ViewFactory.getViewBinding(m.getActionView());
                if (actionViewBinding != null)
                {
                    actionViewBinding.updateBindingInventory(inventory);
                }
            }

            String tag = tagTracker.get(m.getItemId(), null);
            if (tag != null)
            {
                MenuBinding mb = new MenuBinding(m,tag);
                mb.getProxyViewBinding().initialise(null, null, inventory, IViewBinding.Flags.NO_FLAGS);
                menuBindings.add(mb);
            }
        }
	}


}