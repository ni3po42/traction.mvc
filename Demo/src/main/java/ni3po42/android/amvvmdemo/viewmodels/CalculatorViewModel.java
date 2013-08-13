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

import amvvm.implementations.observables.Command;
import amvvm.implementations.observables.ResourceArgument;
import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.R;

/**
 * A simple calculator
 */
public class CalculatorViewModel extends ViewModel
{
    private double currentValue = 0;
    private int decimalPlace = 0;

    public String getDisplay()
    {
        return String.valueOf(currentValue);
    }

    public final Command<ResourceArgument> NumberUpdate = new Command<ResourceArgument>()
    {
        { registerAs("NumberUpdate", CalculatorViewModel.this);}
        @Override
        protected void onExecuted(ResourceArgument resourceArgument)
        {
            int nextInput = getResources().getInteger(resourceArgument.getResourceId());
            updateCurrentNumber(nextInput);
        }
    };

    public final Command<Object> ClearDisplay = new Command<Object>()
    {
        { registerAs("ClearDisplay", CalculatorViewModel.this);}
        @Override
        protected void onExecuted(Object arg)
        {
            doClearDisplay();
        }
    };

    public final Command<Object> SwitchToDecimal = new Command<Object>()
    {
        { registerAs("SwitchToDecimal", CalculatorViewModel.this);}
        @Override
        protected void onExecuted(Object o)
        {
            decimalPlace = 1;
        }
    };

    public void doClearDisplay()
    {
        currentValue = 0;
        decimalPlace = 0;
        notifyListener("Display");
    }

    public void updateCurrentNumber(int nextInput)
    {
        if (currentValue == 0)
            currentValue = nextInput;
        else if (decimalPlace == 0)
        {
            currentValue = currentValue * 10 + nextInput;
        }
        else
        {
            currentValue = currentValue +  (nextInput / Math.pow(10, decimalPlace));
            decimalPlace++;
        }
        notifyListener("Display");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calculator);
    }
}
