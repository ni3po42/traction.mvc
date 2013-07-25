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

package ni3po42.android.amvvm.implementations.ui.viewbinding;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.regex.Pattern;

import ni3po42.android.amvvm.implementations.BindingInventory;
import ni3po42.android.amvvm.implementations.observables.GenericArgument;
import ni3po42.android.amvvm.implementations.observables.PropertyStore;
import ni3po42.android.amvvm.implementations.ui.UIBindedEvent;
import ni3po42.android.amvvm.implementations.ui.UIBindedProperty;
import ni3po42.android.amvvm.implementations.ui.UIHandler;
import ni3po42.android.amvvm.interfaces.IUIElement.IUIUpdateListener;
import ni3po42.android.amvvm.interfaces.IViewBinding;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import ni3po42.android.amvvm.R;

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
	
	public final UIBindedProperty<Boolean> IsVisible = new UIBindedProperty<Boolean>(this, R.styleable.View_IsVisible);
	
	private WeakReference<V> widget;
	
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
		IsVisible.setUIUpdateListener(new IUIUpdateListener<Boolean>()
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
	extends UIBindedProperty<Object>
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
			setUIUpdateListener(new IUIUpdateListener<Object>()
			{			
				@Override
				public void onUpdate(Object value)
				{
					if (viewProperty == null || viewProperty.isReadOnly() || getWidget() == null)
						return;
						
					viewProperty.set(getWidget(), value);
				}
			});
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void initialize(TypedArray notUsed, BindingInventory inventory, UIHandler uiHandler)
		{
			this.uiHandler = uiHandler;
			this.inventory = inventory;
			
			String[] pairs = split.split(connection);
			
			//let the property store find the property for me...
			viewProperty = (Property<Object, Object>) PropertyStore.find(getWidget().getClass(), pairs[0].trim());
			
			this.path = pairs[1].trim();
			inventory.track(this);
		}
		
	}
		
	/**
	 * defines a generic event. These handle binding commands to different types of listeners on a view. This fills the gap
	 * missing from the generic properties by reacting to listener events.
	 * @author Tim Stratton
	 *
	 */
	class GenericUIBindedEvent
	extends UIBindedEvent<Object>
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
		public void initialize(TypedArray notUsed, BindingInventory inventory, UIHandler uiHandler)
		{
			this.uiHandler = uiHandler;
			this.inventory = inventory;
			
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
						GenericArgument arg = new GenericArgument(method.getName(), args);
						
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
			
			inventory.track(this);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialise(View v, AttributeSet attrs, Context context, UIHandler uiHandler, BindingInventory inventory)
	{		
		widget = new WeakReference<V>((V)v);
		initialise(attrs, context, uiHandler, inventory);
	}
	
	protected void initialise(AttributeSet attrs, Context context, UIHandler uiHandler, BindingInventory inventory)
	{
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.View);
		IsVisible.initialize(ta, inventory, uiHandler);		
		
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
				prop.initialize(null, inventory, uiHandler);
			}
			else//properties
			{	
				GenericUIBindedEvent evnt = new GenericUIBindedEvent(this, bindingList[i].trim());
				evnt.initialize(null, inventory, uiHandler);
			}			
		}
	}

	//not implemented yet
	@Override
	public String getBasePath()
	{
		return null;
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
