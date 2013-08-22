package amvvm.implementations.ui.viewbinding;

import android.view.View;

import amvvm.implementations.BindingInventory;
import amvvm.implementations.ViewBindingFactory;
import amvvm.implementations.ui.UIHandler;
import amvvm.interfaces.IViewBinding;

/**
 * Handles some simple tasks all viewbindings can expect to implement
 */
public class ViewBindingHelper
{
    private BindingInventory bindingInventory;
    private UIHandler uiHandler;

    private ViewBindingFactory factory = new ViewBindingFactory();

    private boolean root;
    private boolean ignoreChildren;
    private boolean synthetic;

    public IViewBinding createSyntheticFor(View view, String bindingType, BindingInventory inventory)
    {
        return factory.createSyntheticFor(view, bindingType, inventory);
    }

    public BindingInventory getBindingInventory()
    {
        return bindingInventory;
    }

    public UIHandler getUIHandler()
    {
        return uiHandler;
    }

    public boolean isRoot()
    {
        return this.root;
    }

    public void setRoot(boolean b)
    {
        this.root = b;
    }

    public boolean ignoreChildren()
    {
        return this.ignoreChildren;
    }

    public boolean isSynthetic()
    {
        return synthetic;
    }

    public void markAsSynthetic(BindingInventory inventory)
    {
        synthetic = true;
        root = true;
        setBindingInventory(inventory);
        uiHandler = new UIHandler();
    }

    public void setIgnoreChildren(boolean ignoreChildren)
    {
        this.ignoreChildren = ignoreChildren;
    }

    public void setUiHandler(UIHandler uiHandler)
    {
        this.uiHandler = uiHandler;
    }

    public void setBindingInventory(BindingInventory bindingInventory)
    {
        this.bindingInventory = bindingInventory;
    }
}
