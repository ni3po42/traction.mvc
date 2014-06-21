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

import traction.mvc.controllers.FragmentController;
import ni3po42.android.tractiondemo.R;
import ni3po42.android.tractiondemo.models.EntryItem;
import ni3po42.android.tractiondemo.models.IEntryModel;

public class EntryController
    extends FragmentController
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View.setContentView(R.layout.entryviewmodel);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        IEntryModel model = View.getScope();

        model.getEntries().add(new EntryItem(1,"Something", true));
        model.getEntries().add(new EntryItem(2,"Another Thing", true));
        model.getEntries().add(new EntryItem(3,"Not the first", true));
        model.getEntries().add(new EntryItem(4,"Or the Second", true));
        model.getEntries().add(new EntryItem(5,"Blah", true));
        model.getEntries().add(new EntryItem(6,"Foo", true));
        model.getEntries().add(new EntryItem(7,"Bar", true));
    }
}
