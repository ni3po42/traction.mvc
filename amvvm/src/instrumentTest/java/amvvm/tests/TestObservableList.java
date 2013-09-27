package amvvm.tests;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;

import java.util.ArrayList;

import amvvm.implementations.observables.ObservableList;
import amvvm.implementations.observables.ObservableObject;
import amvvm.implementations.observables.PropertyStore;
import amvvm.interfaces.IObjectListener;

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
        IObjectListener listen = mock(IObjectListener.class);
        list.registerListener("testSource",listen);

        //act
        list.notifyListener();

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

        verify(listen).onEvent(argument.capture());

        String arg = argument.getValue();

        assertEquals("testSource", arg);
    }

    private ObservableList<oo> createList()
    {
        return new ObservableList(new ArrayList<oo>());
    }

    class oo
        extends ObservableObject
    {

        private PropertyStore store = new PropertyStore();
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
