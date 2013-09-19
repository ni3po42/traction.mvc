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

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.regex.Pattern;

import amvvm.implementations.ui.UIEvent;
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IAttributeBridge;
import amvvm.implementations.observables.PropertyStore;
import amvvm.implementations.ui.UIHandler;
import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IUIElement;
import amvvm.interfaces.IViewBinding;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.observables.GenericArgument;

import android.util.Property;
import android.view.View;
import amvvm.R;

/**
 * The base implementation for all default view bindings in AMVVM. This handles all base ui elements for all views bounded
 * @author Tim Stratton
 * 
 * Exposes the following properties:
 * IsVisible : updates if view is gone, visible or invisible
 * Generic Properties and Events
 *
 * @param <V> : type of view for the binding
 */
public class GenericViewBinding<V extends View>
implements IViewBinding
{	
	protected ArrayList<GenericUIBindedEvent> genericBindedEvents = new ArrayList<GenericUIBindedEvent>();
	
	public final UIProperty<Boolean> IsVisible = new UIProperty<Boolean>(this, R.styleable.View_IsVisible);

	private WeakReference<V> widget;

    private final ViewBindingHelper helper = new ViewBindingHelper();

    @Override
    public BindingInventory getBindingInventory()
    {
        return helper.getBindingInventory();
    }

    @Override
    public UIHandler getUIHandler()
    {
        return helper.getUIHandler();
    }

    @Override
    public int getBindingFlags() {
        return helper.getBindingFlags();
    }

    @Override
    public boolean isSynthetic()
    {
        return helper.isSynthetic();
    }

    @Override
    public void markAsSynthetic(BindingInventory inventory)
    {
       helper.markAsSynthetic(inventory);
    }

    @Override
    public String getPathPrefix() {
        return helper.getPrefix();
    }

    @Override
    public void setPathPrefix(String prefix) {
        helper.setPrefix(prefix);
    }

    /**
	 * Gets the view (widget) this view binding is associated with.
	 * @return view (widget) if available. null otherwise.
	 */
	protected V getWidget()
	{
		if (widget == null || widget.get() == null)
			return null;
		return widget.get();
	}

	public GenericViewBinding()
	{
		//IsVisible expects a Boolean value (not boolean). 
		IsVisible.setUIUpdateListener(new IUIElement.IUIUpdateListener<Boolean>()
		{
			@Override
			public void onUpdate(Boolean value)
			{
				if (getWidget()== null)
					return;
				if (value == null)//null will be interpreted as GONE
					getWidget().setVisibility(View.GONE);
				else//true/false is visible or invisible
					getWidget().setVisibility(value ? View.VISIBLE : View.INVISIBLE);
			}
		});
	}
	
	/**
	 * defines a generic property. These handle general properties on a view that are not explicitly defined.
	 * The work very similar to the regular ui element, however with the disadvantage of not being able to signal the
	 * model/view-model directly
	 * @author Tim Stratton
	 *
	 */
	public class GenericUIBindedProperty 
	extends UIProperty<Object>
	{
		private final Pattern split = Pattern.compile("=");		
		private String connection;
		
		//the property on the view to set and get values from
		private Property<Object, Object> viewProperty;
		
		/**
		 * Not really used, only defined because it's base requires it
		 * @param viewBinding
		 * @param pathAttribute
		 */
		public GenericUIBindedProperty(IViewBinding viewBinding, int pathAttribute)
		{
			super(viewBinding, pathAttribute);
		}
		
		/** 
		 * @param viewBinding 
		 * @param connection : a single connection point following this pattern:
		 * xxx = aaa.bbb.ccc, where xxx setter method (does not include the prefix and aaa.bbb.ccc is a path to the model/view-model
		 */
		public GenericUIBindedProperty(IViewBinding viewBinding, String connection)
		{
			super(viewBinding, 0);
			this.connection = connection;
			setUIUpdateListener(new IUIElement.IUIUpdateListener<Object>()
			{			
				@Override
				public void onUpdate(Object value)
				{
					if (viewProperty == null || viewProperty.isReadOnly() || getWidget() == null)
						return;
                    if (viewProperty.getType().isPrimitive() && value == null)
                        return;

					viewProperty.set(getWidget(), value);
				}
			});
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void initialize(IAttributeGroup notUsed)
		{
			String[] pairs = split.split(connection);
			
			//let the property store find the property for me...
			viewProperty = (Property<Object, Object>) PropertyStore.find(getWidget().getClass(), pairs[0].trim());
			
			this.path = pairs[1].trim();
            getBindingInventory().track(this);
		}
		
	}
		
	/**
	 * defines a generic event. These handle binding commands to different types of listeners on a view. This fills the gap
	 * missing from the generic properties by reacting to listener events.
	 * @author Tim Stratton
	 *
	 */
	class GenericUIBindedEvent
	extends UIEvent<GenericArgument>
	{
		private final Pattern split = Pattern.compile("\\+=");
		private String connection;
		private static final int prefixWidth = 3;
		private Method setMethod;
		
		public GenericUIBindedEvent(IViewBinding viewBinding, int pathAttribute)
		{
			super(viewBinding, pathAttribute);
		}
		
		/**
		 * 
		 * @param viewBinding
		 * @param connection : a single connection point following this pattern:
		 * xxx += aaa.bbb.ccc, where xxx setter method (does not include the prefix and aaa.bbb.ccc is a path to the model/view-model
		 */
		public GenericUIBindedEvent(IViewBinding viewBinding, String connection)
		{
			super(viewBinding, 0);
			this.connection = connection;
		}
		
		@Override
		public void initialize(IAttributeGroup notUsed)
		{
			String[] pairs = split.split(connection);
			String setName = pairs[0].trim();
			
			this.path = pairs[1].trim();
			
			
			Class<?> widgetClass = getWidget().getClass();
			Method[] methods = widgetClass.getMethods();
			
			//try to find the set method for the listener. It assumes the prefix is 3 characters, like 'set' or 'add'
			try
			{
				for(int i = 0; i< methods.length;i++)
				{
					if (methods[i].getName().indexOf(setName) != prefixWidth)
						continue;
					setMethod = methods[i];
					break;
				}
				if (setMethod == null || setMethod.getParameterTypes().length != 1)
					throw new NoSuchMethodException();
			}
			catch(NoSuchMethodException ex)
			{
				throw new RuntimeException("No set/add method for listner");
			}
			
			//get the first and only parameter of the method, get the type
			Class<?> listenerType = setMethod.getParameterTypes()[0];
			
			try
			{
				//try to proxy the interface, if it the interface, using a invocation handler
				Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{listenerType}, new InvocationHandler()
				{
					@Override
					public Object invoke(Object proxy, Method method, Object[] args)
							throws Throwable
					{
						//create generic argument with name of method call and arguments
						GenericArgument arg = new GenericArgument(GenericUIBindedEvent.this.getPropertyName(),method.getName(), args);
						
						GenericUIBindedEvent.this.execute(arg);
						
						if (method.getReturnType().equals(Void.class))
							return null;
						
						return arg.getReturnObj();
					}				
				});
				
				//add proxy listener to the method				
				setMethod.invoke(getWidget(), proxy);
			}
			catch (Exception e)
			{
				//Log.
			}
			getBindingInventory().track(this);
		}
		
		/**
		 * try to clear the proxy listener
		 */
		public void clearProxyListner()
		{
			if (setMethod == null || getWidget() == null)
				return;
			try
			{
				setMethod.invoke(getWidget(), (Object[])null);
			}
			catch (Exception e)
			{
			}
		}
		
	}

	protected void initialise(IAttributeBridge attributeBridge)
	{
        IAttributeGroup ta = attributeBridge.getAttributes(R.styleable.View);
        if (ta == null)
            return;

		IsVisible.initialize(ta);

		//get semi-colon delimited properties
		String bindings = ta.getString(R.styleable.View_GenericBindings);		
		ta.recycle();
		
		if (bindings == null)
			return;
		
		String[] bindingList = bindings.split(";");
		//for each connection
		for(int i=0;i<bindingList.length;i++)
		{			
			if (!bindingList[i].contains("+="))//events
			{
				GenericUIBindedProperty prop = new GenericUIBindedProperty(this, bindingList[i].trim());
				prop.initialize(null);
			}
			else//properties
			{	
				GenericUIBindedEvent evnt = new GenericUIBindedEvent(this, bindingList[i].trim());
				evnt.initialize(null);
			}			
		}
	}

    @Override
    public void initialise(View v, IAttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory, int flags)
    {
        widget = new WeakReference<V>((V)v);
        if (attributeBridge == null)
            return;
        helper.setBindingFlags(flags);
        helper.setBindingInventory(inventory);
        helper.setUiHandler(uiHandler);
        initialise(attributeBridge);
    }

	/**
	 * does general cleanup
	 */
	@Override
	public void detachBindings()
	{
		for(int i=0;i<genericBindedEvents.size();i++)
		{
			genericBindedEvents.get(i).clearProxyListner();
		}
	}

}
