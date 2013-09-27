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

package amvvm.implementations.ui;

import android.content.res.TypedArray;

import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IProxyViewBinding;
import amvvm.interfaces.IViewBinding;
import amvvm.implementations.BindingInventory;
import amvvm.interfaces.IUIElement;

/**
 * Defines the UI end point for model/view-model data. Along with the IUIUpdateListener, the UIProperty can receive data updates
 * and transform them in a usable way that a view can consume. Updates from the view can be given to the UIProperty that can be
 * sent back to the model/view-model.
 * 
 * @author Tim Stratton
 *
 * @param <T> : data type of the property the element is bounded to
 */
public class UIProperty<T>
implements IUIElement<T>
{				
	protected String path;
    protected T tempValue;

	private IUIElement.IUIUpdateListener<T> updateListener;		
	
	private boolean _isUpdating;
	
	private int pathAttribute = -1;
	private final IViewBinding parentViewBinding;

    @SuppressWarnings("unused")
    private UIProperty(){parentViewBinding = null;}

	public UIProperty(IProxyViewBinding viewBinding, int pathAttribute)
	{
        this.parentViewBinding = viewBinding.getProxyViewBinding();
		this.pathAttribute = pathAttribute;
	}
	
	public UIProperty(IProxyViewBinding viewBinding)
	{
        this.parentViewBinding = viewBinding.getProxyViewBinding();
	}
	
	/**
	 * @return : true when element is currently sending an update back to the model or view-model
	 */
	public boolean isUpdating()
	{
		return _isUpdating;
	}

	/**
	 * Initiates the BindingInventory to send data to the model/view-model
	 */
	public void sendUpdate(T value)
	{	
		if (path == null)
			return;
		
		disableReceiveUpdates();
        getBindingInventory().sendUpdateFromUIElement(this, value);
		enableReceiveUpdates();
	}

    @Override
    public T dereferenceValue()
    {
        return (T) getBindingInventory().dereferenceValue(getPath());
    }

    /**
	 * Gets the full path the ui element is bounded to
	 */
	@Override
	public String getPath()
	{
		return path;
	}

    @Override
    public T getTempValue()
    {
        return tempValue;
    }

    @Override
    public void setTempValue(T value)
    {
        this.tempValue = value;
    }

    @Override
    public String getPropertyName()
    {
        if (getPath() == null)
            return null;
        if (getPath().equals("."))
            return null;

        int index = getPath().lastIndexOf(".");
        return getPath().substring(index+1);
    }

    @Override
    public void setPath(String path)
    {
        this.path = path;
    }

    @Override
	public void setUIUpdateListener(IUIElement.IUIUpdateListener<T> listener)
	{
		this.updateListener = listener;
	}

    @Override
    public void receiveUpdate(final Object value)
    {
        if (updateListener == null)
            return;

        synchronized(this)
        {
            //is true is 'disableReceiveUpdates' has been called before 'enablRecieveUpdates'
            //This is also the case if UIProperty has called the 'sendUpdate' method
            if (isUpdating())
                return;

            //if no handler, then just run on current thread
            if (getUIHandler() == null)
            {
                updateListener.onUpdate((T)value);
            }
            else
            {
                //call the update listener on the UI thread. Needs to be on the UI thread because it is most
                //certainly updating something on the UI
                getUIHandler().tryPostImmediatelyToUIThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateListener.onUpdate((T) value);
                    }
                });
            }
        }
    }

	@Override
	public void initialize(IAttributeGroup attributeGroup)
	{
		if (pathAttribute >= 0 && attributeGroup != null)
        {
			String tempPath = attributeGroup.getString(pathAttribute);
            if (parentViewBinding != null && parentViewBinding.getPathPrefix() == null)
                path = tempPath;
            else if (parentViewBinding != null && tempPath != null)
                path = parentViewBinding.getPathPrefix() + "." + tempPath;
            else
                path = tempPath;
        }
        getBindingInventory().track(this);
	}

    @Override
    public void disableReceiveUpdates()
    {
        synchronized(this)
        {
            _isUpdating = true;
        }
    }

    @Override
    public void enableReceiveUpdates()
    {
        synchronized(this)
        {
            _isUpdating = false;
        }
    }

    @Override
	public BindingInventory getBindingInventory()
	{
		return parentViewBinding.getBindingInventory();
	}

    protected UIHandler getUIHandler()
    {
        return parentViewBinding.getUIHandler();
    }
}
