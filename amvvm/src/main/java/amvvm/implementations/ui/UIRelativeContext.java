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

package amvvm.implementations.ui;

import amvvm.implementations.BindingInventory;
import amvvm.interfaces.IViewBinding;

public class UIRelativeContext
    extends UIProperty<Object>
{
    public UIRelativeContext(IViewBinding viewBinding, int pathAttribute) {
        super(viewBinding, pathAttribute);
    }

    public UIRelativeContext(IViewBinding viewBinding) {
        super(viewBinding);
    }

    @Override
    public BindingInventory getBindingInventory() {
        return super.getBindingInventory().getParentInventory();
    }
}
