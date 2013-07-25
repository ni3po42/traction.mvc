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

package ni3po42.android.amvvm.implementations.ui;

import android.content.res.TypedArray;
import ni3po42.android.amvvm.implementations.BindingInventory;
import ni3po42.android.amvvm.interfaces.IUIElement;
import ni3po42.android.amvvm.interfaces.IViewBinding;

/**
 * Defines the UI end point for model/view-model data. Along with the IUIUpdateListener, the UIBindedProperty can receive data updates
 * and transform them in a usable way that a view can consume. Updates from the view can be given to the UIBindedProperty that can be
 * sent back to the model/view-model.
 * 
 * @author Tim Stratton
 *
 * @param <T> : data type of the property the element is bounded to
 */
public class UIBindedProperty<T>
implements IUIElement<T>
{				
	protected String path;
	private IUIElement.IUIUpdateListener<T> updateListener;		
	
	private boolean _isUpdating;
	
	private int pathAttribute;
	protected UIHandler uiHandler;
	protected BindingInventory inventory;
	
	public UIBindedProperty(IViewBinding viewBinding, int pathAttribute)
	{
		this.pathAttribute = pathAttribute;
	}
	
	public UIBindedProperty(IViewBinding viewBinding)
	{
		this.pathAttribute = -1;
	}
	
	/**
	 * @return : true when element is currently sending an update back to the model or view-model
	 */
	public boolean isUpdating()
	{
		return _isUpdating;
	}
	
	@SuppressWarnings("unused")
	private UIBindedProperty(){}
		
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
			//This is also the case if UIBindedProperty has called the 'sendUpdate' method
			if (isUpdating())
				return;
			
			//if no handler, then just run on current thread
			if (uiHandler == null)
			{
				updateListener.onUpdate((T)value);
			}
			else
			{
				//call the update listener on the UI thread. Needs to be on the UI thread because it is most
				//certainly updating something on the UI
				uiHandler.tryPostImmediatelyToUIThread(new Runnable()
				{
					@Override
					public void run()
					{
						updateListener.onUpdate((T)value);
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
		inventory.sendUpdateFromUIElement(this, value);		
		enableRecieveUpdates();		
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
	public void initialize(TypedArray styledAttributes, BindingInventory inventory, UIHandler uiHandler)
	{
		this.uiHandler = uiHandler;
		this.inventory = inventory;
		if (pathAttribute >= 0 && styledAttributes != null)
			path = styledAttributes.getString(pathAttribute);
		inventory.track(this);
	}

	@Override
	public BindingInventory getBindingInventory()
	{
		return inventory;
	}	
}
