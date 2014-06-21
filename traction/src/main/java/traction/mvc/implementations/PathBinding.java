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

package traction.mvc.implementations;

import java.util.ArrayList;

import traction.mvc.interfaces.IUIElement;

/**
 * Object to hold list of ui elements for the binding inventory
 * @author Tim Stratton
 *
 */
public class PathBinding
{
	private final ArrayList<IUIElement<?>> uiElements = new ArrayList<IUIElement<?>>();

	/**
	 * Adds a ui element to the Path biding
	 * @param element
	 */
	public void addUIElement(IUIElement<?> element)
	{
		uiElements.add(element);
	}	
	
	/**
	 * Gets all ui elements tracked to this path so far
	 * @return
	 */
	public ArrayList<IUIElement<?>> getUIElements()
	{
		return uiElements;
	}
}
