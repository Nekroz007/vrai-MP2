package ch.epfl.cs107.play.data.json;

/** Used to represent json strings, ints, doubles and booleans */
public class JSONString extends JSONValue {
    /** The value of the string */
    private String value;

    /**
     * Creates a new JSONString with the given value
     * @param value (String): The value of the string
     */
    public JSONString(String value) {
        set(value);
    }
    /**
     * Sets the value of this object
     * @param value (String): The new value of this object
     */
    public void set(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value;
    }


    /**
     * Returns the value of this object
     * @return (String): The value of this object as a String
     */
    public String getString() {
        return value;
    }

    /**
     * Returns the value of this object
     * @return (Integer): The value of this object as an Integer
     */
    public Integer getInt() {
        return isInteger(value) ? Integer.parseInt(value) : null;
    }

    /**
     * Returns the value of this object
     * @return (Double): The value of this object as a Double
     */
    public Double getDouble() {
        return isDouble(value) ? Double.parseDouble(value) : null;
    }

    /**
     * Returns the value of this object
     * @return (Boolean): The value of this object as a Boolean
     */
    public Boolean getBoolean() {
        return isBoolean(value) ? Boolean.parseBoolean(value) : null;
    }

    @Override
    protected String toJSONString() {
        return value;
    }

    /**
     * Checks if the given string is an integer
     * @param str (String): The string to check
     * @return (boolean): True if the string is an integer, false otherwise
     */
    public static boolean isInteger(String str) {
        return str.matches("-?\\d+");
    }

    /**
     * Checks if the given string is a boolean
     * @param str (String): The string to check
     * @return (boolean): True if the string is a boolean, false otherwise
     */
    public static boolean isBoolean(String str) {
        return "true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str);
    }

    /**
     * Checks if the given string is a double
     * @param str (String): The string to check
     * @return (boolean): True if the string is a double, false otherwise
     */
    public static boolean isDouble(String str) {
        // This regex covers basic double representations but not scientific notation
        return str.matches("[-+]?\\d*\\.?\\d+");
    }
}