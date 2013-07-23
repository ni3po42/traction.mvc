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

import ni3po42.android.amvvm.viewmodels.ViewModel;

/**
 * Defines basic interface of a view model
 * @author Tim Stratton
 *
 */
public interface IViewModel
extends IAccessibleFragmentManager, IDefaultActivityService, IProxyObservableObject
{
	/**
	 * lets view model set which view to use
	 * @param layoutResID
	 */
	void setContentView(int layoutResID);

	/**
	 * lets view model set which menu to load
	 * @param id : menu layout
	 */
	void setMenuLayout(int id);
		
	/**
	 * Convenience method for retrieving a view model this view model maintains 
	 * @param memberName : property name A(or tag) for view model
	 * @return
	 */
	ViewModel getViewModel(String memberName);
	
	/**
	 * Allows the setting of a view model in this view model
	 * @param memberName
	 * @param viewModel
	 */
	void setViewModel(String memberName, ViewModel viewModel);
	
}
