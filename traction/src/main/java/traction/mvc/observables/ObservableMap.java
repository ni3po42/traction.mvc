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

import android.util.Property;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import traction.mvc.interfaces.IPropertyStore;

public class ObservableMap
    extends ObservableObject
    implements Map<String, Object>
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
        public internalStore(Class<?> hostClass) {
            super(hostClass);
        }

        @Override
        public Property<?, ?> getProperty(String name)
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

    protected final internalStore store;
    protected final Map<String,Object> internalMap;

    public ObservableMap(Map<String, Object> map, Class<?> scopeInterface)
    {
        if (scopeInterface == null)
            throw new IllegalArgumentException("scopeInterface must not be null");
        if (!scopeInterface.isInterface())
            throw new IllegalArgumentException("scopeInterface must be an interface");
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");

        this.internalMap = map;
        this.store = new internalStore(scopeInterface);
    }

    @Override
    public void clear()
    {
        Set<String> keys = internalMap.keySet();
        Iterator<String> iterator = keys.iterator();
        while(iterator.hasNext())
        {
            String current = iterator.next();
            if (internalMap.get(current) instanceof IProxyObservableObject)
            {
                IProxyObservableObject obj = (IProxyObservableObject)internalMap.get(current);
                obj.getProxyObservableObject().getObservable().unregisterListener(String.valueOf(current), getProxyObservableObject());
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
    public Set<Entry<String, Object>> entrySet() {
        return internalMap.entrySet();
    }

    @Override
    public Object get(Object o) {
        if (!internalMap.containsKey(o))
            return null;
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
    public Object put(String k, Object v)
    {
        if (v instanceof IProxyObservableObject)
        {
            if (internalMap.containsKey(k) && internalMap.get(k) instanceof IProxyObservableObject)
            {
                IProxyObservableObject obj = (IProxyObservableObject)internalMap.get(k);
                obj.getProxyObservableObject().getObservable().unregisterListener(String.valueOf(k), getProxyObservableObject());
            }
            IProxyObservableObject obj = (IProxyObservableObject)v;
            obj.getProxyObservableObject().getObservable().registerListener(String.valueOf(k), getProxyObservableObject());
        }

       store.removeKey(k);
        if (k != null && v != null) {
            Object old = internalMap.containsKey(k) ? internalMap.get(k) : null;
            Object returnObj = internalMap.put(k, v);
            notifyListener(String.valueOf(k), old, v);
            return returnObj;
        }
        return null;
    }


    @Override
    public void putAll(Map<? extends String, ? extends Object> map)
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
                    obj.getProxyObservableObject().getObservable().unregisterListener(String.valueOf(current), getProxyObservableObject());
                }
                IProxyObservableObject obj = (IProxyObservableObject)map.get(current);
                obj.getProxyObservableObject().getObservable().registerListener(String.valueOf(current), getProxyObservableObject());
            }
            store.removeKey(current);
        }
        internalMap.putAll(map);

        notifyListener();
    }

    @Override
    public Object remove(Object key) {
        Object obj = internalMap.remove(key);
        store.removeKey(String.valueOf(key));
        notifyListener(String.valueOf(key), obj, null);
        if (obj instanceof IProxyObservableObject)
        {
            ((IProxyObservableObject)obj).getProxyObservableObject().getObservable().unregisterListener(String.valueOf(key), getProxyObservableObject());
        }
        return obj;
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public Collection<Object> values() {
        return internalMap.values();
    }

    @Override
    protected IPropertyStore getPropertyStore()
    {
        return store;
    }
}