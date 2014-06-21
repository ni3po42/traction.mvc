package ni3po42.android.tractiondemo.models;

import traction.mvc.observables.Command;

public interface DialogModel
{
    String getSomeText();
    void setSomeText(String t);

    Command getClose();
    Command getOpen();
}
