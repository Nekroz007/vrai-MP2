package ch.epfl.cs107.play.data.json;

/** Used to represent json values */
public abstract class JSONValue {
    /**
     * Serializes the implementing class to a JSON-formatted string.
     * @return A JSON-formatted string representation of the object.
     */
    protected abstract String toJSONString();

    @Override
    public String toString() {
        return toJSONString();
    }
}