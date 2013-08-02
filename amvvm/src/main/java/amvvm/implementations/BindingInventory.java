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
import amvvm.interfaces.IProxyObservableObject;
import amvvm.interfaces.IUIElement;
import amvvm.interfaces.IAccessibleFragmentManager;
import amvvm.interfaces.IObjectListener;
import amvvm.interfaces.IObjectListener.EventArg;

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
	private final static Pattern splitIndex = Pattern.compile("([^@]+)(@(\\d+)\\[(.+)\\])?");
	
	//used for determining a range of paths to get from the inventory. Useful for getting all properties down a 
	//specific branch
	private final static String pathTerminator = "{";
	
	//current context object, may be the view model or model
	private IProxyObservableObject context;
	
	//there may be many levels of inventories, this points to the parent inventory. will be nul if it is the root
	private BindingInventory parentInventory;
	
	private final TreeMap<String, PathBinding> map = new TreeMap<String, PathBinding>();

	private IObjectListener contextListener = new IObjectListener()
	{		
		@Override
		public void onEvent(EventArg arg)
		{
			if (arg ==  null)
				return;
			
			onContextSignaled(arg);
		}
	};
	
	public void linkFragments(FragmentManager fragmentManager)
	{		
		//will be used laster for Dynamic fragments
		/*for (int i=0;i< fragmentMapping.size();i++)
		{
			Object fragment = fragmentManager.findFragmentById(fragmentMapping.get(i).id);
			sendUpdate(fragmentMapping.get(i).path, fragment);	
		}*/
	}
		
	private String[] tempStringArray = new String[0];
	
	protected void onContextSignaled(EventArg arg)
	{			
		String path = null;
		Object value = null;

        path = arg.generateNextPropagationId();
        if (path != null && map.containsKey(path))
            value = DereferenceValue(path);

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
				Object subValue = DereferenceValue(subPath);
									
				for(int j=0;j<elements.size();j++)
				{
					elements.get(j).recieveUpdate(subValue);
				}
			}			
		}
		else
		{
			ArrayList<IUIElement<?>> elements = map.get(path).getUIElements();
			for(int i=0;i<elements.size();i++)
			{
				elements.get(i).recieveUpdate(value);
			}
		}
			
		if (getContextObject() != null && 
				getContextObject().getProxyObservableObject() != null && 
				getContextObject().getSource() instanceof IAccessibleFragmentManager)
		{
			((IAccessibleFragmentManager)getContextObject().getSource()).linkFragments(this);
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

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void  fireCommand(String commandPath, Object commandArg)
	{
		Object command = DereferenceValue(commandPath);
		if (command instanceof ICommand)
		{
			ICommand c = (ICommand)command;
			c.execute(commandArg);
		}
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
	
	public void setContextObject(IProxyObservableObject object)
	{
		if (context != null && context.getProxyObservableObject() != null)
			context.getProxyObservableObject().unregisterListener("", contextListener);
		context = object;
		if (context != null && context.getProxyObservableObject() != null)
			context.getProxyObservableObject().registerListener("", contextListener);
	}
	
	public IProxyObservableObject getContextObject()
	{
		return context;
	}
	
	public void track (IUIElement<?> element)
	{
		if (element.getPath() == null)
			return;//no path, no track.
		
		if (!map.containsKey(element.getPath()))
		{
			map.put(element.getPath(), new PathBinding());
		}
		PathBinding p = map.get(element.getPath());
		p.addUIElement(element);
	}
	
	@Deprecated
	public void trackFragmentX(int id, String path)
	{
		//will be used later...
		//fragmentMap newMap =new fragmentMap(id,path);
		//if (!fragmentMapping.contains(newMap))
		//	fragmentMapping.add(newMap);
	}
	
	@SuppressWarnings("unchecked")
	public void sendUpdate(String path, Object value)
	{		
		if (path != null && path.equals("."))
			return;
		
		Matcher matches = pathPattern.matcher(path);
		if (!matches.find())
			return;
		
		BindingInventory currentInventory = getInventoryByMatchedPattern(matches);
				
		Object currentContext = currentInventory.getContextObject().getSource();				
		String[] chains = split.split(matches.group(2));
		
		Property<Object,Object> prop = null;
		String indexStr = null;
		
		 for(int i=0;i<chains.length;i++)
		 { 		
			 if (currentContext == null)
				 return;//throw new NullPointerException("Cannot send value update to null object.");
			 Matcher m = splitIndex.matcher(chains[i]);
			 if (!m.find())
				 throw new RuntimeException("Not a valid membeer: " + chains[i]);
			 
			 String member = m.group(1);
			indexStr = m.group(3);
			
			prop = (Property<Object, Object>) PropertyStore.find(currentContext.getClass(), member);
			if (i + 1 < chains.length)
			{
				currentContext = extractByProxy(getIndex(indexStr, prop.get(currentContext)));
				indexStr = null;
			}
		 }
			
		 if (currentContext == null)
			 return;//throw new NullPointerException("Cannot send value update to null object.");
		 if (prop == null)
			 throw new InvalidParameterException("invalid path supplied: "+path);
		 
		 Object propertyCurrentValue = extractByProxy(getIndex(indexStr, prop.get(currentContext)));
				 
		 if ((propertyCurrentValue != null && !propertyCurrentValue.equals(value))				 
				 || (propertyCurrentValue == null && value != null))
		setIndex(prop, indexStr, currentContext, value);
	}
		
	private Object getIndex(String indexStr, Object host)
	{
		if (indexStr == null || host == null || !(host instanceof IObservableList))
			return host;
		
		try
		{
			int index = Integer.parseInt(indexStr);
			return ((IObservableList<?>)host).get(index);
		}
		catch(Exception e)
		{
			
		}
		return host;
	}
	
	@SuppressWarnings("unchecked")
	private void setIndex(Property<Object,Object> prop, String indexStr, Object host, Object value)
	{
		if (indexStr == null)
			prop.set(host, value);
		else
		{
			int index = Integer.parseInt(indexStr);
			Object listObj = prop.get(host);
			((IObservableList<Object>)listObj).set(index, value);
		}
	}
	
	public void sendUpdateFromUIElement(IUIElement<?> element, Object value)
	{
		sendUpdate(element.getPath(), value);
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
	public Object DereferenceValue(String path)
	{		
		if (path == null || context == null || context.getSource() == null)
			return null;
		
		if (path.equals("."))
			return context;
		
		Matcher matches = pathPattern.matcher(path);
		
		if (!matches.find())
			return null;
		
		BindingInventory currentInventory = getInventoryByMatchedPattern(matches);
			
		Object currentContext = currentInventory.getContextObject().getSource();
			
		String[] chains = split.split(matches.group(2));
			
		 for(int i=0;i<chains.length;i++)
		 { 		
			 if (currentContext == null)
				 return null;
			 Matcher m = splitIndex.matcher(chains[i]);
			 if (!m.find())
				 throw new RuntimeException("Not a valid membeer: " + chains[i]);
			 
			 String member = m.group(1);				
			 Property<Object,Object> prop = (Property<Object, Object>) PropertyStore.find(currentContext.getClass(), member);
			 currentContext = extractByProxy(prop.get(currentContext));	
						 
			 String indexStr = m.group(3);
			if (currentContext != null && currentContext instanceof IObservableList && indexStr != null)
			{	
				try
				{
					int index = Integer.parseInt(indexStr);
					currentContext = extractByProxy(((IObservableList<?>)currentContext).get(index));					
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}			
			}											
		 }
		 return currentContext;
	}	
	
	private Object extractByProxy(Object obj)
	{
		if (obj instanceof IProxyObservableObject)
			 return((IProxyObservableObject)obj).getSource();
		return obj;
	}
	

	@SuppressWarnings("unchecked")
	public Class<?> DereferencePropertyType(String path)
	{
        if (path == null)
            return null;

		if (path != null && path.equals("."))
			return context.getSource() == null ? null : context.getSource().getClass();
		
		Matcher matches = pathPattern.matcher(path);
		if (!matches.find())
			return null;
		
		BindingInventory currentInventory = getInventoryByMatchedPattern(matches);
				
		Object currentContext = currentInventory.getContextObject().getSource();				
		String[] chains = split.split(matches.group(2));
		
		Property<Object,Object> prop = null;
		String indexStr = null;
		
		 for(int i=0;i<chains.length;i++)
		 { 		
			 if (currentContext == null)
				 return null;
			 Matcher m = splitIndex.matcher(chains[i]);
			 if (!m.find())
				 throw new RuntimeException("Not a valid membeer: " + chains[i]);
			 
			 String member = m.group(1);
			indexStr = m.group(3);
			
			prop = (Property<Object, Object>) PropertyStore.find(currentContext.getClass(), member);
			if (i + 1 < chains.length)
			{
				currentContext = extractByProxy(getIndex(indexStr, prop.get(currentContext)));
				indexStr = null;
			}
		 }
			
		 if (prop == null)
			 throw new InvalidParameterException("invalid path supplied: "+path);
		 
		 return prop.getType();
	}	
	
}
