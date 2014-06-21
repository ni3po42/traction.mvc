package ni3po42.android.tractiondemo.models;

import traction.mvc.observables.Command;

public interface ICalculatorModel
{
    String getDisplay();
    void setDisplay(String str);

    Command getNumberUpdate();
    Command getOperation();
    Command getClearDisplay();
    Command getSwitchToDecimal();
}
