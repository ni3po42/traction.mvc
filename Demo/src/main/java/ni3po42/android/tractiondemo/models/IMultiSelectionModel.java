package ni3po42.android.tractiondemo.models;

import java.util.List;

import traction.mvc.observables.Command;
import traction.mvc.observables.IProxyObservableObject;

public interface IMultiSelectionModel
    extends IProxyObservableObject
{
    List<SelectableItem> getItems();

    void setSelected(SelectableItem i);
    SelectableItem getSelected();

    int getSelectedCount();
    void setSelectedCount(int i);

    Command getCountSelected();

    String getMyString();
    void setMyString(String myString);
}
