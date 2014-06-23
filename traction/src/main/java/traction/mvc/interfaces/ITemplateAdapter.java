package traction.mvc.interfaces;

import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

import traction.mvc.observables.BindingInventory;

public interface ITemplateAdapter
    extends ListAdapter, SpinnerAdapter
{
    void setParentInventory(BindingInventory parentInventory);
    void setLayoutId(int layoutId);
    int indexOf(Object obj);
}
