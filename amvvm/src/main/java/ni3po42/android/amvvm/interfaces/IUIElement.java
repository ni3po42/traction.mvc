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

package ni3po42.android.amvvm.interfaces;

import android.content.res.TypedArray;
import ni3po42.android.amvvm.implementations.BindingInventory;
import ni3po42.android.amvvm.implementations.ui.UIHandler;

/**
 * Defines interface for a ui element link. It defines the object path to a specific linked piece of data
 * @author Tim Stratton
 *
 * @param <T> : type of data to link from the UI to a model/view-model
 */
public interface IUIElement<T>
{
	/**
	 * property path to model/view-model data
	 * @return
	 */
	public String getPath();
	
	/**
	 * sets a handler for when the ui recieves data from the model/view-model
	 * @param listener
	 */
	public void setUIUpdateListener(IUIUpdateListener<T> listener);

	/**
	 * Sends data to the ui element. Expects the IUIUpdateListener to be called 
	 * @param value
	 */
	public void recieveUpdate(final Object value);
	
	/**
	 * Sends data from ui back to model/view-model
	 * @param value
	 */
	public void sendUpdate(T value);
	
	/**
	 * Initializes data for ui element during inflation
	 * @param styledAttributes
	 * @param inventory
	 * @param uiHandler : handler giving specific access to ui thread
	 */
	public void initialize(TypedArray styledAttributes, BindingInventory inventory, UIHandler uiHandler);
	
	/**
	 * turns off ability to receive updates from model/view-model
	 */
	public void disableRecieveUpdates();
	
	/**
	 * turns on ability to receive updates from model/view-model
	 */
	public void enableRecieveUpdates();
	
	/**
	 * gets the binding inventory the ui element is associated to
	 * @return
	 */
	public BindingInventory getBindingInventory();
	
	/**
	 * listener to handle updates to the ui element
	 * @author Tim Stratton
	 *
	 * @param <S> : type of data sent to ui element
	 */
	public interface IUIUpdateListener<S>
	{
		public void onUpdate(S value);
	}
}
