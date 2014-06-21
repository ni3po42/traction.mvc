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

package traction.mvc.implementations.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import traction.mvc.implementations.BindingInventory;
import traction.mvc.implementations.CommandArgument;
import traction.mvc.interfaces.IProxyViewBinding;
import traction.mvc.interfaces.IUIElement;
import traction.mvc.interfaces.IViewBinding;

/**
 * Defines the UI end for an event. This can be user interaction with the UI or a system initiated event the 
 * element is listening to
 * @author Tim Stratton
 */
public class UIEvent
implements IUIElement<CommandArgument>
{
    protected String[] paths;

    private IUIElement.IUIUpdateListener<CommandArgument> updateListener;

    private boolean _isUpdating;

    protected String pathAttribute = null;
    protected final IViewBinding parentViewBinding;

    @SuppressWarnings("unused")
    private UIEvent(){parentViewBinding = null;}

    public UIEvent(IProxyViewBinding viewBinding, String pathAttribute)
    {
        this.parentViewBinding = viewBinding.getProxyViewBinding();
        this.pathAttribute = pathAttribute;
        this.parentViewBinding.registerUIElement(this);
    }

    /**
     * @return : true when element is currently sending an update back to the model or view-model
     */
    public boolean isUpdating()
    {
        return _isUpdating;
    }

    /**
     * Initiates the BindingInventory to send data to the model/view-model
     */
    public void sendUpdate(CommandArgument value)
    {
        if (paths == null)
            return;

        disableReceiveUpdates();
        for(int i=0;i< paths.length; i++)
            getBindingInventory().sendUpdate(paths[i], value);
        enableReceiveUpdates();
    }

    @Override
    public void setUIUpdateListener(IUIElement.IUIUpdateListener<CommandArgument> listener)
    {
        this.updateListener = listener;
    }

    @Override
    public void receiveUpdate(final Object value)
    {
        if (updateListener == null)
            return;

        synchronized(this)
        {
            //is true is 'disableReceiveUpdates' has been called before 'enablRecieveUpdates'
            //This is also the case if UIProperty has called the 'sendUpdate' method
            if (isUpdating())
                return;

            //if no handler, then just run on current thread
            if (getUIHandler() == null)
            {
                updateListener.onUpdate((CommandArgument)value);
            }
            else
            {
                //call the update listener on the UI thread. Needs to be on the UI thread because it is most
                //certainly updating something on the UI
                getUIHandler().tryPostImmediatelyToUIThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateListener.onUpdate((CommandArgument) value);
                    }
                });
            }
        }
    }

    @Override
    public void initialize() throws Exception
    {
        if (pathAttribute == null)
            return;

        JSONObject tagProperties =parentViewBinding.getTagProperties();
        JSONArray tempPaths =  tagProperties.has(pathAttribute) ? tagProperties.getJSONArray(pathAttribute) : null;

        if (tempPaths == null)
            return;

        this.paths = new String[tempPaths.length()];
        for(int i=0;i<paths.length;i++)
        {
            if (parentViewBinding.getPathPrefix() != null)
                this.paths[i] = parentViewBinding.getPathPrefix() + "." + tempPaths.getString(i);
            else
                this.paths[i] = tempPaths.getString(i);
            getBindingInventory().track(this, this.paths[i]);
        }
    }

    protected void disableReceiveUpdates()
    {
        synchronized(this)
        {
            _isUpdating = true;
        }
    }

    protected void enableReceiveUpdates()
    {
        synchronized(this)
        {
            _isUpdating = false;
        }
    }

    protected String getPropertyName(int index)
    {
        if (paths == null || paths.length <= index)
            return null;
        if (".".equals(paths[index]))
            return null;

        int dotIndex = paths[index].lastIndexOf(".");
        return paths[index].substring(dotIndex+1);
    }

    @Override
    public BindingInventory getBindingInventory()
    {
        return parentViewBinding.getBindingInventory();
    }

    @Override
    public boolean isDefined()
    {
        return paths != null;
    }

    @Override
    public void track(BindingInventory differentBindingInventory)
    {
        if (paths == null || paths.length ==0)
            return;
        for(int i=0;i<paths.length;i++)
        {
            differentBindingInventory.track(this, this.paths[i]);
        }
    }

    protected UIHandler getUIHandler()
    {
        return parentViewBinding.getUIHandler();
    }

	/**
	 * Lets a UIEvent signal a bounded command
	 * @param arg
	 */
	public boolean execute(CommandArgument arg)
	{
        if (paths == null || paths.length == 0)
            return true;

        for(int i=0;i<paths.length;i++)
        {
            CommandArgument baseArg = arg;
            if (baseArg == null && parentViewBinding != null)
                baseArg = new CommandArgument(getPropertyName(i), parentViewBinding.getTagProperties());
            else if (parentViewBinding == null)
                baseArg = new CommandArgument(getPropertyName(i));

            getBindingInventory().fireCommand(paths[i], baseArg);
            if (baseArg.isEventCancelled())
                return false;
        }
        return true;
	}			
}
