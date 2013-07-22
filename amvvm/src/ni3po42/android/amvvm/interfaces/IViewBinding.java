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

package ni3po42.android.amvvm.interfaces;

import ni3po42.android.amvvm.implementations.BindingInventory;
import ni3po42.android.amvvm.implementations.ui.UIHandler;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


/**
 * Defines a View Binding object. This is an object that lives beside a View object, extending it in a way to 
 * expose bindable properties and events. New and custom views may implement this as to avoid maintaining to separate
 * files; the framework can be configured to handle existing views
 *  
 * @author Tim Stratton
 *
 */
public interface IViewBinding
{		
	/**
	 * Initialise the ViewBinding. It is here you will read properties defined in the layout files
	 * @param v : the view to init against
	 * @param attrs : all attributes defined on the view. Refer to declared styleable fields for what attributes
	 * 					can be pulled out.
	 * @param context : current context performing the initialisation (it's going to be the activity).
	 * @param uiHandler : a handler for accessing the ui thread
	 * @param inventory : inventory to register ui elements and paths
	 */
	void initialise(View v,AttributeSet attrs, Context context, UIHandler uiHandler, BindingInventory inventory);
		
	/**
	 * Not really implemented at this time
	 * @return
	 */
	String getBasePath();
		
	/**
	 * Here, you will want to unhook stuff, general clean up
	 */
	void detachBindings();
	
}
