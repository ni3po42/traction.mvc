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

package amvvm.interfaces;

/**
 * Allows non-IObservableObject to be handled like IObservableObject by composition instead of inheritance/interface
 * Objects can implement this interface and expose a composed observableObject to handle event delegations
 * @author Tim Stratton
 *
 */
public interface IProxyObservableObject
{
	/**
	 * Allow access to the composed IObservableObject
	 * @return
	 */
	IObservableObject getProxyObservableObject();
	
	/**
	 * Returns source object that is being extended with the IObservableObject properties
	 * @return
	 */
	Object getSource();
	
}
