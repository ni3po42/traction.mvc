package traction.mvc.tests;

import android.database.DataSetObserver;

import junit.framework.TestCase;

import java.util.ArrayList;

import traction.mvc.observables.ObservableList;
import traction.mvc.observables.ObservableObject;
import traction.mvc.observables.PropertyStore;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestObservableList extends TestCase
{
    public TestObservableList()
    {

    }

    public void testCanCreateList()
    {
        ObservableList<oo> list = createList();

        assertNotNull(list);
    }


    public void testCanNotify()
    {
        //arrange
        ObservableList<oo> list = createList();
        DataSetObserver listen = mock(DataSetObserver.class);
        list.registerDataSetObserver(listen);

        //act
        list.notifyDataSetChanged();

        //assert
        verify(listen).onChanged();
    }

    private ObservableList<oo> createList()
    {
        return new ObservableList(new ArrayList<oo>());
    }

    class oo
        extends ObservableObject
    {

        private PropertyStore store = new PropertyStore(oo.class);
        @Override
        public PropertyStore getPropertyStore() {
            return store;
        }

        private int i;

        int getI() {
            return i;
        }

        void setI(int i) {
            this.i = i;
            notifyListener("I");
        }
    }
}
