package ch.epfl.cs107.play.data.json;

import java.util.ArrayList;
import java.util.List;

/** Used to represent json arrays */
public class JSONArray extends JSONValue {
    /** The array stored in this object */
    private final List<JSONValue> values;

    public JSONArray() {
        this.values = new ArrayList<>();
    }

    /**
     * Returns the value at the given index
     * @param index (int): The index of the value to return
     * @return (JSONValue): The value at the given index
     */
    public JSONValue get(int index) {
        if (index < 0 || index >= values.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + values.size());
        }
        return values.get(index);
    }

    /**
     * Sets the value at the given index
     * @param index (int): The index of the value to set
     * @param value (JSONValue): The new value
     */
    public void set(int index, JSONValue value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (index < 0 || index >= values.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + values.size());
        }
        values.set(index, value);
    }

    /**
     * Appends the given value to the array
     * @param value (JSONValue): The value to append
     */
    public void append(JSONValue value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        values.add(value);
    }

    /**
     * Removes the value at the given index
     * @param index (int): The index of the value to remove
     */
    public void remove(int index) {
        if (index < 0 || index >= values.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + values.size());
        }
        values.remove(index);
    }

    /**
     * Checks if the array contains the given value
     * @param value (JSONValue): The value to check
     * @return (boolean): True if the array contains the value, false otherwise
     */
    public boolean contains(JSONValue value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return values.contains(value);
    }

    /**
     * Returns the size of the array
     * @return (int): The size of the array
     */
    public int size() {
        return values.size();
    }

    @Override
    protected String toJSONString() {
        if (values.isEmpty()) return "[]";

        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) instanceof JSONArray) builder.append(values.get(i).toJSONString());
            else builder.append(values.get(i).toString());
            if (i < values.size() - 1) builder.append(", ");
        }
        return builder.append("]").toString();
    }

    /**
     * Parses the given json into a JSONArray
     * @param json (String): The json to parse
     * @return (JSONArray): The parsed array
     */
    protected static JSONArray parse(String json) {
        json = json.trim().replaceAll("\\s{2,}", "");
        if (!json.startsWith("[") || !(json.endsWith("]") || json.endsWith("],"))) {
            throw new IllegalArgumentException("Invalid JSON array format");
        }
        // Remove the last comma if it exists
        if (json.endsWith("],")) json = json.substring(0, json.length() - 1);

        JSONArray array = new JSONArray();

        // Removing the outermost brackets
        json = json.substring(1, json.length() - 1);
        List<String> parts = splitJsonArray(json);

        for (String part : parts) {
            // Recursive call for nested arrays
            if (part.startsWith("[") && part.endsWith("]")) array.append(parse(part));
                // Regular JSON value
            else array.append(new JSONString(part));
        }

        return array;
    }

    /**
     * Splits a json array into its parts
     * @param json (String): The json array to split
     * @return (List<String>): The parts of the json array
     */
    private static List<String> splitJsonArray(String json) {
        List<String> parts = new ArrayList<>();
        int start = 0;
        int bracketsCount = 0;

        for (int i = 0; i < json.length(); i++) {
            char ch = json.charAt(i);

            // Check for nested arrays
            if (ch == '[') bracketsCount++;
            else if (ch == ']') bracketsCount--;

            // Check for comma or end of string
            if (bracketsCount == 0 && (ch == ',' || i == json.length() - 1)) {
                String part = json.substring(start, ch == ',' ? i : i + 1).trim();
                if (!part.isEmpty()) parts.add(part);
                start = i + 1;
            }
        }

        return parts;
    }
}