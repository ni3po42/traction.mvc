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

import android.util.Property;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import amvvm.interfaces.IPropertyStore;
import amvvm.interfaces.IProxyObservableObject;

public class ObservableMap<V>
    extends ObservableObject
    implements Map<String, V>
{

    class internalProperty extends Property<Object,Object>
    {
        boolean isNull;
        public internalProperty(Class<Object> type, String key, boolean isNull) {
            super(type, key);
            this.isNull = isNull;
        }

        @Override
        public Object get(Object o) {
            if (!(o instanceof ObservableMap) || isNull)
                return null;
            return ((ObservableMap)o).get(getName());
        }

        @Override
        public void set(Object object, Object value)
        {
            if (!(object instanceof ObservableMap))
                return;
            ((ObservableMap)object).put(getName(), value);
        }
    }

    class internalStore
    extends PropertyStore
    {
        @Override
        public Property<?, ?> getProperty(Class<?> hostClass, String name)
        {
            if (!properties.containsKey(name) && ObservableMap.this.internalMap.containsKey(name))
            {
                properties.put(name, new internalProperty((Class<Object>) ObservableMap.this.internalMap.get(name).getClass(), name, false));
            }
            else if (!properties.containsKey(name))
            {
                properties.put(name, new internalProperty(Object.class, name, true));
            }
            return properties.get(name);
        }
        void removeKey(String name)
        {
            if (properties.containsKey(name))
            {
                properties.remove(name);
            }
        }
        void removeAllKeys()
        {
            properties.clear();
        }
    }

    protected final Map<String,V> internalMap;
    public ObservableMap(Map<String, V> map)
    {
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");

        this.internalMap = map;
    }

    protected final internalStore store = new internalStore();

    @Override
    public IPropertyStore getPropertyStore() {
        return store;
    }

    @Override
    public void clear()
    {
        Set<String> keys = (Set<String>) internalMap.keySet();
        Iterator<String> iterator = keys.iterator();
        while(iterator.hasNext())
        {
            String current = iterator.next();
            if (internalMap.get(current) instanceof IProxyObservableObject)
            {
                IProxyObservableObject obj = (IProxyObservableObject)internalMap.get(current);
                obj.getProxyObservableObject().unregisterListener(String.valueOf(current), getProxyObservableObject());
            }
        }

        internalMap.clear();
        store.removeAllKeys();
        notifyListener();
    }

    @Override
    public boolean containsKey(Object o) {
        return internalMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return internalMap.containsValue(o);
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return internalMap.entrySet();
    }

    @Override
    public V get(Object o) {
        return internalMap.get(o);
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return internalMap.keySet();
    }

    @Override
    public V put(String k, V v)
    {
        if (v instanceof IProxyObservableObject)
        {
            if (internalMap.containsKey(k) && internalMap.get(k) instanceof IProxyObservableObject)
            {
                IProxyObservableObject obj = (IProxyObservableObject)internalMap.get(k);
                obj.getProxyObservableObject().unregisterListener(String.valueOf(k), getProxyObservableObject());
            }
            IProxyObservableObject obj = (IProxyObservableObject)v;
            obj.getProxyObservableObject().registerListener(String.valueOf(k), getProxyObservableObject());
        }

        store.removeKey(k);
        V obj = internalMap.put(k,v);

        notifyListener(String.valueOf(k));
        return obj;
    }


    @Override
    public void putAll(Map<? extends String, ? extends V> map)
    {
        Set<String> keys = (Set<String>) map.keySet();
        Iterator<String> iterator = keys.iterator();
        while(iterator.hasNext())
        {
            String current = iterator.next();
            if (map.get(current) instanceof IProxyObservableObject)
            {
                if (internalMap.containsKey(current) && internalMap.get(current) instanceof IProxyObservableObject)
                {
                    IProxyObservableObject obj = (IProxyObservableObject)internalMap.get(current);
                    obj.getProxyObservableObject().unregisterListener(String.valueOf(current), getProxyObservableObject());
                }
                IProxyObservableObject obj = (IProxyObservableObject)map.get(current);
                obj.getProxyObservableObject().registerListener(String.valueOf(current), getProxyObservableObject());
            }
            store.removeKey(current);
        }
        internalMap.putAll(map);

        notifyListener();
    }

    @Override
    public V remove(Object key) {
        V obj = internalMap.remove(key);
        store.removeKey(String.valueOf(key));
        notifyListener(String.valueOf(key));
        if (obj instanceof IProxyObservableObject)
        {
            ((IProxyObservableObject)obj).getProxyObservableObject().unregisterListener(String.valueOf(key), getProxyObservableObject());
        }
        return obj;
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public Collection<V> values() {
        return internalMap.values();
    }

    @Override
    public void autoFieldRegistration()
    {
        hasAutoRegisteredFields = true;
    }
}
