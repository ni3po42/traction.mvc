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
 * An absttaction of a Command
 * @author Tim Stratton
 * @param <TArg> : Type of command argument to pass when commands are executed
 */
public interface ICommand<TArg> 
{		
	/**
	 * call to execute command
	 * @param arg
	 */
	void execute(TArg arg);
	
	/**
	 * 
	 * @return : true if command can execute
	 */
	boolean getCanExecute();
	
	/**
	 * set whether command can execute
	 * @param b
	 */
	void setCanExecute(boolean b);
}
