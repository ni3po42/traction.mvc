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
import android.widget.Toast;

import traction.mvc.controllers.FragmentController;
import traction.mvc.implementations.CommandArgument;
import traction.mvc.interfaces.IOnExecuteListener;
import ni3po42.android.tractiondemo.R;
import ni3po42.android.tractiondemo.models.IMultiSelectionModel;
import ni3po42.android.tractiondemo.models.SelectableItem;
import traction.mvc.observables.OnPropertyChangedEvent;

public class MultiSelectController extends FragmentController
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View.setContentView(R.layout.multiselection);
        View.setMenuLayout(R.menu.multiselectoptions);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        final IMultiSelectionModel model = View.getScope();

        model.getItems().clear();
        for(int i=0;i<12;i++)
            model.getItems().add(new SelectableItem());

        model.setSelectedCount(0);
        model.getCountSelected().setExecuteListener(new IOnExecuteListener() {
            @Override
            public void onExecuted(CommandArgument argument) {
                String txt = model.getMyString() == null ? "" : model.getMyString();
                Toast.makeText(getActivity(), "There are " + model.getSelectedCount() + " item(s) selected. " + txt, Toast.LENGTH_SHORT).show();
            }
        });
        model.getProxyObservableObject().addOnChange(new OnPropertyChangedEvent() {
            @Override
            protected void onChange(String propertyName, Object oldValue, Object newValue) {
                if ("Selected".equals(propertyName)) {
                    model.getSelected().setSelected(!model.getSelected().isSelected());
                    if (model.getSelected().isSelected())
                        model.setSelectedCount(model.getSelectedCount() + 1);
                    else
                        model.setSelectedCount(model.getSelectedCount() - 1);
                }

            }
        });
    }
}
