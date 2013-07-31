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

package amvvm.implementations.observables;

import java.util.ArrayList;
import java.util.HashMap;
import amvvm.interfaces.IObjectListener;
import amvvm.interfaces.IObservableObject;
import amvvm.interfaces.IProxyObservableObject;
import amvvm.util.ObjectPool;

import android.util.Property;

/**
 * Base implementation of an IObservableObject.
 * @author Tim Stratton
 *
 */
public abstract class ObservableObject
implements IObservableObject
{	
	//object pool for requesting arguments
	protected ObjectPool<IObjectListener.EventArg> argPool = new ObjectPool<IObjectListener.EventArg>()
	{	
		@Override
		protected void cleanObj(EventArg obj)
		{
			obj.recycle();
		}

		@Override
		protected <S extends EventArg> S createNewObject(Class<S> objType)
		{
			try
			{
				return objType.newInstance();
			}
			catch (InstantiationException e)
			{
			}
			catch (IllegalAccessException e)
			{
			}
			return null;
		}
	};
	
	//map relating a listener to a source by name
	protected HashMap<IObjectListener, ArrayList<String>> tracableListeners = new HashMap<IObjectListener, ArrayList<String>>();
	
	//map tracking properties it should react to.
	protected HashMap<String, ArrayList<String>> reactions = new HashMap<String, ArrayList<String>>();
	
	
	@Override
	public void addReaction(String localProperty, String reactsTo)
	{
		if (!reactions.containsKey(reactsTo))
			reactions.put(reactsTo, new ArrayList<String>());
		
		reactions.get(reactsTo).add(localProperty);
	}
	
	@Override
	public void clearReactions()
	{
		reactions.clear();
	}
	
	@Override
	public Object getSource()
	{
		return this;
	}
	
	@Override
	public IObservableObject getProxyObservableObject()
	{
		return this;
	}
	
	@Override
	public void notifyListener(String propertyName, IProxyObservableObject oldPropertyValue, IProxyObservableObject newPropertyValue)	
	{
		if (getProxyObservableObject() == null)
			return;
		
			//unregister old object
		if (oldPropertyValue != null && oldPropertyValue.getProxyObservableObject() != null)
			oldPropertyValue.getProxyObservableObject().unregisterListener(propertyName, getProxyObservableObject());
		
			//register new object
		if (newPropertyValue != null && newPropertyValue.getProxyObservableObject() != null)
			newPropertyValue.getProxyObservableObject().registerListener(propertyName, getProxyObservableObject());
		
		//notify change
		notifyListener(propertyName);
	}
	
	private IObjectListener[] tempListenerArray = new IObjectListener[0];
	
	@Override
	public void notifyListener(String propertyName)
	{	
		synchronized (tracableListeners)
		{
            EventArg arg = argPool.checkOut(EventArg.class);

			//notify properties that react to this property
			if (reactions.size() > 0 && reactions.containsKey(propertyName))
			{
				ArrayList<String> props = reactions.get(propertyName);
				for(int i=0;i<props.size();i++)
				{
					notifyListener(props.get(i));
				}
			}
						
			tempListenerArray = tracableListeners.keySet().toArray(tempListenerArray);
			
			for(int i=0;i<tracableListeners.size();i++)
			{
				//gets a listener
				IObjectListener listener = tempListenerArray[i];
				
				//get the sources of the listeners
				ArrayList<String> sourceNames = tracableListeners.get(listener);
				for(int j=0;j<sourceNames.size();j++)
				{
					String sourceName = sourceNames.get(j);
                    arg.setAll(sourceName, getSource());
                    arg.setPropagationId(propertyName);
					listener.onEvent(arg);
				}
			}		
			argPool.checkIn(arg);
		}
	}

	@Override
	public void notifyListener()
	{		
		notifyListenerRecursive(null);
	}
	
	private void notifyListenerRecursive(EventArg bubbledArg)
	{
		synchronized(tracableListeners)
		{
            EventArg arg = argPool.checkOut(EventArg.class);
			
			//if not null, means this change is bubbling up through the object till it reaches the top level
			//(probably the activity or head view model)
			//grab the full path at this point and add it to this new argument
			if (bubbledArg != null)
				arg.setPropagationId(bubbledArg.generateNextPropagationId());
			String ph = arg.getPropagationId();
			
			//notify properties that react to this property
			if (reactions.size() > 0 && reactions.containsKey(ph))
			{
				ArrayList<String> props = reactions.get(ph);
				for(int i=0;i<props.size();i++)
				{
					notifyListener(props.get(i));
				}
			}
			
			tempListenerArray = tracableListeners.keySet().toArray(tempListenerArray);
			
			//signal the listeners
			for(int i=0;i<tracableListeners.size();i++)
			{
				IObjectListener listener = tempListenerArray[i];
				ArrayList<String> sourceNames = tracableListeners.get(listener);
				for(int j=0;j<sourceNames.size();j++)
				{
                    arg.setAll(sourceNames.get(j), getSource());
					listener.onEvent(arg);
				}
			}		
			argPool.checkIn(arg);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IProxyObservableObject> T registerAs(String propertyName, IProxyObservableObject parentObj)
	{
		if (getProxyObservableObject() == null || parentObj == null || parentObj.getProxyObservableObject() == null)
			return null;
		
		//let the parent object listen to 'this' (this being the getSource() which can be overriden to be anything)
		getProxyObservableObject().getProxyObservableObject().unregisterListener(propertyName, parentObj.getProxyObservableObject());
		getProxyObservableObject().getProxyObservableObject().registerListener(propertyName, parentObj.getProxyObservableObject());
		return (T)this;
			
	}
	
	@Override
	public void onEvent(EventArg arg)
	{
        notifyListenerRecursive(arg);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IProxyObservableObject> T registerListener(String sourceName, IObjectListener listener)
	{	
		synchronized(tracableListeners)
		{
			if (listener != null)
			{			
				if (tracableListeners.get(listener) == null)
					tracableListeners.put(listener, new ArrayList<String>());
				
				tracableListeners.get(listener).add(sourceName);
			}
			return (T)this;
		}		
	}
	
	@Override
	public void unregisterListener(String sourceName, IObjectListener listener)
	{
		synchronized(tracableListeners)
		{
			if (listener == null || !tracableListeners.containsKey(listener) || !tracableListeners.get(listener).contains(sourceName))
				return;
			tracableListeners.get(listener).remove(sourceName);		
		}
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public Property<Object,Object> getProperty(String name)
	{
		if (getSource() == null)
			return null;
		
		PropertyStore store = getPropertyStore();
		if (store == null)
			return null;
		
		return (Property<Object, Object>) store.getProperty(getSource().getClass(), name);
	}
	
}
