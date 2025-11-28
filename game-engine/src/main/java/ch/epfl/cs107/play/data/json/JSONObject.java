package ch.epfl.cs107.play.data.json;

import java.security.InvalidParameterException;
import java.util.*;

/** Used to represent json objects */
public class JSONObject extends JSONValue {
    /** The map of values stored in this object */
    private final Map<String, JSONValue> values = new HashMap<>();

    /**
     * Adds a value to this object
     * @param key (String): The key of the value to add
     * @param value (JSONValue): The value to add
     * @return (JSONObject): This object
     */
    public JSONObject add(String key, JSONValue value) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (values.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        values.put(key, value);
        return this;
    }

    /**
     * Returns all the values in this object
     * @return The value of this object
     */
    public Map<String, JSONValue> get() {
        return values;
    }

    /**
     * Returns the value with the given key
     * @param key (String): The key of the value to return
     * @return (JSONValue): The value with the given key
     */
    public JSONValue get(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        return values.get(key);
    }

    /**
     * Returns whether this object contains the given key
     * @param key (String): The key to check
     * @return (boolean): Whether this object contains the given key
     */
    public boolean containsKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        return values.containsKey(key);
    }

    /**
     * Removes the value with the given key
     * @param key (String): The key of the value to remove
     * @return (JSONObject): This object
     */
    public JSONObject remove(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("Key not found: " + key);
        }

        values.remove(key);
        return this;
    }

    /**
     * Returns the set of keys in this object
     * @return The set of keys in this object
     */
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    protected String toJSONString() {
        if (values.isEmpty()) return "{}";

        String indent = "  ";
        StringBuilder builder = new StringBuilder().append("{\n");
        boolean firstEntry = true;

        for (Map.Entry<String, JSONValue> entry : values.entrySet()) {
            if (!firstEntry) builder.append(",\n");
            builder.append(indent).append("\"").append(entry.getKey()).append("\": ");

            if (entry.getValue() instanceof JSONObject || entry.getValue() instanceof JSONArray) {
                // (?m)^ is a regex that activates multiline mode to match the start of each line
                String nestedJson = (entry.getValue() + "").replaceAll("(?m)^", indent);
                builder.append(nestedJson);
            } else if (entry.getValue() instanceof JSONString value) {
                if (JSONString.isInteger(value.toString()) || JSONString.isDouble(value.toString()) || JSONString.isBoolean(value.toString())) {
                    builder.append(value);
                } else builder.append("\"").append(value).append("\"");
            }
            firstEntry = false;
        }

        return builder.append("\n}").toString().replaceAll(":\\s+", ": ");
    }

    /**
     * Parses a list of lines into a JSONObject
     * @param  lines The lines to parse
     * @return (JSONObject): The parsed JSONObject
     */
    protected static JSONObject parse(List<String> lines) {
        JSONObject json = new JSONObject();

        // Variables used to parse a JSONObject
        boolean inObject = false;
        String objectKey = "";
        List<String> objectLines = new ArrayList<>();
        int braceCount = 0;

        for (String line : lines) {
            // Add the line to the correct list
            if (inObject) objectLines.add(line);

            // Check if we are looking at an empty object or array
            if (!inObject && line.replaceAll("\\s+", "").contains("{}") || line.replaceAll("\\s+", "").contains("[]")) {
                String[] parts = split(line);
                json.add(parts[0], (line.contains("{") && line.contains("}")) ? new JSONObject() : new JSONArray());
            }
            // Check for one line object
            else if (!inObject && line.contains("{") && line.contains("}")) {
                String[] parts = split(line, false);
                String key = parts[0];
                json.add(key, JSONObject.parse(convertToMultiline(parts)));
            }
            // Check if we are at the start of an object
            else if (line.contains("{")) {
                braceCount++;
                if (!inObject) {
                    inObject = true;
                    objectKey = split(line)[0];
                }
            }
            // Check if we are at the start of a nested object
            else if (inObject && line.contains("{")) ++braceCount;
            // Check if we are at the end of an object
            else if (line.contains("}")) {
                braceCount--;
                if (inObject && braceCount == 0) {
                    json.add(objectKey, JSONObject.parse(objectLines));

                    // Reset the variables
                    objectKey = "";
                    objectLines.clear();
                    inObject = false;
                }
            }
            // Check for arrays
            else if (!inObject && line.contains("[")) {
                String[] parts = split(line, false);
                json.add(parts[0], JSONArray.parse(parts[1]));
            }
            // Parse the line
            else if (!inObject) {
                String[] parts = split(line);
                json.add(parts[0], new JSONString(parts[1]));
            }
        }
        return json;
    }

    /**
     * Splits a line into two parts: the key and the value
     * @param line (String): the line to split
     * @param removeCommas (boolean): whether to remove the commas from the value or not
     * @return (String[]): the key and the value (in that order)
     */
    private static String[] split(String line, boolean removeCommas) {
        String[] parts = line.split(": ", 2);
        if (parts.length != 2) throw new InvalidParameterException("Invalid JSON string: " + line);
        if (removeCommas) parts[1] = parts[1].replaceAll(",", "");
        return parts;
    }

    /**
     * Splits a line into two parts: the key and the value
     * @param line (String): the line to split
     * @return (String[]): the key and the value (in that order)
     */
    private static String[] split(String line) {
        return split(line, true);
    }

    /**
     * Converts a line with a linear object into a multiline object
     * @param parts (String[]): the parts of the line
     * @return (List<String>): the multiline object
     */
    private static List<String> convertToMultiline(String[] parts) {
        String newLine = parts[1].replaceAll("\\{", "").replaceAll("}", "");
        ArrayList<String> lines = new ArrayList<>();
        parts = newLine.split(",");
        for (String s : parts) lines.add(s.trim());
        return lines;
    }
}