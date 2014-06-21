package traction.mvc.interfaces;

import traction.mvc.implementations.CommandArgument;

public interface IOnExecuteListener
{
    void onExecuted(CommandArgument arg);
}