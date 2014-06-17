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

package ni3po42.android.amvvmdemo.viewmodels;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import amvvm.implementations.observables.Command;
import amvvm.implementations.observables.SimpleCommand;
import amvvm.interfaces.ICommand;
import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.R;

/**
 * A simple calculator. not perfect, but fine for a demonstration
 */
public class CalculatorViewModel extends ViewModel
{
    private static final int MAX_DIGITS = 7;

    private double currentValue = 0;
    private int decimalPlace = 0;
    private int totalDigits = 0;

    private double stack = 0;
    private int stackSize = 0;

    private String currentAction = null;

    public String getDisplay()
    {
        return String.valueOf(currentValue);
    }

    public final Command<ICommand.CommandArgument> NumberUpdate = new Command<ICommand.CommandArgument>()
    {
        @Override
        public void onExecuted(ICommand.CommandArgument argument)
        {
            updateCurrentNumber(argument);
        }
    };

    public final Command<ICommand.CommandArgument> Operation = new Command<ICommand.CommandArgument>()
    {
        @Override
        protected void onExecuted(ICommand.CommandArgument argument)
        {
            doOperation(argument.getEventData());
        }
    };

    public final SimpleCommand ClearDisplay = new SimpleCommand()
    {
        @Override
        protected void onExecuted(CommandArgument commandArgument)
        {
            doClearDisplay();
        }
    };

    public final SimpleCommand SwitchToDecimal = new SimpleCommand()
    {
        @Override
        protected void onExecuted(CommandArgument commandArgument)
        {
            decimalPlace = 1;
        }
    };

    public void doOperation(JSONObject argument)
    {
        String action = null;
        try
        {
            action = argument.getString("CommandValue");
        }
        catch (JSONException ex)
        {

        }

        if ("+/-".equals(action))
        {
            currentValue*=-1;
            notifyListener("Display");
            return;
        }
        else if ("=".equals(action))
        {
            popStack();
            if ("+".equals(currentAction))
                    currentValue += stack;
            else if ("-".equals(currentAction))
                    currentValue = stack - currentValue;
            else if ("X".equals(currentAction))
                    currentValue *= stack;
            else if ("/".equals(currentAction)) {
                if (currentValue == 0) {
                    currentValue = Double.NaN;
                } else {
                    currentValue = stack / currentValue;
                }
            }
            currentAction = null;
            notifyListener("Display");
            return;
        }
        else
        {
            currentAction = action;
            pushStack();
        }
        totalDigits = 0;
    }

    public void pushStack()
    {
        stackSize++;
        stack = currentValue;
        decimalPlace = 0;
        currentValue = 0;
    }

    public void popStack()
    {
        if (stackSize != 0)
            stackSize--;
        decimalPlace = 0;
    }

    public void doClearDisplay()
    {
        currentValue = 0;
        decimalPlace = 0;
        totalDigits = 0;
        notifyListener("Display");
    }

    public void updateCurrentNumber(ICommand.CommandArgument argument)
    {
        int nextInput = 0;
        try
        {
            nextInput = argument.getEventData().getInt("CommandValue");
        }
        catch (JSONException ex)
        {

        }

        if (totalDigits == MAX_DIGITS)
            return;

        if (currentValue == 0)
            currentValue = nextInput;
        else if (decimalPlace == 0)
        {
            currentValue = currentValue * 10 + (currentValue > 0 ? nextInput : -nextInput);
        }
        else
        {
            currentValue = currentValue +  ((currentValue > 0 ? nextInput : -nextInput) * Math.pow(10, -decimalPlace));
            decimalPlace++;
        }
        totalDigits++;
        notifyListener("Display");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calculator);
    }
}
