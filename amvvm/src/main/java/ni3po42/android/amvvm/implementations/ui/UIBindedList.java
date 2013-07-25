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

import ni3po42.android.amvvm.interfaces.IObservableList;
import ni3po42.android.amvvm.interfaces.IProxyObservableObject;
import ni3po42.android.amvvm.interfaces.IViewBinding;

/**
 * Defines the UI end for a list. It's pretty much just a UIBindingProperty that uses an IObservableList as it's item type.
 * @author Tim Stratton
 *
 * @param <T> : the item type of the list
 */
public class UIBindedList<T extends IProxyObservableObject> 
extends UIBindedProperty<IObservableList<T>>
{
	public UIBindedList(IViewBinding viewBinding, int pathAttribute)
	{
		super(viewBinding, pathAttribute);
	}	
	
}
