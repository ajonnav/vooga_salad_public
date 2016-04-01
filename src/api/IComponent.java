package api;

import java.io.Serializable;


/**
 * This is the interface for all components, which hold data.
 */
public interface IComponent extends Serializable {
    void update ();

    /**
     * @return whether only one of these components is allowed for an Entity
     */
    default boolean unique () {
        return false;
    }

    /**
     * @return the class to be put into an Entity map
     */
    default Class<? extends IComponent> getClassForComponentMap () {
        return getClass();
    }
}