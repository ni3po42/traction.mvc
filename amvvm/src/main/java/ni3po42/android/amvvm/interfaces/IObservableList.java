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

import java.util.List;


/**
 * Defines a list that is observable
 * @author Tim Stratton
 * @param <T> : type of item the list is composed of
 */
public interface IObservableList<T>
extends IObservableObject, List<T>
{	
	/**
	 * A read-only property 'Count'. Should return the number of elements in the list
	 * @return size of list
	 */
	public int getCount();
}
