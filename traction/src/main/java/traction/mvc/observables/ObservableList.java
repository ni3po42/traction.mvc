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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import traction.mvc.interfaces.IObservableList;
import traction.mvc.interfaces.IPropertyStore;

import android.util.Property;

/**
 * A concrete class of IObservableList. This allows a list to be a container of this oberservable list
 * @author Tim Stratton
 *
 * @param <T> : item type of list
 */
public class ObservableList<T>
extends ListAdapter
implements IObservableList<T>
{
	//internal list to store
	protected List<T> internalImp;

    private ObservableObject proxy = new ObservableObject() {
        @Override
        protected IPropertyStore getPropertyStore() {
            return null;
        }

        @Override
        public Property<Object, Object> getProperty(String name)
        {
            Class<?> c = int.class;
            if (name.equals("Count"))
                return (Property<Object, Object>) Property.of(getSource().getClass(), c, name);
            return null;
        }

        @Override
        public Object getSource() {
            return ObservableList.this;
        }
    };

    @Override
    protected List<?> getList() {
        return this;
    }

    /**
	 * Observable readonly property that exposes the list's size
	 */
	
	@Override
	public int getCount()
	{
		return size();
	}

    /**
     * Constructor. Requires a backing list instance to be passed in. Package only!
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
        int s = getInternalCollection().size();
		if(getInternalCollection().add(arg0))
		{
            notifyAdapterOfChange();
            proxy.notifyListener("Count", s, s+1);
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0)
	{
        int s = getInternalCollection().size();
		boolean returnValue = getInternalCollection().addAll(arg0);
		if (returnValue)
		{
            notifyAdapterOfChange();
            proxy.notifyListener("Count", s, getInternalCollection().size());
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
            notifyAdapterOfChange();
            proxy.notifyListener("Count", size, 0);
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
        int s = getInternalCollection().size();
		boolean returnValue = getInternalCollection().remove(arg0);
		if (returnValue)
		{
            notifyAdapterOfChange();
            proxy.notifyListener("Count", s, s-1);
		}
		return returnValue;
	}

	@Override
	public boolean removeAll(Collection<?> arg0)
	{
        int s = getInternalCollection().size();
		boolean returnValue =  getInternalCollection().removeAll(arg0);
		if (returnValue)
		{
            notifyAdapterOfChange();
            proxy.notifyListener("Count", s, getInternalCollection().size());
		}
		return returnValue;
	}

	@Override
	public boolean retainAll(Collection<?> arg0)
	{
        int s = getInternalCollection().size();
		boolean returnValue = getInternalCollection().retainAll(arg0);
		if (returnValue)
		{
            notifyAdapterOfChange();
            proxy.notifyListener("Count", s, getInternalCollection().size());
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

    @Override
    public <T1> T1[] toArray(T1[] arg0) {
        return getInternalCollection().toArray(arg0);
    }

    @Override
	public void add(int location, T object)
	{
        int size = getInternalCollection().size();
        getInternalCollection().add(location, object);
        notifyAdapterOfChange();
        proxy.notifyListener("Count", size, getInternalCollection().size());
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1)
	{
        int size = getInternalCollection().size();
        boolean b = getInternalCollection().addAll(arg0, arg1);
        if (b) {
            notifyAdapterOfChange();
            proxy.notifyListener("Count", size, getInternalCollection().size());
        }
        return b;
	}

	@Override
	public T get(int location)
	{
	    return getInternalCollection().get(location);
	}

	@Override
	public int indexOf(Object object)
	{
		return getInternalCollection().indexOf(object);
	}

	@Override
	public int lastIndexOf(Object object)
	{
		return getInternalCollection().lastIndexOf(object);
	}

	@Override
	public ListIterator<T> listIterator()
	{
	    return getInternalCollection().listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int location)
	{
	    return  getInternalCollection().listIterator(location);
	}

	@Override
	public T remove(int location)
	{
        int s = getInternalCollection().size();
        T b = getInternalCollection().remove(location);
        notifyAdapterOfChange();
        proxy.notifyListener("Count", s, getInternalCollection().size());
        return b;
	}

	@Override
	public T set(int location, T object)
	{
        int s = getInternalCollection().size();
        T b = getInternalCollection().set(location, object);
        notifyAdapterOfChange();
        if (s != getInternalCollection().size())
            proxy.notifyListener("Count", s, getInternalCollection().size());
        return b;
	}

	@Override
	public List<T> subList(int start, int end)
	{
	    return getInternalCollection().subList(start, end);
	}

    private void notifyAdapterOfChange()
    {
        notifyDataSetChanged();
    }

    @Override
    public ObservableObject getProxyObservableObject() {
        return proxy;
    }
}
