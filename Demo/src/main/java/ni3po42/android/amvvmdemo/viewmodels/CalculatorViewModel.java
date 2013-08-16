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
import android.util.FloatMath;

import amvvm.implementations.observables.Command;
import amvvm.implementations.observables.ResourceArgument;
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

    private int currentAction = -1;

    public String getDisplay()
    {
        return String.valueOf(currentValue);
    }

    public final Command<ResourceArgument> NumberUpdate = new Command<ResourceArgument>()
                .setOnExecuteListener(new Command.IOnExecuteListener<ResourceArgument>()
                {
                    @Override
                    public void onExecuted(ResourceArgument resourceArgument)
                    {
                        int nextInput = getResources().getInteger(resourceArgument.getResourceId());
                        updateCurrentNumber(nextInput);
                    }
                });

    public final Command<ResourceArgument> Operation = new Command<ResourceArgument>()
    {
        @Override
        protected void onExecuted(ResourceArgument resourceArgument)
        {
            doOperation(resourceArgument.getResourceId());
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

    public void doOperation(int action)
    {
        if (action != R.id.calc_equals)
        {
            currentAction = action;
            pushStack();
        }
        else
        {
            popStack();
            switch (currentAction)
            {
                //push to stack
                case R.id.calc_add:
                    currentValue += stack;
                    break;
                case R.id.calc_subtract:
                    currentValue = stack - currentValue;
                    break;
                case R.id.calc_multiply:
                    currentValue *= stack;
                    break;
                case R.id.calc_divide:
                    if (currentValue == 0)
                    {
                        currentValue = Double.NaN;
                    }
                    else
                    {
                        currentValue = stack / currentValue;
                    }
                    break;
            }
            currentAction = -1;
            notifyListener("Display");
            return;
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

    public void updateCurrentNumber(int nextInput)
    {
        if (totalDigits == MAX_DIGITS)
            return;

        if (currentValue == 0)
            currentValue = nextInput;
        else if (decimalPlace == 0)
        {
            currentValue = currentValue * 10 + nextInput;
        }
        else
        {
            currentValue = currentValue +  (nextInput * Math.pow(10, -decimalPlace));
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
