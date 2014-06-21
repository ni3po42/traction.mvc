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

import android.content.Loader;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Property;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import traction.mvc.implementations.BindingInventory;
import traction.mvc.interfaces.IObservableCursor;
import traction.mvc.interfaces.IPOJO;
import traction.mvc.interfaces.IPropertyStore;

/**
 * Expose a cursor that is observable, duh.
 */
public class ObservableCursor
    extends ObservableObject
    implements IObservableCursor
{
    private CursorAdapter internalCursorAdapter;
    private Loader<Cursor> cursorLoader;

    protected ArrayList<DataSetObserver> observers = new ArrayList<DataSetObserver>();

    private BindingInventory parentInventory;
    private int templateId;

    public void setLoader(Loader<Cursor> cursorLoader)
    {
        this.cursorLoader = cursorLoader;
    }

    public Cursor getCursor()
    {
        if (internalCursorAdapter != null && internalCursorAdapter.isCursorAdapterValid())
            return internalCursorAdapter.getCursor();
        return null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        internalCursorWrapper wrappedCursor = new internalCursorWrapper(cursor);
        if (internalCursorAdapter == null)
        {
            internalCursorAdapter = new CursorAdapter(cursorLoader.getContext(), wrappedCursor);

            for(int i=0;i<observers.size();i++)
            {
                internalCursorAdapter.registerDataSetObserver(observers.get(i));
            }
            observers.clear();
            internalCursorAdapter.setLayoutId(this.templateId);
            internalCursorAdapter.setParentInventory(parentInventory);
            notifyListener();
        }
        else
            if (internalCursorAdapter.swapCursor(wrappedCursor) != null)
            {
                internalCursorAdapter.setLayoutId(this.templateId);
                internalCursorAdapter.setParentInventory(parentInventory);
                internalCursorAdapter.notifyDataSetChanged();
            }
    }


    @Override
    public void setParentInventory(BindingInventory parentInventory) {
        this.parentInventory = parentInventory;
        if (internalCursorAdapter != null)
            internalCursorAdapter.setParentInventory(parentInventory);
    }

    @Override
    public void setLayoutId(int layoutId) {
        this.templateId = layoutId;
        if (internalCursorAdapter != null)
            internalCursorAdapter.setLayoutId(layoutId);
    }

    @Override
    public int indexOf(Object obj) {
        if (internalCursorAdapter !=null)
            return internalCursorAdapter.getCursor().getPosition();
        return -1;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        if (internalCursorAdapter != null)
            internalCursorAdapter.swapCursor(null);
    }

//    @Override
//    public IPropertyStore getPropertyStore()
//    {
//        if (internalCursorAdapter == null || !(internalCursorAdapter.getCursor() instanceof internalCursorWrapper))
//            return null;
//        return (internalCursorWrapper)internalCursorAdapter.getCursor();
//    }

    @Override
    protected IPropertyStore getPropertyStore()
    {
       if (internalCursorAdapter == null || !(internalCursorAdapter.getCursor() instanceof internalCursorWrapper))
            return null;
        return (internalCursorWrapper)internalCursorAdapter.getCursor();
    }

    ///////////////////////////////////////////

    @Override
    public boolean areAllItemsEnabled() {
        if (internalCursorAdapter != null)
            return internalCursorAdapter.areAllItemsEnabled();
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        if (internalCursorAdapter != null)
            return internalCursorAdapter.isEnabled(i);
        return false;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if (internalCursorAdapter != null)
            return internalCursorAdapter.getDropDownView(i,view,viewGroup);
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        if (internalCursorAdapter != null)
            internalCursorAdapter.registerDataSetObserver(dataSetObserver);
        else
        {
            observers.add(dataSetObserver);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        if (internalCursorAdapter != null)
            internalCursorAdapter.unregisterDataSetObserver(dataSetObserver);
        {
            observers.remove(dataSetObserver);
        }
    }

    @Override
    public int getCount() {
        if (internalCursorAdapter != null)
            return internalCursorAdapter.getCount();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (internalCursorAdapter != null)
            return internalCursorAdapter.getItem(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        if (internalCursorAdapter != null)
            return internalCursorAdapter.getItemId(i);
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        if (internalCursorAdapter != null)
            return internalCursorAdapter.hasStableIds();
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (internalCursorAdapter != null)
            return internalCursorAdapter.getView(i,view, viewGroup);
        return null;
    }

    @Override
    public int getItemViewType(int i) {
        if (internalCursorAdapter != null)
            return internalCursorAdapter.getItemViewType(i);
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        if (internalCursorAdapter != null)
            internalCursorAdapter.getViewTypeCount();
        return 1;
    }

    @Override
    public boolean isEmpty() {
        if (internalCursorAdapter != null)
            internalCursorAdapter.isEmpty();
        return true;
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

//            if (index < 0 && extension != null)
            {
                //Property<?,?> prop = PropertyStore.find(extension.getClass(), getName());
                //prop.set(extension, value);
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
                //if (cursorExtension == null)
                //    return null;
                //return cursorExtension.getCursorExtendedProperty(internalCursor, getName());
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
        implements IPOJO, IPropertyStore
    {
        private SparseArray<Property<Object, Object>> store = new SparseArray<Property<Object, Object>>();

        public internalCursorWrapper(Cursor cursor)
        {
            super(cursor);
        }

        @Override
        public Property<Object, Object> getProperty(String name)
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
    }

}
