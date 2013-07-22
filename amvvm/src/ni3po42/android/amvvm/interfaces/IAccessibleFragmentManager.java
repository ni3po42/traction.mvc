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

import ni3po42.android.amvvm.implementations.BindingInventory;

/**
 * Access to the FragmentManager is required to tracking fragments in layout files and syncing them to properties.
 * This allows object to accept a BindingInvetory and callit's it's link fragments call and pass it's instance of FragmentManager
 * @author Tim Stratton
 *
 */
public interface IAccessibleFragmentManager
{
	public void linkFragments(BindingInventory inventory);	
}
