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

package traction.mvc.observables;

import traction.mvc.implementations.CommandArgument;

/**
 * GenericArgument - used with GenericEvents, represents the arguments passed to whatever proxy listener was generated.
 * 
 * @author Tim Stratton
 *
 */
public class GenericArgument extends CommandArgument
{
	public GenericArgument(String propertyName, String name, Object[] args)
	{
        super(propertyName);
		this.name = name;
		this.arguments = args;
	}
		
	public final Object[] arguments;
	public final String name;
	private Object returnObj;	
	
	public Object getReturnObj()
	{
		return returnObj;
	}
	
	public void setReturnObject(Object obj)
	{
		returnObj = obj;
	}
}
