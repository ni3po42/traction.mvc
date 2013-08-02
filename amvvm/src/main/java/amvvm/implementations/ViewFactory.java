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

package amvvm.implementations;

import java.util.Hashtable;
import java.util.Map;

import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.ui.viewbinding.GenericViewBinding;
import amvvm.interfaces.IViewBinding;
import amvvm.util.Log;
import amvvm.interfaces.IProxyObservableObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory2;
import android.view.View;
import android.view.ViewParent;

import amvvm.R;
/**
 * Custom factory for creating Views during inflation. This determines which IViewBiding object will be associated with
 * each view and add it along with a binding inventory to the view
 * @author Tim Stratton
 *
 */
public class ViewFactory 
implements Factory2
{
	//stores current inflater
	private LayoutInflater inflater;
	
	//lookup table to map view types to view-binding types
	private Map<Class<?>, String> bindingConfig = new Hashtable<Class<?>, String>();
	
	//cache of keys for the bindingConfig map
	private Class<?>[] bindingKeys;

	/**
	 * AMVVM uses the ViewHolder pattern. This is the data that is associated to each view that has bounded data.
	 * @author Tim Stratton
	 *
	 */
	public static class ViewHolder
	{
        private boolean synthetic;

        public ViewHolder(boolean isSynthetic)
        {
            this.synthetic = isSynthetic;
        }

        public ViewHolder()
        {
            this(false);
        }

        public boolean isSynthetic()
        {
            return synthetic;
        }

		/**
		 * Bridge between UI and data
		 */
		public IViewBinding viewBinding;
		
		/**
		 * true if view is considered a root, means it will own an instance of BindingInventory and 
		 * not just use it's parent's instance.
		 */
		public boolean isRoot;
		
		/**
		 * A flag to left the parser know that no child of this ViewGroup will have bound data. For performace. 
		 */
		public boolean ignoreChildren;
		
		/**
		 * keeps inventory of all paths, UIElements that the view binding use to pass data back and forth
		 */
		public BindingInventory inventory;
	}
	
	/**
	 * Constructor to create custom ViewFactory
	 * @param inflater : this is the existing inflater, it will be used to inflate views before we read
	 * 					any custom attribute added
	 */
	public ViewFactory(LayoutInflater inflater)
	{
		this.inflater = inflater;
		try
		{
			//should move this out and allow user the option to register new ones if they so please...
			String packageName = "amvvm.implementations.ui.viewbinding";
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
            addBindingConfig("android.widget.DatePicker", packageName+".DatePickerBinding");
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

    public void clearBindingConfig()
    {
        bindingConfig.clear();
        bindingKeys = null;
    }

	/**
	 * Perform lookup to find which IViewBinding is needed for this view
	 * @param view : view to check against
	 * @param viewBindingTypeAsString : optional custom View type override as string. May be null.
	 * @return : an instantiated IViewBinding object
	 */
	public IViewBinding getViewBinding(View view, String viewBindingTypeAsString)
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
		
		IViewBinding viewBinding = null;
		try
		{
			//try and get instance...
			viewBinding = (IViewBinding)theClass.newInstance();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage(),e);
		}
		return viewBinding;		
	}

    public AttributeBridge createAttributeBridge(Context context, AttributeSet attrs)
    {
        return new AttributeBridge(context, attrs);
    }

    public View inflateViewByClassName(String className, AttributeSet attrs)
    {
        if (inflater == null)
            return null;

        try
        {
            return inflater.createView(className,null, attrs);
        }
        catch (Exception e)
        {
            //fail silently. Sometimes the inflater will fail. This happens
            //somewhere in the ViewConfiguration.get method when a Display metric
            //is being retrieved, but null pointer exception occurs. I suspected it
            //has to do with android remeasuring the layout and failing. I don't have a better
            //fix for now except to ignore it; it apparently tries again successfully.
        }
        return null;
    }

	@SuppressLint("DefaultLocale")
	@Override
	public View onCreateView(View parent, String name, Context context,	AttributeSet attrs) 
	{
        //no name, no view
		if (name == null)
			return null;

        //figure out full name of view to inflate
        String viewFullName = "android.widget." + name;
        if (name.equals("View") || name.equals("ViewGroup"))
         viewFullName = "android.view." + name;
        else if (name.toLowerCase().equals("fragment"))
        {
            //ignore if it's a fragment, it's handled higher in the parser, and no way will I try to mimic or override that
            return null;
        }
        else if (name.toLowerCase().equals("fragmentstub"))
        {
            viewFullName = android.widget.FrameLayout.class.getName();
        }
        else if (name.contains("."))
            viewFullName = name;

        //inflate
        View view = inflateViewByClassName(viewFullName, attrs);

		//no view, um, well, no view.
         if (view==null) return null;

        //should we go on?
        ViewHolder parentViewHolder = parent == null ? null : getViewHolder(parent);

        //if either there is no viewholder for a non null parent or that parent's viewholder says to ignore chilrend..
        if ((parent != null && parentViewHolder == null) || (parentViewHolder != null && parentViewHolder.ignoreChildren))
            //then stop here and return the view, no binding steps needed now.
            return view;

        AttributeBridge attributeBridge = createAttributeBridge(context, attrs);
         ViewHolder viewHolder = new ViewHolder();
         UIHandler handler = new UIHandler();
         
       //pull base attributes
 		TypedArray ta = attributeBridge.getAttributes(R.styleable.View);
 		viewHolder.ignoreChildren = ta.getBoolean(R.styleable.View_IgnoreChildren, false);	
 		viewHolder.isRoot = ta.getBoolean(R.styleable.View_IsRoot, false);
        boolean isBindable = ta.getBoolean(R.styleable.View_IsBindable, false);

 		//grab custom binding type, if available
         String bindingType = ta.getString(R.styleable.View_BindingType);
         
 		ta.recycle();
         
         view.setTag(R.id.amvvm_viewholder, viewHolder);
         
         BindingInventory parentInv = (parentViewHolder != null) ? parentViewHolder.inventory : null;

         if (viewHolder.isRoot)
        	 viewHolder.inventory = new BindingInventory(parentInv);
         else
        	 viewHolder.inventory = parentInv;
         
         if (!isBindable)
        	 return view;
         
                  
         //if view implements IViewBinding and no custom type is given...
         if (view instanceof IViewBinding && bindingType == null)
        	 //use the view itself...
        	 viewHolder.viewBinding = (IViewBinding)view;
         else
        	 //...otherwise lookup the binding needed.
        	 viewHolder.viewBinding = getViewBinding(view,bindingType);   
         
         //if at this point we actually have a view binding...
         if (viewHolder.viewBinding != null)
         {
        	 viewHolder.viewBinding.initialise(view, attributeBridge, handler, viewHolder.inventory);
         }
         
         return view;
	}

	/**
	 * Not used, part of the ViewFactory interface, I need to use ViewFactory2
	 */
	@Override
	public View onCreateView(String arg0, Context arg1, AttributeSet arg2) 
	{		
		return null;
	}

	/**
	 * Gets ViewHolder from this view
	 */
	public static ViewHolder getViewHolder(final View view)
	{
		if (view == null)
			return null;
		return (ViewHolder)view.getTag(R.id.amvvm_viewholder);
	}

    /**
     * Injects a 'synthetic' viewholder into the view
     * @param view
     * @param inventory
     * @param viewBinding
     * @param uiHandler
     * @return : the newly injected viewHolder
     */
    public static ViewHolder createViewHolderFor(final View view, BindingInventory inventory, IViewBinding viewBinding, UIHandler uiHandler)
    {
        //create 'synthetic' viewholder
        ViewHolder vh = new ViewHolder(true);

        vh.inventory =inventory;
        vh.viewBinding = viewBinding;

        view.setTag(R.id.amvvm_viewholder, vh);
        return vh;
    }

	/**
	 * Registers (binds) View to an object. The framework support late binding, when this
	 * observable is updated, it will bubble the event to all listening objects.
	 * So, it is not necessary to have all object populated, 
	 * they will be caught with the objects update.
	 * @param view : view to Register (bind) to
	 * @param context : root object to bind against
	 */
	public static void RegisterContext(final View view,final IProxyObservableObject context)
	{
		if (context == null || view == null || context.getProxyObservableObject() == null)
			return;
		
		ViewHolder vh = getViewHolder(view);
		if (vh == null)
			return;
		
		BindingInventory inventory = vh.inventory;			
		if (inventory != null)
		{
			inventory.setContextObject(context);			
			context.getProxyObservableObject().notifyListener();
		}		
	}
				
	/**
	 * Detach (unbind) view from current root context; this in turn releases all binding links
	 * @param view : view to detach from
	 */
	public static void DetachContext(final View view)
	{
		if (view == null)
			return;
		
		ViewHolder vh = getViewHolder(view);
		
		if (vh == null)
			return;
		
		if (vh.inventory != null)
		{
			vh.inventory.setContextObject(null);
			vh.inventory = null;
		}
		
		//if found, then detachBindings...
		if (vh.viewBinding != null)
		{
			vh.viewBinding.detachBindings();
			vh.viewBinding = null;
		}
		view.setTag(R.id.amvvm_viewholder, null);
	}
	
}
