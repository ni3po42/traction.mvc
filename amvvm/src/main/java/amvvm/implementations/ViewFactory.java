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
import amvvm.interfaces.IViewBinding;
import amvvm.util.Log;
import amvvm.interfaces.IProxyObservableObject;
import amvvm.interfaces.IUIElement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory2;
import android.view.View;
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
	private static final String androidRESNamespace = "http://schemas.android.com/apk/res/android";
	
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
			bindingConfig.put(Class.forName("android.view.View"), packageName+".GenericViewBinding");
			bindingConfig.put(Class.forName("android.widget.AbsListView"), packageName+".ListViewBinding");
			bindingConfig.put(Class.forName("android.widget.Spinner"), packageName+".SpinnerViewBinding");
			bindingConfig.put(Class.forName("android.widget.TextView"), packageName+".TextViewBinding");
			bindingConfig.put(Class.forName("android.widget.NumberPicker"), packageName+".NumberPickerBinding");
			bindingConfig.put(Class.forName("android.widget.TimePicker"), packageName+".TimePickerBinding");
			bindingConfig.put(Class.forName("android.widget.ProgressBar"), packageName+".ProgressBarBinding");
			bindingConfig.put(Class.forName("android.widget.SeekBar"), packageName+".SeekBarBinding");
			bindingConfig.put(Class.forName("android.widget.ImageView"), packageName+".ImageViewBinding");
			bindingConfig.put(Class.forName("android.widget.ImageButton"), packageName+".ImageButtonBinding");
			bindingConfig.put(Class.forName("android.widget.CompoundButton"), packageName+".CompoundButtonBinding");
			bindingConfig.put(Class.forName("android.widget.Button"), packageName+".ButtonBinding");
			bindingConfig.put(Class.forName("android.widget.CalendarView"), packageName+".CalendarViewBinding");
			bindingConfig.put(Class.forName("android.widget.DatePicker"), packageName+".DatePickerBinding");
			
			bindingKeys = bindingConfig.keySet().toArray(new Class<?>[bindingConfig.size()]);
		}
		catch(Exception ex)
		{
			Log.e("error creating view binding config", ex);
		}
	}
		
	/**
	 * Perform lookup to find which IViewBinding is needed for this view
	 * @param view : view to check against
	 * @param viewBindingTypeAsString : optional custom View type override as string. May be null.
	 * @return : an instantiated IViewBinding object
	 */
	private IViewBinding getViewBinding(View view, String viewBindingTypeAsString)
	{	
		//if no override is given...
		if (viewBindingTypeAsString == null)
		{
			Class<?> currentClass = null;
			//iterate through all keys...
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

    private static class fragmentFailedView extends View
    {
        public fragmentFailedView(Context context) {
            super(context);
        }
    }

    private final String tagPrefix = "amvvm.";

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
        catch (ClassNotFoundException e)
        {
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
         
         BindingInventory parentInv = null;
         ViewHolder parentViewHolder = null;
    	 if (parent != null)
    	 {
    		 parentViewHolder = getViewHolder(parent);
    		 if (parentViewHolder != null)
    			 parentInv = parentViewHolder.inventory;
    	 }

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

	//not used at this time..
	class metaProperty
	implements IUIElement<Object>
	{
		private final Object value;
		private String path;
		private BindingInventory inventory;
				
		public metaProperty(BindingInventory inventory, String path, Object value)
		{	
			this.path = path;
			this.value = value;
			this.inventory = inventory;
		}
		@Override
		public String getPath()
		{
			return this.path;
		}
		@Override
		public void setUIUpdateListener(IUIUpdateListener<Object> listener)
		{
			//not used
		}
		@Override
		public void recieveUpdate(Object notUsed)
		{
			getBindingInventory().sendUpdateFromUIElement(this, value);
		}
		@Override
		public void sendUpdate(Object value)
		{
			//not used
		}
		@Override
		public void initialize(TypedArray x, BindingInventory y, UIHandler z)
		{
			//not used
		}
		@Override
		public void disableRecieveUpdates()
		{
			//not used
		}
		@Override
		public void enableRecieveUpdates()
		{
			//not used
		}
		@Override
		public BindingInventory getBindingInventory()
		{
			return inventory;
		}
		
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
