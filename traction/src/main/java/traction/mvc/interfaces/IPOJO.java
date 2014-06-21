package traction.mvc.interfaces;

import android.util.Property;

/**
 * Created by tstratto on 6/20/2014.
 */
public interface IPOJO
{
    /**
     * Access property from property store.
     * @param name
     * @return if property exists, return property object, or null if not found
     */
    Property<Object,Object> getProperty(String name);
}
