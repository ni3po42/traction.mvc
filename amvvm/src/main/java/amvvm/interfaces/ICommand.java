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


import org.json.JSONObject;

/**
 * An absttaction of a Command
 * @author Tim Stratton
 * @param <TArg> : Type of command argument to pass when commands are executed
 */
public interface ICommand<TArg extends ICommand.CommandArgument>
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

    /**
     * class for passing arguments to commands
     */
    public class CommandArgument
    {
        private String commandName;
        private boolean eventCancelled;
        private JSONObject tagProperties;

        public boolean isEventCancelled()
        {
            return eventCancelled;
        }

        public void setEventCancelled(boolean isCancelled)
        {
            eventCancelled = isCancelled;
        }

        public CommandArgument(String commandName)
        {
            this(commandName, null);
        }

        public CommandArgument(String commandName, JSONObject tagProperties)
        {
            this.commandName = commandName;
            this.eventCancelled = false;
            this.tagProperties = tagProperties;
        }

        public JSONObject getEventData()
        {
            return tagProperties;
        }

        public String getCommandName()
        {
            return commandName;
        }
    }

}
