package ni3po42.android.tractiondemo.models;

import java.util.List;

import traction.mvc.interfaces.IProxyObservableObject;

public interface IDemoSelectionModel
    extends IProxyObservableObject
{
    List<DemoViewModelChoice> getViewModelChoices();

    DemoViewModelChoice getChoice();
    void setChoice(DemoViewModelChoice choice);
}
