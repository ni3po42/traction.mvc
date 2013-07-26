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

package amvvm.util;

import java.util.HashMap;
import java.util.HashSet;

/**
 * A super simple object pool
 * @author Tim Stratton
 *
 * @param <T> : type of object to pull from pool
 */
public abstract class ObjectPool<T>
{	
	/**
	 * map of available objects
	 */
	private HashMap<Class<? extends T>, HashSet<T>> available = new HashMap<Class<? extends T>, HashSet<T>>();
	
	/**
	 * map of unavailable objects
	 */
	private HashMap<Class<? extends T>, HashSet<T>> unavailable = new HashMap<Class<? extends T>, HashSet<T>>();

	/**
	 * creates a new instance for the available pool
	 * @param objType
	 * @return
	 */
	protected abstract <S extends T> S createNewObject(Class<S> objType);
	
	/**
	 * clean an object to a initial state
	 * @param obj
	 */
	protected abstract void cleanObj(T obj);

	private Object[] tempArray = new Object[0];
	
	/**
	 * Checks out object of type S
	 */
	 @SuppressWarnings("unchecked")
	public synchronized <S extends T> S checkOut(Class<S> objType)
	{
		T obj = null;
		if (!available.containsKey(objType))
			available.put(objType, new HashSet<T>());
		
		HashSet<T> hs = available.get(objType);
		
		if (hs.size() > 0)	
		{
			tempArray = hs.toArray(tempArray);
			obj = (T)tempArray[0];
			hs.remove(obj);
		}
		else
			obj = createNewObject(objType);
		
		if (!unavailable.containsKey(objType))
			unavailable.put(objType, new HashSet<T>());
		
		hs = unavailable.get(objType);
		hs.add(obj);
		cleanObj(obj);
		return (S) obj;
	}

	 /**
	  * checks in a collection of objects
	  * @param obj
	  */
	public synchronized void checkTheseIn(T ... obj)
	{
		if (obj == null)
			return;

		for(int i=0;i<obj.length;i++)
		{
			if (obj[i] == null)
				continue;
			
			HashSet<T> hs = unavailable.get(obj[i].getClass());
			
			if (!hs.contains(obj[i]))
				continue;
			hs.remove(obj[i]);
			
			hs = available.get(obj[i].getClass());
			hs.add(obj[i]);
		}
	}

	/**
	 * checks in a single object
	 * @param obj
	 */
	public synchronized void checkIn(T obj)
	{
		if (obj == null)
			return;
		
		HashSet<T> hs = unavailable.get(obj.getClass());
		
		if (!hs.contains(obj))
			return;
		
		hs.remove(obj);
		
		hs = available.get(obj.getClass());
		hs.add(obj);
	}

	/**
	 * clear all instances if all are checked in.
	 * @return : true if clean success.
	 */
	public synchronized boolean clean()
	{
		if (unavailable.size() > 0)
			return false;
		available.clear();
		return true;
	}
}
