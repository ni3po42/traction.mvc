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

package traction.mvc.observables;

import java.util.ArrayList;
import java.util.HashMap;

import traction.mvc.interfaces.IObjectListener;
import traction.mvc.interfaces.IObservableObject;
import traction.mvc.interfaces.IPropertyStore;
import traction.mvc.interfaces.IProxyObservableObject;

import android.util.Property;

/**
 * Base implementation of an IObservableObject.
 * @author Tim Stratton
 *
 */
public abstract class ObservableObject
implements IObservableObject
{
	//map relating a listener to a source by name
	protected HashMap<IObjectListener, ArrayList<String>> tracableListeners = new HashMap<IObjectListener, ArrayList<String>>();
	
	//map tracking properties it should react to.
	protected HashMap<String, ArrayList<String>> reactions = new HashMap<String, ArrayList<String>>();

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
                    listener.onEvent(IObjectListener.Utility.generatePropagationId(propertyName, sourceName));
				}
			}
		}
	}

	@Override
	public void notifyListener()
	{		
		notifyListenerRecursive(null);
	}
	
	private void notifyListenerRecursive(String propagationId)
	{
		synchronized(tracableListeners)
		{
			//notify properties that react to this property
			if (reactions.size() > 0 && reactions.containsKey(propagationId))
			{
				ArrayList<String> props = reactions.get(propagationId);
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
                    listener.onEvent(IObjectListener.Utility.generatePropagationId(propagationId, sourceNames.get(j)));
				}
			}
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
	public void onEvent(String propagationId)
	{
        notifyListener(propagationId);
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

    protected abstract IPropertyStore getPropertyStore();

	@SuppressWarnings("unchecked")
	@Override
	public Property<Object,Object> getProperty(String name)
	{
		if (getSource() == null)
			return null;

        IPropertyStore store = getPropertyStore();
		if (store == null)
			return null;
		
		return (Property<Object, Object>) store.getProperty(name);
	}
}
