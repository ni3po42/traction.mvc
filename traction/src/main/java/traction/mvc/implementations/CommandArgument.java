package traction.mvc.implementations;

import org.json.JSONObject;

/**
 * Created by tstratto on 6/21/2014.
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
