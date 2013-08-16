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

package amvvm.implementations.ui.menubinding;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import amvvm.implementations.BindingInventory;
import amvvm.implementations.InflatedAttributes;
import amvvm.interfaces.IAttributeBridge;
import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IObservableObject;
import amvvm.interfaces.IProxyObservableObject;

import org.xmlpull.v1.XmlPullParser;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;
import amvvm.R;

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
	private BindingInventory inventory = new BindingInventory();
	private IProxyObservableObject menuContext;
	
	public <S extends IObservableObject> MenuInflater(Context context, IProxyObservableObject menuContext)
	{
		super(context);
		
		//hold a reference to current context, we'll need it
		this.weakContextRef = new WeakReference<Context>(context);
		//the menu's context could change at any time, and we need a way after the inflater is created
		//to still bind to it, so we pass the context as a property to inject new contexts later
		this.menuContext = menuContext;		
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
			menuBindings.get(i).detachBindings();
		menuBindings.clear();

		XmlResourceParser parser = null;
		
        try {
        	//get parser and attributes for the menu layout
            parser = context.getResources().getLayout(menuRes);
            AttributeSet attrs = Xml.asAttributeSet(parser);

            IAttributeBridge bridge = new InflatedAttributes(context, attrs);

            int eventType = parser.getEventType();
            
            //while the xml document has not ended...
            while (eventType != XmlPullParser.END_DOCUMENT) 
            {
            	if (eventType == XmlPullParser.START_TAG && parser.getName().equals("item"))
            	{
            		//.. then get the menu's id
            		String idStr = attrs.getAttributeValue(androidRESNamespace, "id");
                    idStr = idStr == null ? "0" : idStr;
                    int id = Integer.parseInt(idStr.replace("@",""));
                    if (id != 0)
                    {
                        //get menu item, create bindings from it
                        IAttributeGroup ag = bridge.getAttributes(R.styleable.Menu);
                        MenuItem m = menu.findItem(id);
                        MenuBinding mb = new MenuBinding(m, ag ,inventory);
                        menuBindings.add(mb);
                        ag.recycle();
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
        inventory.setContextObject(menuContext);
	}
}