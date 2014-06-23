package traction.mvc.observables;

import traction.mvc.interfaces.IObjectListener;

//package visibility
interface IObservable
{
    /**
     * Registers a listener to the IObservable
     * @param sourceName : name of source property to track changes and bubble up to inventory
     */
    void registerListener(String sourceName, IObjectListener listener);

    /**
     * Remove registration from IObservable. Once called, OnObservableUpdated should no longer signal the listener
     * @param listener : listener to unregister from IObservable
     */
    void unregisterListener(String sourceName, IObjectListener listener);
}
