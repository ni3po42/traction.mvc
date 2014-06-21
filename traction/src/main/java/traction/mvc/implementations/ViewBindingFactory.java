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

package traction.mvc.implementations;


import android.view.View;

import java.util.Hashtable;
import java.util.Map;

import traction.mvc.R;
import traction.mvc.interfaces.IProxyViewBinding;
import traction.mvc.interfaces.IViewBinding;
import traction.mvc.util.Log;

public class ViewBindingFactory
{
    //lookup table to map view types to view-binding types
    private Map<Class<?>, String> bindingConfig = new Hashtable<Class<?>, String>();


    //cache of keys for the bindingConfig map
    private Class<?>[] bindingKeys;

    public ViewBindingFactory()
    {
        try
        {
            //should move this out and allow user the option to register new ones if they so please...
            String packageName = "traction.mvc.implementations.ui.viewbinding";
            addBindingConfig("android.view.View", packageName+".GenericViewBinding");
            addBindingConfig("android.widget.AbsListView", packageName+".ListViewBinding");
            addBindingConfig("android.widget.Spinner", packageName+".SpinnerViewBinding");
            addBindingConfig("android.widget.TextView", packageName+".TextViewBinding");
            addBindingConfig("android.widget.NumberPicker", packageName+".NumberPickerBinding");
            addBindingConfig("android.widget.TimePicker", packageName+".TimePickerBinding");
            addBindingConfig("android.widget.ProgressBar", packageName+".ProgressBarBinding");
            addBindingConfig("android.widget.SeekBar", packageName+".SeekBarBinding");
            addBindingConfig("android.widget.ImageView", packageName+".ImageViewBinding");
            addBindingConfig("android.widget.ImageButton", packageName+".ImageButtonBinding");
            addBindingConfig("android.widget.CompoundButton", packageName+".CompoundButtonBinding");
            addBindingConfig("android.widget.Button", packageName+".ButtonBinding");
            addBindingConfig("android.widget.CalendarView", packageName+".CalendarViewBinding");
            addBindingConfig("android.widget.DatePicker", packageName + ".DatePickerBinding");
        }
        catch(ClassNotFoundException ex)
        {
            Log.e("error creating view binding config", ex);
        }
    }

    public void addBindingConfig(String viewClass, String viewBindingClass)
    throws ClassNotFoundException
    {
        bindingConfig.put(Class.forName(viewClass), viewBindingClass);
        bindingKeys = null;
    }

    /**
     * Injects a 'synthetic' viewholder into the view
     * @param view
     * @return : the newly injected viewHolder
     */
    public IViewBinding createSyntheticFor(final View view, String viewBindingType, BindingInventory inventory)
    {
        IProxyViewBinding viewBinding = createViewBinding(view, viewBindingType);
        if (viewBinding != null && viewBinding.getProxyViewBinding() != null)
            viewBinding.getProxyViewBinding().markAsSynthetic(inventory);

        view.setTag(R.id.viewholder, viewBinding);
        return (viewBinding == null || viewBinding.getProxyViewBinding() == null) ? null : viewBinding.getProxyViewBinding();
    }

    /**
     * Perform lookup to find which IViewBinding is needed for this view
     * @param view : view to check against
     * @param viewBindingTypeAsString : optional custom View type override as string. May be null.
     * @return : an instantiated IViewBinding object
     */
    public IProxyViewBinding createViewBinding(View view, String viewBindingTypeAsString)
    {
        //if no override is given...
        if (viewBindingTypeAsString == null)
        {
            Class<?> currentClass = null;
            //iterate through all keys...

            if (bindingKeys == null)
                bindingKeys = bindingConfig.keySet().toArray(new Class<?>[bindingConfig.size()]);

            for(int i=0;i<bindingKeys.length;i++)
            {
                //..if the view is not an instance of the key, just skip it...
                if (!bindingKeys[i].isInstance(view))
                    continue;

                //.. if it is, either set it or determine if it is a more derived then the currently
                //selected class
                if (currentClass == null || currentClass.isAssignableFrom(bindingKeys[i]))
                    currentClass = bindingKeys[i];
            }
            //get the class name if one was found.
            if (currentClass != null)
                viewBindingTypeAsString = bindingConfig.get(currentClass);
        }

        Class<?> theClass = null;
        try
        {
            //try and get the class
            if (viewBindingTypeAsString != null)
            {
                //theClass = IViewBinding.class.getClassLoader().loadClass(viewBindingTypeAsString);
                theClass = Class.forName(viewBindingTypeAsString);
            }
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }

        IProxyViewBinding viewBinding = null;
        try
        {
            //try and get instance...
            viewBinding = (IProxyViewBinding)theClass.newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(),e);
        }
        return viewBinding;
    }

}
