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

/**
 * The base Activity will need to be overridden and calls to getSystemService must be hijacked to return the new layout inflater.
 * To keep this logic out of the base Activity and in the ViewModelHelper, access to the default getSystemService is still required.
 * This allows object to define seperate the default calls from the overriden calls.
 * @author Tim Stratton
 *
 */
public interface IDefaultActivityService
{
	/**
	 * Access to 'super' activities' getSystemService
	 */
	Object getDefaultActivityService(String name);
}
