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
import traction.mvc.interfaces.IPOJO;
import traction.mvc.interfaces.IPropertyStore;

import android.util.Property;

/**
 * @author Tim Stratton
 *
 */
public abstract class ObservableObject
implements IObjectListener, IProxyObservableObject,IPOJO
{
    //map relating a listener to a source by name
    protected HashMap<IObjectListener, ArrayList<String>> tracableListeners = new HashMap<IObjectListener, ArrayList<String>>();

    private final IObservable observable = new IObservable()
    {
        @SuppressWarnings("unchecked")
        public void registerListener(String sourceName, IObjectListener listener)
        {
            synchronized(tracableListeners)
            {
                if (listener != null)
                {
                    if (tracableListeners.get(listener) == null)
                        tracableListeners.put(listener, new ArrayList<String>());

                    tracableListeners.get(listener).add(sourceName);
                }
            }
        }

        public void unregisterListener(String sourceName, IObjectListener listener)
        {
            synchronized(tracableListeners)
            {
                if (listener == null || !tracableListeners.containsKey(listener) || !tracableListeners.get(listener).contains(sourceName))
                    return;
                tracableListeners.get(listener).remove(sourceName);
            }
        }

    };

    public void addOnChange(OnPropertyChangedEvent onPropertyChangedEvent)
    {
        getObservable().registerListener("", onPropertyChangedEvent);
    }

    public void removeOnChange(OnPropertyChangedEvent onPropertyChangedEvent)
    {
        getObservable().unregisterListener("", onPropertyChangedEvent);
    }

	public Object getSource()
	{
		return this;
	}

    protected final IObservable getObservable()
    {
        return observable;
    }

	@Override
	public ObservableObject getProxyObservableObject()
	{
		return this;
	}

	public void notifyListener(String propertyName, Object oldPropertyValue, Object newPropertyValue)
	{
		if (getProxyObservableObject() == null)
			return;
		
			//unregister old object
		if (oldPropertyValue instanceof IProxyObservableObject)
        {
            ObservableObject proxy = ((IProxyObservableObject) oldPropertyValue).getProxyObservableObject();
            IObservable ob = proxy.getObservable();
            ob.unregisterListener(propertyName, getProxyObservableObject());
        }
		
			//register new object
        if (newPropertyValue instanceof IProxyObservableObject)
        {
            ObservableObject proxy = ((IProxyObservableObject) newPropertyValue).getProxyObservableObject();
            IObservable ob = proxy.getObservable();
            ob.registerListener(propertyName, getProxyObservableObject());
        }

		//notify change
        notifyListenerInternal(propertyName, oldPropertyValue, newPropertyValue);
	}
	
	private IObjectListener[] tempListenerArray = new IObjectListener[0];

	protected void notifyListenerInternal(String propertyName, Object oldValue, Object newValue)
	{	
		synchronized (tracableListeners)
		{
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
                    if (listener instanceof OnPropertyChangedEvent)
                    {
                        ((OnPropertyChangedEvent)listener).onChange(propertyName, oldValue, newValue);
                    }
                    listener.onEvent(IObjectListener.Utility.generatePropagationId(propertyName, sourceName));
				}
			}
		}
	}

	public void notifyListener()
	{
        notifyListenerInternal(null, null, null);
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public <T extends IProxyObservableObject> T registerAs(String propertyName, IProxyObservableObject parentObj)
//	{
//		if (getProxyObservableObject() == null || parentObj == null || parentObj.getProxyObservableObject() == null)
//			return null;
//
//		//let the parent object listen to 'this' (this being the getSource() which can be overriden to be anything)
//		getProxyObservableObject().getProxyObservableObject().unregisterListener(propertyName, parentObj.getProxyObservableObject());
//		getProxyObservableObject().getProxyObservableObject().registerListener(propertyName, parentObj.getProxyObservableObject());
//		return (T)this;
//
//	}

	@Override
	public void onEvent(String propagationId)
	{
        notifyListenerInternal(propagationId, null, null);
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
