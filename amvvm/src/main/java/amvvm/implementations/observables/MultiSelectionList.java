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

import java.util.List;

import amvvm.interfaces.IMultiSelection;

public class MultiSelectionList<T>
    extends ObservableList<T>
    implements IMultiSelection
{

    private ISelection selectionHandler;

    private SparseIntArray indexTrack = new SparseIntArray();

    public MultiSelectionList(List<T> collection)
    {
        super(collection);
    }

    public final Command<SelectedArgument> Selected = new Command<SelectedArgument>()
    {
        @Override
        protected void onExecuted(SelectedArgument selectedArgument) {
            super.onExecuted(selectedArgument);

            if (selectedArgument == null)
                return;
            boolean selected = indexTrack.indexOfKey(selectedArgument.getPosition())<0;
            if (selected)
                indexTrack.put(selectedArgument.getPosition(), selectedArgument.getPosition());
            else
                indexTrack.delete(selectedArgument.getPosition());

            if (selectionHandler != null)
                selectionHandler.onSelection(selectedArgument.getPosition(), selected);

            MultiSelectionList.this.notifyListener("SelectionCount");
        }
    };

    @Override
    public int getSelectionCount() {
        return indexTrack.size();
    }

    @Override
    public void forEach(IAction action)
    {
        if (action == null)
            return;
        for(int i=0;i<indexTrack.size();i++)
        {
            int index = indexTrack.keyAt(i);
            if (!action.doAction(index))
                break;
        }
    }

    public void setSelectionHandler(ISelection selectionHandler) {
        this.selectionHandler = selectionHandler;
    }
}
