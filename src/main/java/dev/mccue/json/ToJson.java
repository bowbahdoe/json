package dev.mccue.json;

/**
 * An object that can be converted to JSON.
 *
 * <p>
 *     Implementing this interface allows a class to be used
 *     a bit more tersely in the array and object builders.
 * </p>
 *
 * <p>
 *     Conceptually, a given object might have more than one
 *     way to encode into Json. Implementing this interface
 *     implies that the representation returned from this
 *     method in particular is in some way "canonical."
 * </p>
 *
 * <p>
 *     For convenience, there is a vacuous implementation on
 *     Json itself.
 * </p>
 */
public interface ToJson {
    /**
     * @return A Json representation of the object.
     */
    Json toJson();
}
