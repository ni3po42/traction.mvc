package traction.mvc.observables;

import traction.mvc.interfaces.IObjectListener;

public abstract class OnPropertyChangedEvent
    implements IObjectListener
{
    @Override
    public void onEvent(String propagationId) {
        //eh, nothing
    }

    protected abstract void onChange(String propertyName, Object oldValue, Object newValue);
}
