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

package ni3po42.android.amvvmdemo.models;

import amvvm.implementations.observables.ObservableObject;
import amvvm.implementations.observables.PropertyStore;
import amvvm.viewmodels.ViewModel;


public class DemoViewModelChoice extends ObservableObject
{
	private static PropertyStore store = new PropertyStore();
	private Class<? extends ViewModel> viewModelType;
	private String name;
	private String description;
	
	public DemoViewModelChoice(Class<? extends ViewModel> viewModelType, String name, String description)
	{
		this.viewModelType = viewModelType;
		this.name = name;
		this.description = description;
	}
	
	@Override
	public PropertyStore getPropertyStore()
	{
		return store;
	}

	public Class<? extends ViewModel> getViewModelType()
	{
		return viewModelType;
	}
	public void setViewModelType(Class<? extends ViewModel> viewModelType)
	{
		this.viewModelType = viewModelType;
		notifyListener("ViewModelType");
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
		notifyListener("Name");
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
		notifyListener("Description");
	}
	
}
