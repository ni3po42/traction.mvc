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

package amvvm.interfaces;

import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.BindingInventory;

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
     * @param attributeBridge : access to inflated attributes
	 * @param uiHandler : a handler for accessing the ui thread
	 * @param inventory : inventory to register ui elements and paths
	 */
	void initialise(View v,IAttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory, int bindingFlags);

    public static class Flags
    {
        private Flags(){}

        public static int NO_FLAGS = 0;
        public static int IS_ROOT = 1;
        public static int IGNORE_CHILDREN = 2;
        public static int HAS_RELATIVE_CONTEXT = 4;

        public static boolean hasFlags(int flags, int flagsToTest)
        {
            return (flags & flagsToTest) == flagsToTest;
        }
    }

	/**
	 * Here, you will want to unhook stuff, general clean up
	 */
	void detachBindings();

    /**
     * returns the inventory passed in from the initialise method
     * @return
     */
    BindingInventory getBindingInventory();

    /**
     * Gets a handler to run tasks on the UI thread; whatever was passed to initialise method
     * @return
     */
    UIHandler getUIHandler();

    /**
     * Gets flags
     * @return
     */
    int getBindingFlags();

    /**
     *
     * @return
     */
    boolean isSynthetic();

    /**
     * marks view binding as synthetic. makes isSynthetic return true; ounce set, cannot be undone.
     */
    void markAsSynthetic(BindingInventory inventory);
}
