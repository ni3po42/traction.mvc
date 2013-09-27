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

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.util.Property;
import android.util.SparseArray;
import android.widget.BaseAdapter;

import amvvm.implementations.ui.viewbinding.CursorAdapter;
import amvvm.implementations.ui.viewbinding.ProxyAdapter;
import amvvm.interfaces.ICursorExtension;
import amvvm.interfaces.IObservableCursor;
import amvvm.interfaces.IPropertyStore;

/**
 * Expose a cursor that is observable, duh.
 */
public class ObservableCursor
    extends ProxyAdapter<Cursor>
    implements IObservableCursor,LoaderManager.LoaderCallbacks<Cursor>
{
    private ICursorExtension cursorExtension;
    private CursorAdapter internalCursorAdapter;
    private ICursorLoader cursorLoader;

    public void setCursorExtension(ICursorExtension extension)
    {
        this.cursorExtension = extension;
    }

    public <T extends ObservableCursor> T setCursorLoader(ICursorLoader cursorLoader)
    {
        this.cursorLoader = cursorLoader;
        return (T)this;
    }

    public Cursor getCursor()
    {
        if (internalCursorAdapter != null && internalCursorAdapter.isCursorAdapterValid())
            return internalCursorAdapter.getCursor();
        return null;
    }

    @Override
    protected int indexOf(Cursor value)
    {
        if (value == null) return -1;
        return value.getPosition();
    }

    @Override
    protected BaseAdapter getProxyAdapter(ProxyAdapterArgument arg)
    {
        if (internalCursorAdapter != null && !internalCursorAdapter.isCursorAdapterValid())
        {
            internalCursorAdapter.updateArguments(arg);
        }
        return internalCursorAdapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        if (cursorLoader == null)
            return null;
        return cursorLoader.onCreateLoader(bundle);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        internalCursorWrapper wrappedCursor = new internalCursorWrapper(cursor);
        if (internalCursorAdapter == null)
        {
            ProxyAdapterArgument arg = new ProxyAdapterArgument()
                                        .setContext(cursorLoader.getContext());

            internalCursorAdapter = new CursorAdapter(arg, wrappedCursor);
            notifyListener();
        }
        else
            if (internalCursorAdapter.swapCursor(wrappedCursor) != null)
            {
                notifyListener();
            }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        if (internalCursorAdapter != null)
            internalCursorAdapter.swapCursor(null);
    }

    @Override
    public IPropertyStore getPropertyStore()
    {
        if (internalCursorAdapter == null || !(internalCursorAdapter.getCursor() instanceof internalCursorWrapper))
            return null;
        return (internalCursorWrapper)internalCursorAdapter.getCursor();
    }

    class cursorPropAccessor extends Property<Object, Object>
    {
        private int index;

        public cursorPropAccessor(Class<Object> type, int index)
        {
            super(type, "notUsed");
            this.index = index;
        }

        public cursorPropAccessor(String name)
        {
            super(Object.class, name);
            this.index = -1;
        }

        @Override
        public void set(Object obj, Object value)
        {
           if (!(obj instanceof Cursor))
               return;

            Cursor c = (Cursor)obj;

            if (index < 0 && cursorExtension != null)
            {
                cursorExtension.setCursorExtendedProperty(c, getName(), value);
            }
        }

        @Override
        public Object get(Object obj)
        {
            if (!(obj instanceof Cursor))
                return null;

            Cursor internalCursor = (Cursor)obj;

            if (index == -1)
            {
                if (cursorExtension == null)
                    return null;
                return cursorExtension.getCursorExtendedProperty(internalCursor, getName());
            }

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

    class internalCursorWrapper extends CursorWrapper
        implements IPropertyStore
    {
        private SparseArray<Property<Object, Object>> store = new SparseArray<Property<Object, Object>>();

        @Override
        public Property<?, ?> getProperty(Class<?> hostClass, String name)
        {
            if (internalCursorAdapter == null || internalCursorAdapter.getCursor() == null || internalCursorAdapter.getCursor().isClosed())
                return null;

            Cursor c = internalCursorAdapter.getCursor();
            int index = c.getColumnIndex(name);

            if (index < 0)
            {
               return new cursorPropAccessor(name);
            }

            Class<?> type = null;

            switch (c.getType(index))
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
