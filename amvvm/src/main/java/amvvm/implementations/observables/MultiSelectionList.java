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

import java.util.Collection;
import java.util.List;

import amvvm.interfaces.IMultiSelection;

public class MultiSelectionList<T>
    extends ObservableList<T>
    implements IMultiSelection<IMultiSelection.SelectionKey>
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
            {
                SelectionKey key = new SelectionKey();
                key.setPosition(selectedArgument.getPosition());
                selectionHandler.onSelection(key, selected);
            }

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
        SelectionKey key = new SelectionKey();
        for(int i=0;i<indexTrack.size();i++)
        {
            int index = indexTrack.keyAt(i);
            key.setPosition(index);
            if (!action.doAction(key))
                break;
        }
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        if (super.removeAll(arg0))
        {
            indexTrack.clear();
            notifyListener("SelectionCount");
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object arg0) {
        int index = indexOf(arg0);
        if (super.remove(arg0))
        {
            indexTrack.delete(index);
            notifyListener("SelectionCount");
            return true;
        }
        return false;
    }

    @Override
    public T remove(int location)
    {
        T obj = super.remove(location);
        if (obj != null)
        {
            indexTrack.delete(location);
            notifyListener("SelectionCount");
        }
        return obj;
    }

    @Override
    public void clear() {
        super.clear();
        indexTrack.clear();
        notifyListener("SelectionCount");
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        if(super.retainAll(arg0))
        {
            indexTrack.clear();
            notifyListener("SelectionCount");
            return true;
        }
        return false;
    }

    public void setSelectionHandler(ISelection selectionHandler) {
        this.selectionHandler = selectionHandler;
    }
}
