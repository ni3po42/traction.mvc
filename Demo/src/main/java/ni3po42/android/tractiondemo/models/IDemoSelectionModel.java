package ni3po42.android.tractiondemo.models;

import android.app.Fragment;

import java.util.List;

import traction.mvc.controllers.FragmentController;
import traction.mvc.observables.IProxyObservableObject;

public interface IDemoSelectionModel
    extends IProxyObservableObject
{
    List<DemoFragmentChoice> getChoices();

    Fragment getCurrentFragment();
    void setCurrentFragment(Fragment fragment);

    DemoFragmentChoice getChoice();
    void setChoice(DemoFragmentChoice choice);
}
