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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import amvvm.interfaces.IObservableList;

import android.util.Property;

/**
 * A concrete class of IObservableList. This allows a list to be a container of this oberservable list
 * @author Tim Stratton
 *
 * @param <T> : item type of list
 */
public class ObservableList<T> 
extends ObservableObject
implements IObservableList<T>
{
	//internal list to store
	protected List<T> internalImp;	
	
	/**
	 * Observable readonly property that exposes the list's size
	 */
	
	@Override
	public int getCount()
	{
		return size();
	}
		
	protected ObservableList()
	{
		
	}
	
	/**
	 * Constructor. Requires a backing list instance to be passed in
	 * @param collection : backing list this class wraps, must be not null!
	 */
	public ObservableList(List<T> collection)
	{
		if (collection == null)
			throw new RuntimeException("Cannot create Observable List: list given in constructor is null.");
		
		internalImp = collection;
	}
	
	/**
	 * Exposes the internal backing list to inherited lists.
	 * @return : the internal implemented list
	 */
	protected List<T> getInternalCollection()
	{
		return internalImp;
	}
	
	//List methods
	//The basic idea is that this will wrap the given list, so all methods will
	//pass along the call to the internal list, or return a default value when
	//appropriate. Fires the 'tryFireListeners' method if a change is detected.
	
	@Override
	public boolean add(T arg0)
	{
		if(getInternalCollection().add(arg0))
		{
			
			notifyListener();
			notifyListener("Count");
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0)
	{
		if (arg0 == null || arg0.size() == 0)
			return true;
		boolean returnValue = getInternalCollection().addAll(arg0);
		if (returnValue)
		{
			notifyListener();
			notifyListener("Count");			
		}
		return returnValue;
	}

	@Override
	public void clear()
	{
		int size = size();
		getInternalCollection().clear();
		if (size != size())
		{
			notifyListener();
			notifyListener("Count");			
		}
	}

	@Override
	public boolean contains(Object arg0)
	{
		return getInternalCollection().contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0)
	{
		return getInternalCollection().containsAll(arg0);
	}

	@Override
	public boolean isEmpty()
	{
		return getInternalCollection().isEmpty();
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return getInternalCollection().iterator();
	}

	@Override
	public boolean remove(Object arg0)
	{
		boolean returnValue = getInternalCollection().remove(arg0);
		if (returnValue)
		{
			notifyListener();
			notifyListener("Count");	
		}
		return returnValue;
	}

	@Override
	public boolean removeAll(Collection<?> arg0)
	{
		boolean returnValue =  getInternalCollection().removeAll(arg0);
		if (returnValue)
		{
			notifyListener();
			notifyListener("Count");	
		}
		return returnValue;
	}

	@Override
	public boolean retainAll(Collection<?> arg0)
	{
		boolean returnValue = getInternalCollection().retainAll(arg0);
		if (returnValue)
		{
			notifyListener();
			notifyListener("Count");	
		}
		return returnValue;
	}

	@Override
	public int size()
	{
		return getInternalCollection().size();
	}
	
	@Override
	public Object[] toArray()
	{
		return getInternalCollection().toArray();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] arg0)
	{
		return getInternalCollection().toArray(arg0);
	}
		
	@Override
	public void add(int location, T object)
	{
		if (getInternalCollection() != null)
		{
			getInternalCollection().add(location, object);	
			notifyListener();
			notifyListener("Count");	
		}
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1)
	{
		if (getInternalCollection() != null)
		{
			boolean b = getInternalCollection().addAll(arg0, arg1);
			notifyListener();
			notifyListener("Count");
			return b;
		}
		return false;
	}

	@Override
	public T get(int location)
	{
		if (getInternalCollection() != null)
			return getInternalCollection().get(location);
		return null;
	}

	@Override
	public int indexOf(Object object)
	{
		if (getInternalCollection() != null)
			return getInternalCollection().indexOf(object);
		return -1;
	}

	@Override
	public int lastIndexOf(Object object)
	{
		if (getInternalCollection() != null)
			return getInternalCollection().lastIndexOf(object);
		return -1;
	}

	@Override
	public ListIterator<T> listIterator()
	{
		if (getInternalCollection() != null)
			return getInternalCollection().listIterator();
		return null;
	}

	@Override
	public ListIterator<T> listIterator(int location)
	{
		if (getInternalCollection() != null)
			return  getInternalCollection().listIterator(location);
		return null;
	}

	@Override
	public T remove(int location)
	{
		if (getInternalCollection() != null)
		{
			T b = getInternalCollection().remove(location);
			notifyListener();
			notifyListener("Count");
			return b;
		}
		return null;
	}

	@Override
	public T set(int location, T object)
	{
		if (getInternalCollection() != null)
		{
			int s = getInternalCollection().size();
			T b = getInternalCollection().set(location, object);
			notifyListener();
			if (s != getInternalCollection().size())
				notifyListener("Count");
			return b;
		}
		return null;
	}

	@Override
	public List<T> subList(int start, int end)
	{
		if (getInternalCollection() != null)
			return getInternalCollection().subList(start, end);
		return null;
	}

	@Override
	public PropertyStore getPropertyStore()
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Property<Object, Object> getProperty(String name)
	{
		Class<?> c = int.class;
		if (name.equals("Count"))
			return (Property<Object, Object>) Property.of(getSource().getClass(), c, name);
		return null;
	}
	

}
