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

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Property;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;

import amvvm.implementations.ui.viewbinding.CursorAdapter;
import amvvm.implementations.ui.viewbinding.ProxyAdapter;
import amvvm.interfaces.ICalculatedPropertiesHandler;
import amvvm.interfaces.IObservableCursor;
import amvvm.interfaces.IObservableObject;
import amvvm.interfaces.IPropertyStore;
import amvvm.interfaces.IProxyObservableObject;

/**
 * Expose a cursor that is observable, duh.
 */
public class ObservableCursor
    extends ProxyAdapter<Cursor>
    implements IObservableCursor
{
    private ICalculatedPropertiesHandler<Cursor> calculatedPropertiesHandler;
    private internalCursorWrapper internalCursor;
    private CursorAdapter internalCursorAdapter;

    @Override
    public void updateCursor(Cursor cursor)
    {
        if (internalCursor != cursor)
        {
            internalCursor = new internalCursorWrapper(cursor);
            notifyListener();
        }
    }

    @Override
    public void setCalculatedPropertiesHandler(ICalculatedPropertiesHandler<Cursor> handler)
    {
        this.calculatedPropertiesHandler = handler;
    }

    @Override
    public IPropertyStore getPropertyStore()
    {
        return internalCursor;
    }

    @Override
    protected int indexOf(Cursor notUsed)
    {
        return internalCursor.getPosition();
    }

    @Override
    protected BaseAdapter getProxyAdapter(ProxyAdapterArgument arg)
    {
        if (arg != null && internalCursorAdapter == null && internalCursor != null && arg.getContext() != null && internalCursor.getWrappedCursor() != null)
        {
            internalCursorAdapter = new CursorAdapter(arg, internalCursor);
        }
        return internalCursorAdapter;
    }

    @Override
    protected void clearProxyAdapter()
    {
        if (internalCursorAdapter != null)
            internalCursorAdapter.changeCursor(null);
        internalCursorAdapter = null;
    }

    class cursorPropAccessor extends Property<Object, Object>
    {
        private int index;

        public cursorPropAccessor(Class<Object> type, int index)
        {
            super(type, "notUsed");
            this.index = index;
        }

        @Override
        public void set(Object object, Object value)
        {
            //not used
        }

        @Override
        public Object get(Object notUsed)
        {
            if (internalCursor == null)
                return null;

            Class<Object> c = cursorPropAccessor.this.getType();
            if (c.equals(float.class))
                return internalCursor.getFloat(index);
            else if (c.equals(double.class))
                return internalCursor.getDouble(index);
            else if (c.equals(int.class))
                return internalCursor.getInt(index);
            else if (c.equals(long.class))
                return internalCursor.getLong(index);
            else if (c.equals(short.class))
                return internalCursor.getShort(index);
            else if (c.equals(String.class))
                return internalCursor.getString(index);
            else if (c.equals(Void.class))
                return null;
            else
                return internalCursor.getBlob(index);
        }
    };

    class calculatedPropertyAccessor extends Property<Object, Object>
    {
        public calculatedPropertyAccessor(Class<?> type, String name)
        {
            super((Class<Object>)type, name);
        }

        @Override
        public Object get(Object notUsed)
        {
            if (calculatedPropertiesHandler == null)
                return null;
            return calculatedPropertiesHandler.getCalculatedProperty(this.getName(), internalCursor);
        }

        @Override
        public void set(Object object, Object value)
        {
            //not used
        }
    }

    class internalCursorWrapper extends CursorWrapper
        implements IPropertyStore
    {
        private SparseArray<Property<Object, Object>> store = new SparseArray<Property<Object, Object>>();
        private HashMap<String, Property<Object, Object>> calculatedStore = new HashMap<String, Property<Object, Object>>();

        @Override
        public Property<?, ?> getProperty(Class<?> hostClass, String name)
        {
            if (internalCursor == null || internalCursor.isClosed())
                return null;

            int index = internalCursor.getColumnIndex(name);

            if (index < 0 && calculatedPropertiesHandler != null)
            {
                if (!calculatedStore.containsKey(name))
                {
                    calculatedStore.put(name, new calculatedPropertyAccessor(calculatedPropertiesHandler.getCalculatedPropertyType(name, internalCursor), name));
                }
                return calculatedStore.get(name);
            }
            else if (index < 0)
                return null;

            Class<?> type = null;

            switch (internalCursor.getType(index))
            {
                case Cursor.FIELD_TYPE_INTEGER:
                    type = int.class;break;
                case Cursor.FIELD_TYPE_STRING:
                    type = String.class;break;
                case Cursor.FIELD_TYPE_FLOAT:
                    type = float.class;break;
                case Cursor.FIELD_TYPE_NULL:
                    type = Void.class;break;
                case Cursor.FIELD_TYPE_BLOB:
                    type = (byte[].class);break;
            }

            if (store.get(index) == null)
                store.put(index, new cursorPropAccessor((Class<Object>)type, index));
            else if (!store.get(index).getType().equals(type))//takes care of null columns
            {
                store.delete(index);
                store.put(index, new cursorPropAccessor((Class<Object>)type, index));
            }
            return store.get(index);
        }

        public internalCursorWrapper(Cursor cursor)
        {
            super(cursor);
        }
    }

}
