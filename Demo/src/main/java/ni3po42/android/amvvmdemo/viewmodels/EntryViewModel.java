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

import java.util.ArrayList;

import amvvm.implementations.observables.ObservableList;
import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.R;
import ni3po42.android.amvvmdemo.models.EntryItem;

public class EntryViewModel
    extends ViewModel
{
    public final ObservableList<EntryItem> Entries =
            new ObservableList<EntryItem>(new ArrayList<EntryItem>());

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Entries.add(new EntryItem(1,"Something", true));
        Entries.add(new EntryItem(2,"Another Thing", true));
        Entries.add(new EntryItem(3,"Not the first", true));
        Entries.add(new EntryItem(4,"Or the Second", true));
        Entries.add(new EntryItem(5,"Blah", true));
        Entries.add(new EntryItem(6,"Foo", true));
        Entries.add(new EntryItem(7,"Bar", true));

        setContentView(R.layout.entryviewmodel);
    }
}
