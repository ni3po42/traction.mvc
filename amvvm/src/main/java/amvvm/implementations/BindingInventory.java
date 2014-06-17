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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.FragmentManager;
import android.util.Property;

import amvvm.implementations.observables.PropertyStore;
import amvvm.interfaces.ICommand;
import amvvm.interfaces.IObservableList;
import amvvm.interfaces.IPropertyStore;
import amvvm.interfaces.IProxyObservableObject;
import amvvm.interfaces.IUIElement;
import amvvm.interfaces.IObjectListener;
import amvvm.interfaces.IViewModel;

/**
 * Tracks all ui element and the property paths to the model's data, also delegates to and from the view and the model/view-model.
 * Pretty much the most important class in this library.
 * @author Tim Stratton
 *
 */
public class BindingInventory
{
	/**
	 * patterns for parsing property chains
	 */
	private final static Pattern pathPattern = Pattern.compile("(\\\\|[\\.]*)(.+)");
	private final static Pattern split = Pattern.compile("\\.");

	//used for determining a range of paths to get from the inventory. Useful for getting all properties down a 
	//specific branch
	private final static String pathTerminator = "{";
	
	//current context object, may be the view model or model
	private IProxyObservableObject context;
	private Object nonObservableContext;

	//there may be many levels of inventories, this points to the parent inventory. will be nul if it is the root
	private BindingInventory parentInventory;

	private final TreeMap<String, PathBinding> map = new TreeMap<String, PathBinding>();

	private IObjectListener contextListener = new IObjectListener()
	{
        @Override
        public void onEvent(String propagationId)
        {
            onContextSignaled(propagationId);
        }
	};

	private String[] tempStringArray = new String[0];

	protected void onContextSignaled(String path)
	{
		Object value = null;

        if (path != null && map.containsKey(path))
            value = dereferenceValue(path);

		if (path == null || !map.containsKey(path))
		{
            path = (path == null) ? "" : path + ".";
			
			NavigableMap<String, PathBinding> subMap = map.subMap(path, false, path+pathTerminator, true);

			Set<String> keys = subMap.keySet();
			tempStringArray = keys.toArray(tempStringArray);
			
			for(int i = 0;i< keys.size();i++)
			{
				String subPath = tempStringArray[i];
				ArrayList<IUIElement<?>> elements = subMap.get(subPath).getUIElements();
				Object subValue = dereferenceValue(subPath);
									
				for(int j=0;j<elements.size();j++)
				{
					elements.get(j).receiveUpdate(subValue);
				}
			}			
		}
		else
		{
			ArrayList<IUIElement<?>> elements = map.get(path).getUIElements();
			for(int i=0;i<elements.size();i++)
			{
				elements.get(i).receiveUpdate(value);
			}
		}
	}

	public BindingInventory()
	{

	}
	
	public BindingInventory(BindingInventory parentInventory)
	{
		setParentInventory(parentInventory);
	}
	
	public BindingInventory getParentInventory()
	{
		return parentInventory;
	}

    public void merge(BindingInventory inventoryToMerge)
    {
        Iterator<PathBinding> collection = inventoryToMerge.map.values().iterator();

        while(collection.hasNext())
        {
            PathBinding current = collection.next();
            ArrayList<IUIElement<?>> elements = current.getUIElements();
            if (elements != null)
            {
                int size = elements.size();
                for(int i=0;i<size;i++)
                {
                    elements.get(i).track(this);
                }
            }
        }

        inventoryToMerge.map.clear();
        inventoryToMerge.setContextObject(null);
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void  fireCommand(String commandPath, ICommand.CommandArgument commandArg)
	{
		Object command = dereferenceValue(commandPath);
		if (command instanceof ICommand)
		{
			ICommand c = (ICommand)command;
			c.execute(commandArg);
		}
        else
        {
            if (commandArg != null)
                commandArg.setEventCancelled(true);
        }
	}

    public boolean isCommand(String path)
    {
        Class<?> c = dereferencePropertyType(path);
        if (c == null)
            return false;
        return ICommand.class.isAssignableFrom(c);
    }
	
	public void setParentInventory(BindingInventory parentInventory)
	{
		this.parentInventory = parentInventory;
	}
	
	public BindingInventory getRootInventory()
	{
		BindingInventory parentInventory = this;		
		while(parentInventory.getParentInventory() != null)
		{
			parentInventory = parentInventory.getParentInventory();
		}
		return parentInventory;
	}
	
	public BindingInventory getInventoryByLevel(int level)
	{
		BindingInventory parentInventory = this;	
		for(int i = 0;i < level; i++)
		{
			if (parentInventory.getParentInventory() == null)
				return null;
			
			parentInventory = parentInventory.getParentInventory();
		}
		return parentInventory;
	}
	
	public void setContextObject(Object object)
	{
		if (context != null && context.getProxyObservableObject() != null)
			context.getProxyObservableObject().unregisterListener("", contextListener);
        if (object instanceof IProxyObservableObject)
		    context = (IProxyObservableObject)object;
        else
            nonObservableContext = object;

		if (context != null && context.getProxyObservableObject() != null)
			context.getProxyObservableObject().registerListener("", contextListener);
	}
	
	public Object getContextObject()
	{
        if (context != null)
            return context;
        else
            return nonObservableContext;
	}

    private Object extractSource()
    {
        if (nonObservableContext != null)
            return nonObservableContext;
        else
            return context.getProxyObservableObject().getSource();
    }

	public void track (IUIElement<?> element, String path)
	{
		if (path == null)
			return;//no path, no track.
		
		if (!map.containsKey(path))
		{
			map.put(path, new PathBinding());
		}
		PathBinding p = map.get(path);
		p.addUIElement(element);
	}

	@SuppressWarnings("unchecked")
	public void sendUpdate(String path, Object value)
	{
		if (path == null || path.equals("."))
			return;
		
		Matcher matches = pathPattern.matcher(path);
		if (!matches.find())
			return;
		
		BindingInventory currentInventory = getInventoryByMatchedPattern(matches);
		Object currentContext = currentInventory.extractSource();
		String[] chains = split.split(matches.group(2));
		Property<Object,Object> prop = null;
		
		 for(int i=0;i<chains.length;i++)
		 { 		
			 if (currentContext == null)
				 return;

			 String member =chains[i];

             if (currentContext instanceof IPropertyStore)
             {
                 prop = (Property<Object, Object>)((IPropertyStore)currentContext).getProperty(currentContext.getClass(), member);
             }
             else
             {
			    prop = (Property<Object, Object>) PropertyStore.find(currentContext.getClass(), member);
             }

			if (i + 1 < chains.length)
			{
				currentContext = extractByProxy(prop.get(currentContext));
			}
		 }
			
		 if (currentContext == null)
			 return;//throw new NullPointerException("Cannot send value update to null object.");
		 if (prop == null)
			 throw new InvalidParameterException("invalid path supplied: "+path);
		 
		 Object propertyCurrentValue = extractByProxy(prop.get(currentContext));
				 
		 if ((propertyCurrentValue != null && !propertyCurrentValue.equals(value))				 
				 || (propertyCurrentValue == null && value != null))
        prop.set(currentContext,value);
	}

	private BindingInventory getInventoryByMatchedPattern(Matcher matches)
	{
		BindingInventory currentInventory = this;
		String up = matches.group(1);
		
		if (up != null && up.length() == 1 && up.equals("\\"))
		{
			currentInventory = getRootInventory();				
		}
		else if (up != null && up.length() > 0)
		{
			currentInventory = getInventoryByLevel(up.length());
		}
		return currentInventory;
	}
	
	@SuppressWarnings("unchecked")
    public Object dereferenceValue(String path)
    {
		if (path == null)
			return null;
		
		if (path.equals("."))
        {
            if (context == null)
                return nonObservableContext;
            else
                return context;
        }
		
		Matcher matches = pathPattern.matcher(path);
		
		if (!matches.find())
			return null;
		
		BindingInventory currentInventory = getInventoryByMatchedPattern(matches);
			
		Object currentContext = currentInventory.extractSource();
			
		String[] chains = split.split(matches.group(2));
			
		 for(int i=0;i<chains.length;i++)
		 { 		
			 if (currentContext == null)
				 return null;
			 
			 String member = chains[i];
			 Property<Object,Object> prop = null;
             if (currentContext instanceof IPropertyStore)
                 prop = (Property<Object, Object>)((IPropertyStore)currentContext).getProperty(currentContext.getClass(), member);
             else
                 prop = (Property<Object, Object>) PropertyStore.find(currentContext.getClass(), member);

             if (prop == null)
                 return null;

			 currentContext = extractByProxy(prop.get(currentContext));
		 }
		 return currentContext;
	}	
	
	private Object extractByProxy(Object obj)
	{
		if (obj instanceof IProxyObservableObject)
			 return((IProxyObservableObject)obj).getProxyObservableObject().getSource();
		return obj;
	}

	@SuppressWarnings("unchecked")
    public Class<?> dereferencePropertyType(String path)
	{
        if (path == null)
            return null;

		if (path != null && path.equals("."))
			return context.getProxyObservableObject().getSource() == null ? null : context.getProxyObservableObject().getSource().getClass();
		
		Matcher matches = pathPattern.matcher(path);
		if (!matches.find())
			return null;
		
		BindingInventory currentInventory = getInventoryByMatchedPattern(matches);
				
		Object currentContext = currentInventory.extractSource();

		String[] chains = split.split(matches.group(2));
		
		Property<Object,Object> prop = null;
		
		 for(int i=0;i<chains.length;i++)
		 { 		
			 if (currentContext == null)
				 return null;
			 String member = chains[i];
			
			prop = (Property<Object, Object>) PropertyStore.find(currentContext.getClass(), member);
			if (i + 1 < chains.length)
			{
				currentContext = extractByProxy(prop.get(currentContext));
			}
		 }
			
		 if (prop == null)
			 throw new InvalidParameterException("invalid path supplied: "+path);
		 
		 return prop.getType();
	}

    public void clearAll()
    {
        map.clear();
    }
}
