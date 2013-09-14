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

    private int bindingFlags = IViewBinding.Flags.NO_FLAGS;

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


    public boolean isSynthetic()
    {
        return synthetic;
    }

    public void markAsSynthetic(BindingInventory inventory)
    {
        synthetic = true;
        bindingFlags |= IViewBinding.Flags.IS_ROOT;
        setBindingInventory(inventory);
        uiHandler = new UIHandler();
    }

    public void setUiHandler(UIHandler uiHandler)
    {
        this.uiHandler = uiHandler;
    }

    public void setBindingInventory(BindingInventory bindingInventory)
    {
        this.bindingInventory = bindingInventory;
    }

    public int getBindingFlags() {
        return bindingFlags;
    }

    public void setBindingFlags(int bindingFlags) {
        this.bindingFlags = bindingFlags;
    }
}
