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

import ni3po42.android.amvvm.implementations.observables.PropertyStore;
import android.util.Property;


/**
 * IObservableObject - defines the methods needed to handle sending and receiving events and updates to other object.
 * @author Tim Stratton
 *
 */
public interface IObservableObject
extends IObjectListener
{	
	/**
	 * Allows IObservableObject a cache of properties.
	 * @return
	 */
	PropertyStore getPropertyStore();
	
	/**
	 * Access property from property store.
	 * @param name
	 * @return if property exists, return property object, or null if not found
	 */
	Property<Object,Object> getProperty(String name);
	
	/**
	 * Allows implementors to hijack 'this'. Implementations should not use 'this' and should instead use 'getSource()',
	 * so non-IObservableObject objects can implement IObservableObject and wrap an existing IObservableObject to reuse the code. 
	 * @return
	 */
	IObservableObject getSource();
		
	/**
	 * Hard wire a property's listener to it's host object. Primarily used when not using a get/set method and instead using a final field.
	 * @param propertyName
	 * @param parentObj
	 * @return
	 */
	<T extends IObservableObject> T registerAs(String propertyName, IObservableObject parentObj);
	
	/**
	 * notifies any object listening that the whole object is claiming an update, not just a particular property
	 */
	void notifyListener();
	
	/**
	 * notifies any object listening that a single property has updated. user this call for non-IObservableObjects.
	 * If you need to update a property that is a IObservableObject, use notifyListener(String,IObservableObject, IObservable) 
	 * @param propertyName
	 */
	void notifyListener(String propertyName);
	
	/**
	 * notifies any object listening that a single property of type IObservableObject. This handles sending the signal and re-wiring
	 * listeners.
	 * @param propertyName
	 * @param oldPropertyValue
	 * @param newPropertyValue
	 */
	void notifyListener(String propertyName, IObservableObject oldPropertyValue, IObservableObject newPropertyValue);
		
	
	/**
	 * Wires up a property to 'react' to changes in another property at the same level or lower in the object hierarchy.
	 * So, if addReaction('localProperty','anotherLocalProperty.somethingFurtherDown') is called, when a change to 'somethingFurtherDown'
	 * occurs, 'localProperty' will send a notifyListener call. 
	 * 
	 * @param localProperty : a property name to the current object
	 * @param reactsTo : a property chain to 'watch' changes to.
	 */
	void addReaction(String localProperty, String reactsTo);
	
	/**
	 * Clears all set reactions
	 */
	void clearReactions();
	
	/**
	 * Registers a listener to the IObservable
	 * @param sourceName : name of source property to track changes and bubble up to inventory 
	 * @param obj : obj to track
	 * @return : 'this' typed as <T>
	 */
	<T extends IObservableObject> T registerListener(String sourceName, IObjectListener listener);
	
	/**
	 * Remove registration from IObservable. Once called, OnObservableUpdated should no longer signal the listener
	 * @param listener : listener to unregister from IObservable
	 */
	void unregisterListener(String sourceName, IObjectListener listener);
}
