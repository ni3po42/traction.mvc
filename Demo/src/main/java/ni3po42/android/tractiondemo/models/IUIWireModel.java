package ni3po42.android.tractiondemo.models;

import traction.mvc.observables.Command;
import traction.mvc.interfaces.IProxyObservableObject;

public interface IUIWireModel
    extends IProxyObservableObject
{
    int getImage();
    void setImage(int image);

    boolean isMainFlagOn();
    void setMainFlagOn(boolean mainFlag);

    int getUpperBound();
    void setUpperBound(int upperBound);

    int getCurrentInteger();
    void setCurrentInteger(int currentInteger);

    Command getMyEvent();

    String getMyString();
    void setMyString(String str);

    int getMyInt();
    void setMyInt(int myInt);

    int getSmallest();
    void setSmallest(int i);

    int getLargest();
    void setLargest(int i);
}
