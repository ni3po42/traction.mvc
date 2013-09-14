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

import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.R;
import ni3po42.android.amvvmdemo.models.RelativeContextModel;

public class RelativeContextViewModel
    extends ViewModel
{

    private RelativeContextModel myModel;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        RelativeContextModel m = new RelativeContextModel();
        m.setValue(3141);
        m.setString("Hello World!");
        setContentView(R.layout.relativecontextroot);
        setMyModel(m);
    }

    public RelativeContextModel getMyModel() {
        return myModel;
    }

    public void setMyModel(RelativeContextModel myModel) {
        this.myModel = myModel;
        notifyListener("MyModel");
    }
}
