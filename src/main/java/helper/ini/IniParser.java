package helper.ini;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple .ini file parser used to parse the settings.ini file.
 * <br>
 * To get a value: use getVarType(key). Sections are separated by slashes. For example, if I wanted to get
 * "key" in section "section", I use:<br>
 * getString("key/section");
 */
public class IniParser {
    private final Map<String, String> values;

    public IniParser(String filepath) {
        this.values = parseIni(filepath);
    }

    private static Map<String, String> parseIni(String filepath) throws UncheckedIOException {
        HashMap<String, String> varAndValue = new HashMap<>();
        String currentSection = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Line is commented
                if (currentLine.startsWith(";") || currentLine.startsWith("#") || currentLine.isBlank()) {
                    continue;
                }

                // There is a new subsection
                if (currentLine.startsWith("[")) {
                    currentSection = currentLine.substring(1, currentLine.length() - 1);
                    continue;
                }

                // Parse the line as a key/value pair
                currentLine = currentLine.replace(" ", "");

                String[] keyValue = currentLine.split("=");

                if (keyValue.length != 2) {
                    throw new IniParseException(
                            "Could not parse line in " + filepath + ":\n" + currentLine
                    );
                }

                if (currentSection.equals("")) {
                    varAndValue.put(
                            keyValue[0],
                            keyValue[1]
                    );
                } else {
                    varAndValue.put(
                            currentSection + "/" + keyValue[0],
                            keyValue[1]
                    );
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return varAndValue;
    }

    public String getString(String varName) {
        return this.values.get(varName);
    }

    public float getFloat(String varName) {
        return Float.parseFloat(this.getString(varName));
    }

    public int getInt(String varName) {
        return Integer.parseInt(this.getString(varName));
    }
}
