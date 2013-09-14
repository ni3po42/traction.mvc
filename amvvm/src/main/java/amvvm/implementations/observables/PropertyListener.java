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

package amvvm.implementations.observables;

import amvvm.interfaces.IObjectListener;

public abstract class PropertyListener<T>
    implements IObjectListener
{
    private String propertyName;
    public PropertyListener(String propertyName)
    {
        this.propertyName = propertyName;
    }

    @Override
    public void onEvent(EventArg arg)
    {
        if (propertyName == null || !propertyName.equals(arg.getPropagationId()))
            return;

        propertyUpdated((T)arg.getSource());
    }

    protected abstract void propertyUpdated(T source);
}
