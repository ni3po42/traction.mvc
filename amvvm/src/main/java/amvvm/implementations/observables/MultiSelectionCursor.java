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

import android.util.SparseIntArray;

import amvvm.interfaces.ICursorExtension;
import amvvm.interfaces.IMultiSelection;
import amvvm.interfaces.IProxyObservableObject;

public class MultiSelectionCursor
    extends ObservableCursor
    implements IMultiSelection
{
    private ISelection selectionHandler;
    private SparseIntArray keyTrack = new SparseIntArray();

    public final Command<SelectedArgument> Selected = new Command<SelectedArgument>()
    {
        @Override
        protected void onExecuted(SelectedArgument selectedArgument) {
            super.onExecuted(selectedArgument);
            if (selectedArgument == null)
                return;

            boolean selected = keyTrack.indexOfKey(selectedArgument.getPosition())<0;
            if (selected)
                keyTrack.put(selectedArgument.getPosition(), selectedArgument.getPosition());
            else
                keyTrack.delete(selectedArgument.getPosition());

            if (selectionHandler != null)
                selectionHandler.onSelection(selectedArgument.getPosition(), selected);
            MultiSelectionCursor.this.notifyListener("SelectionCount");
        }
    };

    @Override
    public int getSelectionCount() {
        return keyTrack.size();
    }

    @Override
    public void forEach(IAction action) {
        if (action == null)
            return;
        for(int i=0;i<keyTrack.size();i++)
        {
            int key = keyTrack.keyAt(i);
            if (!action.doAction(key))
                break;
        }
    }

    public void setSelectionHandler(ISelection selectionHandler) {
        this.selectionHandler = selectionHandler;
    }
}
