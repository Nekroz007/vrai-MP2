package ch.epfl.cs107.play.data.json;

import java.io.*;
import java.util.ArrayList;

/** Used to read JSON files and write to JSON files */
public class JSONParser {
    /** The valid file extensions for JSON files */
    private static final String[] FILE_EXTENSIONS = new String[] { ".json", ".txt" };

    /**
     * Used to read JSON files and get their contents as a JSONObject
     * @param path (String): the path to the file
     * @return (JSONObject): the contents of the file
     */
    public static JSONObject readJSONFromFile(String path) {
        validatePath(path);

        // Read the file
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            ArrayList<String> lines = new ArrayList<>();
            StringBuilder arrayContent = new StringBuilder();
            int arrayDepth = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().replaceAll("\"", "");
                if (line.isEmpty() || line.equals("\n")) continue;

                // Convert multiline arrays to single line arrays
                if (arrayDepth > 0 || line.endsWith("[")) {
                    arrayContent.append(line);

                    // Update array depth count
                    arrayDepth += (int) line.chars().filter(ch -> ch == '[').count();
                    arrayDepth -= (int) line.chars().filter(ch -> ch == ']').count();

                    // If array ends, add to lines and reset arrayContent
                    if (arrayDepth == 0) {
                        lines.add(arrayContent.toString());
                        arrayContent = new StringBuilder();
                    }
                } else lines.add(line);
            }

            // Remove the first and last line since they are just the container brackets
            if (!lines.isEmpty()) {
                lines.remove(0);
                lines.remove(lines.size() - 1);
            }

            return JSONObject.parse(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used to write a JSONObject to a file
     * @param path (String): the path to the file
     * @param json (JSONObject): the JSONObject to write
     */
    public static void writeJSONToFile(String path, JSONObject json) {
        validatePath(path);

        // Check if the JSONObject is null
        if (json == null) {
            throw new IllegalArgumentException("JSON cannot be null");
        }

        // Write to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used to validate a file path
     * @param path (String): the path to validate
     */
    private static void validatePath(String path) {
        // Check if the path is null or empty
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        // Check if the file extension is valid
        boolean valid = false;
        for (String extension : FILE_EXTENSIONS) {
            int length = extension.length();
            String ext = path.substring(path.length() - length);
            if (ext.toLowerCase().equals(extension)) {
                if (ext.equals(".txt")) {
                    System.err.println("Warning in file: " + path);
                    System.err.println("It is recommended to use the .json file extension for JSON files instead of .txt");
                }
                valid = true;
                break;
            }
        }
        if (!valid) throw new IllegalArgumentException("Invalid file extension: ." + path.split("\\.")[1]);
    }
}