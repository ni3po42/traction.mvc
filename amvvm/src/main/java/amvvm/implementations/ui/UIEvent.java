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

package amvvm.implementations.ui;

import amvvm.interfaces.ICommand;
import amvvm.interfaces.IViewBinding;

/**
 * Defines the UI end for an event. This can be user interaction with the UI or a system initiated event the 
 * element is listening to
 * @author Tim Stratton
 *
 * @param <TArg> : type of argument the ui event can pass a command
 */
public class UIEvent<TArg extends ICommand.CommandArgument>
extends UIProperty<Object>
{	
	public UIEvent(IViewBinding viewBinding, int pathAttribute)
	{
		super(viewBinding, pathAttribute);
	}

	/**
	 * Lets a UIEvent signal a bounded command
	 * @param arg
	 */
	public boolean execute(TArg arg)
	{
        ICommand.CommandArgument baseArg = arg;
        if (baseArg == null)
            baseArg = new ICommand.CommandArgument(getPropertyName());

		getBindingInventory().fireCommand(getPath(), baseArg);

        return !baseArg.isEventCancelled();
	}			
}
