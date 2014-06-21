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

package ni3po42.android.tractiondemo.controllers;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import traction.mvc.controllers.FragmentController;
import traction.mvc.implementations.CommandArgument;
import traction.mvc.interfaces.IOnExecuteListener;
import ni3po42.android.tractiondemo.R;
import ni3po42.android.tractiondemo.models.ICalculatorModel;

/**
 * A simple calculator. not perfect, but fine for a demonstration
 */
public class CalculatorController extends FragmentController
{
    private static final int MAX_DIGITS = 7;

    private double currentValue = 0;
    private int decimalPlace = 0;
    private int totalDigits = 0;
    private double stack = 0;
    private int stackSize = 0;
    private String currentAction = null;

    public void doOperation(JSONObject argument)
    {
        String action = null;
        ICalculatorModel model = View.getScope();
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
            model.setDisplay(String.valueOf(currentValue));
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
            model.setDisplay(String.valueOf(currentValue));
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
        ICalculatorModel model = View.getScope();
        model.setDisplay(String.valueOf(currentValue));
    }

    public void updateCurrentNumber(CommandArgument argument)
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
        ICalculatorModel model = View.getScope();
        model.setDisplay(String.valueOf(currentValue));
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View.setContentView(R.layout.calculator);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        ICalculatorModel model = View.getScope();

        currentValue = 0;
        stack = 0;
        model.setDisplay(String.valueOf(currentValue));

        model.getNumberUpdate().setExecuteListener(new IOnExecuteListener() {
            @Override
            public void onExecuted(CommandArgument argument) {
                updateCurrentNumber(argument);
            }
        });
        model.getClearDisplay().setExecuteListener(new IOnExecuteListener() {
            @Override
            public void onExecuted(CommandArgument argument) {
                doClearDisplay();
            }
        });
        model.getOperation().setExecuteListener(new IOnExecuteListener() {
            @Override
            public void onExecuted(CommandArgument argument) {
                doOperation(argument.getEventData());
            }
        });
        model.getSwitchToDecimal().setExecuteListener(new IOnExecuteListener() {
            @Override
            public void onExecuted(CommandArgument argument) {
                decimalPlace = 1;
            }
        });
    }
}
