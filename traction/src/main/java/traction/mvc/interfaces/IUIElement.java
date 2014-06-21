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

package traction.mvc.interfaces;


import traction.mvc.implementations.BindingInventory;

/**
 * Defines interface for a ui element link. It defines the object path to a specific linked piece of data
 * @author Tim Stratton
 *
 * @param <T> : type of data to link from the UI to a model/view-model
 */
public interface IUIElement<T>
{
	/**
	 * sets a handler for when the ui recieves data from the model/view-model
	 * @param listener
	 */
	public void setUIUpdateListener(IUIUpdateListener<T> listener);

	/**
	 * Sends data to the ui element. Expects the IUIUpdateListener to be called 
	 * @param value
	 */
	public void receiveUpdate(final Object value);
	
	/**
	 * Sends data from ui back to model/view-model
	 * @param value
	 */
	public void sendUpdate(T value);

	/**
	 * Initializes data for ui element during inflation
	 */
	public void initialize() throws Exception;

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

    public boolean isDefined();

    public void track(BindingInventory differentBindingInventory);
}
