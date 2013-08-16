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
	private IUIElement.IUIUpdateListener<T> updateListener;		
	
	private boolean _isUpdating;
	
	private int pathAttribute;
	private IViewBinding parentViewBinding;

	public UIProperty(IViewBinding viewBinding, int pathAttribute)
	{
		this.pathAttribute = pathAttribute;
        this.parentViewBinding = viewBinding;
	}
	
	public UIProperty(IViewBinding viewBinding)
	{
		this.pathAttribute = -1;
        this.parentViewBinding = viewBinding;
	}
	
	/**
	 * @return : true when element is currently sending an update back to the model or view-model
	 */
	public boolean isUpdating()
	{
		return _isUpdating;
	}
	
	@SuppressWarnings("unused")
	private UIProperty(){}
		
	/**
	 * When called, updates view with passed data.
	 */
	@SuppressWarnings("unchecked")
	public void recieveUpdate(final Object value)
	{
		if (updateListener == null)
			return;
		
		synchronized(this)
		{	
			//is true is 'disableRecieveUpdates' has been called before 'enablRecieveUpdates'
			//This is also the case if UIProperty has called the 'sendUpdate' method
			if (isUpdating())
				return;
			
			//if no handler, then just run on current thread
			if (parentViewBinding.getUIHandler() == null)
			{
				updateListener.onUpdate((T)value);
			}
			else
			{
				//call the update listener on the UI thread. Needs to be on the UI thread because it is most
				//certainly updating something on the UI
                parentViewBinding.getUIHandler().tryPostImmediatelyToUIThread(new Runnable()
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
	
	/**
	 * Initiates the BindingInventory to send data to the model/view-model
	 */
	public void sendUpdate(T value)
	{	
		if (path == null)
			return;
		
		disableRecieveUpdates();
        parentViewBinding.getBindingInventory().sendUpdateFromUIElement(this, value);
		enableRecieveUpdates();		
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
	
	/**
	 * disables the ui element from receiving data from the model/view-model
	 */
	@Override
	public void disableRecieveUpdates()
	{
		synchronized(this)
		{	
			_isUpdating = true;
		}
	}
	
	/**
	 * re-enables the ui element to receive data from the model/view-model
	 */
	@Override
	public void enableRecieveUpdates()
	{
		synchronized(this)
		{	
			_isUpdating = false;
		}
	}

	@Override
	public void initialize(IAttributeGroup attributeGroup)
	{
		if (pathAttribute >= 0 && attributeGroup != null)
			path = attributeGroup.getString(pathAttribute);
        parentViewBinding.getBindingInventory().track(this);
	}

	@Override
	public BindingInventory getBindingInventory()
	{
		return parentViewBinding.getBindingInventory();
	}	
}
