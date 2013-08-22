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

package amvvm.implementations.observables;

import android.util.Property;

import amvvm.interfaces.ICommand;
import amvvm.interfaces.IObservableCommand;
import amvvm.interfaces.IPropertyStore;
import amvvm.interfaces.IProxyObservableObject;

/**
 * class representing a Command. It exposes a property that can be used to determine if
 * the command can execute and gives a handle to override to perform functions when the command has been invoked
 * @author Tim Stratton
 *
 * @param <TArg> : argument type for the command
 */
public class Command<TArg extends ICommand.CommandArgument>
extends ObservableObject
implements IObservableCommand<TArg>
{
    private IOnExecuteListener<TArg> onExecuteListener;

    public Command()
    {
        setCanExecute(true);
    }

    public Command(boolean initCanExecute)
    {
        setCanExecute(initCanExecute);
    }

    public interface IOnExecuteListener<PArg>
    {
        void onExecuted(PArg arg);
    }

    public Command<TArg> setOnExecuteListener(IOnExecuteListener<TArg> listener)
    {
        onExecuteListener = listener;
        return this;
    }

	private boolean canExecute;
	
	@Override
	public IPropertyStore getPropertyStore()
	{
		//getProperty was overridden, this is not used.
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Property<Object, Object> getProperty(String name)
	{
		Class<?> c = boolean.class;
		//The only supported property on a command will be 'CanExecute', for now...
		if (name.equals("CanExecute") && getSource() != null)
			return (Property<Object, Object>) Property.of(getSource().getClass(), c, name);
		return null;
	}

	@Override
	public boolean getCanExecute()
	{
		return canExecute;
	}
	
	@Override
	public void setCanExecute(boolean b)
	{
		if (canExecute == b)
			return;
		canExecute = b;
		notifyListener("CanExecute");
	}

	/**
	 * Is fired when execute(TArg) is called and CanExecute() is true
	 * @param arg : argument passed to execute method. Could very well be null.
	 */
	protected void onExecuted(TArg arg)
    {
        if (onExecuteListener != null)
            onExecuteListener.onExecuted(arg);
    }

	@Override
	public void execute(TArg arg)
	{
		if (!getCanExecute())
			return;

		onExecuted(arg);
	}

}
