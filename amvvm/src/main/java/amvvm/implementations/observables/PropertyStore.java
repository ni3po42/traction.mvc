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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;
import android.util.Property;

import amvvm.interfaces.IPropertyStore;

/**
 * A utility class for searching properties on an object and cache them
 * @author Tim Stratton
 *
 */
public class PropertyStore
    implements IPropertyStore
{
	//prefixes to test against
	private static final String[] prefixes = new String[]{"get","is"};
	private static final Class<?>[] emptyclasses = new Class<?>[]{};
	
	private Hashtable<String, Property<?,?>> properties = new Hashtable<String, Property<?,?>>();
	
	/**
	 * get property form store
	 */
    @Override
	public Property<?,?> getProperty(Class<?> hostClass, String name)
	{
		if (!properties.containsKey(name))
		{
			properties.put(name, find(hostClass, name) );
		}
		return properties.get(name);
	}
	
	/**
	 * Try to find a property.	 * 
	 * @param hostClass
	 * @param name
	 * @return
	 */
	public static Property<?,?> find(Class<?> hostClass, String name)
	{
		//The Property Class in the android SDK requires a property type, which we don't have at this time, so we must look it up manually
		Class<?> valueType = null;
		//method check
		for(int i=0;i<prefixes.length;i++)
		{
			try
			{
				Method method = hostClass.getMethod(prefixes[i]+name,emptyclasses);
				if (method != null)
				{
					valueType = method.getReturnType();
					break;
				}
			}
			catch(Exception ex){}
		}
		
		//field check
		if (valueType == null)
		{
			try
			{
				Field field = hostClass.getField(name);
				if (field != null)
				{
					valueType = field.getType();
				}
			}
			catch(Exception ex){}
		}
		
		if (valueType == null)
			throw new RuntimeException("Cannot find property '"+name+"' in type '"+hostClass.getName()+"'.");
		
		//we have th host class, the value class and the name, now we can get the property
		return Property.of(hostClass, valueType, name);
	}
	
}
