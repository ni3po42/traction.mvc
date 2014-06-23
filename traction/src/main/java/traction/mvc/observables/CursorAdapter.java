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

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import traction.mvc.implementations.BindingInventory;
import traction.mvc.implementations.ViewBindingFactory;
import traction.mvc.implementations.ViewFactory;
import traction.mvc.interfaces.IPOJO;
import traction.mvc.interfaces.ITemplateAdapter;

public class CursorAdapter
    extends android.widget.CursorAdapter
    implements ITemplateAdapter
{
    public <T extends IPOJO & Cursor> CursorAdapter(Context context, T cursor)
    {
        super(context, cursor, android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    public boolean isCursorAdapterValid()
    {
        return this.templateId > 0 && this.parentInventory != null;
    }

    private BindingInventory parentInventory;
    private ViewBindingFactory factory = new ViewBindingFactory();
    private LayoutInflater inflater;
    private int templateId;


    @Override
    public void setParentInventory(BindingInventory parentInventory) {
        this.parentInventory = parentInventory;
    }

    @Override
    public void setLayoutId(int layoutId) {
        this.templateId = layoutId;
    }

    @Override
    public int indexOf(Object obj) {
        return getCursor().getPosition();
    }

    protected LayoutInflater getLayoutInflater(View parentView)
    {
        if (inflater == null)
            inflater = LayoutInflater.from(parentView.getContext());
        return inflater;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        ViewFactory.updateScope(view, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        //the adapter is expecting the parent to have a view holder. it should for the most part..
        //except the weird Spinner. There are at least two different parent ViewGroups that can
        //show up; the spinner and it's popup window. In the case the popup window (or some other
        //viewgroup) comes up, this will synthetically add a viewHolder with the proper objects
        //and register the list as the root context.

        if (ViewFactory.getViewBinding(parent) == null)
        {
            factory.createSyntheticFor(parent,null, parentInventory);
        }

        View view = getLayoutInflater(parent).inflate(templateId, parent ,false);

        if (ViewFactory.getViewBinding(parent).equals(ViewFactory.getViewBinding(view)))
        {
            throw new IllegalStateException("Possible child view not marked 'IsRoot'. Templates of AdapterViews must be marked 'IsRoot' = true.");
        }

        //now to clean up. If a synthetic viewholder was created, detach the list from the 'root'
        if (ViewFactory.getViewBinding(parent).isSynthetic())
            ViewFactory.removeViewBinding(parent);

        return view;
    }
}
