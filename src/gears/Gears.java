package gears;

import java.awt.Desktop;

import org.json.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import java.net.URI;
import java.net.URISyntaxException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class Gears {
    // constructor
    private Gears() {

    };

    // #region UTILS

    @SuppressWarnings("unchecked")
    public static <T> T[] getArrayGeneric(Class<T> clazz, int length) {
        return (T[]) Array.newInstance(clazz, length);
    }

    public static <T> T getInstanceGeneric(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            throw new RuntimeException("Failed to create an instance of " + clazz.getName(), e);
        }
    }

    public static <T1, T2> Pair<T1, T2> cPair(T1 one, T2 two) {
        return new Pair<>(one, two);
    }

    public static void openLinkDefaultBrowser(String link) {
        try {
            try {
                // System.out.println("Open link : " + link);
                Desktop.getDesktop().browse(new URI(link));
            } catch (URISyntaxException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        ;
    }

    public static void searchInDefaultBrowser(String searchTerm) {
        try {
            // Construct the search URL for Google
            String searchUrl = "https://www.google.com/search?q=" + searchTerm.replace(' ', '+');
            URI uri = new URI(searchUrl);

            // Get the default desktop object and browse the URI
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            } else {
                System.out.println("Desktop browsing is not supported.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Pair<T1, T2> {
        public T1 elem1;
        public T2 elem2;

        public Pair(T1 elem1, T2 elem2) {
            this.elem1 = elem1;
            this.elem2 = elem2;
        }
    }

    public static boolean stringIsNullOrEmpty(String string) {
        return string == "" || string == null;
    }

    // Offset Method
    public static <T1 extends Iterable<T2>, T2> String insertManyStrings(Boolean replaceAtLocation, String mainString, T1 insertlist,
            Func<T2, Pair<Integer, String>> getStringInfo) {
        int startLength = mainString.length();
        Integer offset = 0;

        for (T2 elem : insertlist) {
            Pair<Integer, String> stringInfos = getStringInfo.call(elem);

            if (replaceAtLocation) {
                mainString = deleteCharacterAt(mainString, stringInfos.elem1 + offset);
            }

            StringBuffer buffer = new StringBuffer(mainString);
            buffer.insert(stringInfos.elem1 + offset, stringInfos.elem2);

            mainString = buffer.toString();
            offset = mainString.length() - startLength;
        }

        return mainString;
    }

    // Split Method : DO Finish
    public static String insertMultipleStringsInStringSplit(String mainString,
            List<Pair<Integer, String>> mainStrings) {
        String buildString = "";
        int lastPos = 0;

        for (Pair<Integer, String> stringInfos : mainStrings) {
            // Cut mainString
            String subString = mainString.substring(stringInfos.elem1, lastPos);

            // Insert string
            StringBuffer buffer = new StringBuffer(subString);
            buffer.insert(0, stringInfos.elem2);

            buildString += buffer.toString();

            lastPos = stringInfos.elem1;
        }

        return buildString;
    }

    public static String deleteCharacterAt(String string, int location) {
        StringBuilder sb = new StringBuilder(string);
        sb.deleteCharAt(location);
        return sb.toString();
    }

    public static String deleteStringAt(String string, int start, int end) {
        StringBuilder sb = new StringBuilder(string);
        sb.delete(start, end);
        return sb.toString();
    }

    // To Create Beans
    public static class Container<T> {
        private T value;

        public Container(T value) {
            this.value = value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    // C# out keyword
    public static class Out<T> {
        public T param;

        public Out(T param) {
            this.param = param;
        }
    }

    // ???
    public static class MethodDeployer<R> {
        public List<Pair<String, Object>> propertyBag;
        public CoreFunc<String> method;

        public MethodDeployer(List<Pair<String, Object>> propertyBag, CoreFunc<String> method) {
            this.propertyBag = propertyBag;
            this.method = method;
        }
    }

    // #endregion

    // #region Iterables

    public static <T1 extends Iterable<T2>, T2> boolean contains(T1 iterable, T2 toFind) {
        for (T2 element : iterable) {
            if (element == toFind) {
                System.out.println("Contains : " + toFind);
                return true;
            }
        }

        return false;
    }

    // TODO Return Count
    public static <T1 extends Iterable<T2>, T2> T2 findIn(T1 iterable, T2 toFind) {
        int count = 0;

        for (T2 element : iterable) {
            if (element == toFind) {
                System.out.println("Found : " + toFind + " At : " + count);
                return element;
            }

            count++;
        }

        return null;
    }

    public static <T1 extends Iterable<T2>, T2, T3> List<T3> iterableConverter(T1 from, Func<T2, T3> convertionMethod) {
        List<T3> list = new ArrayList<>();

        for (T2 element : from) {
            list.add(convertionMethod.call(element));
        }

        return list;
    }

    // #endregion

    // #region FILE

    public static void readTextFileCore(String filePath, Action<String> readLineAction) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            try {
                String line = br.readLine();

                while (line != null) {
                    readLineAction.call(line);
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
            System.exit(1);
        }
    }

    public static String readTextFileCombined(String filePath) {
        if (stringIsNullOrEmpty(filePath)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        readTextFileCore(filePath, line -> {
            sb.append(line);
            sb.append(System.lineSeparator());
        });

        return sb.toString();
    }

    public static ArrayList<String> readTextFileLines(String filePath) {
        ArrayList<String> lines = new ArrayList<>();

        readTextFileCore(filePath, line -> {
            lines.add(line);
        });

        return lines;
    }

    public static String stringArrayToString(ArrayList<String> array) {
        String combined = "";

        StringBuilder sb = new StringBuilder();

        for (String string : array) {
            sb.append(string);
            sb.append(System.lineSeparator());
        }

        combined = sb.toString();

        return combined;
    }

    public static void writeTextToFile(String filePath, String text) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(text);
            // System.out.println("Text saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeJsonFile(String path, JSONObject jsonObject, Runnable onSaved) {
        // Write JSON to file
        try (FileWriter file = new FileWriter(path)) {
            file.write(jsonObject.toString());
            file.flush();
            onSaved.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject readJsonFile(String path) {
        try {
            // Read file as String
            String content = new String(Files.readAllBytes(Paths.get(path)));

            // Print JSON Object
            // System.out.println(jsonObject.toString(4)); // Pretty print with indentation

            // Convert to JSONObject
            return new JSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void openFileDefault(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileNameFromPath(String filePath) {
        if (filePath.contains("\\")) {
            return filePath.substring(filePath.lastIndexOf("\\") + 1);
        }

        return filePath;
    }

    // #endregion

    // #region Functional Interface Core

    // Everything / Divine / Core Function > All -> Define a type of any function
    // e.g : CoreFunc<String> coreFunc = params -> params.toString(); //return list
    // of the parameters
    // coreFunc.call(37, 42, "Test123");
    // e.g 2 : CoreFunc<String> coreFunc = params ->
    // Gears.insertMultipleStringsInString((String) params[0], (List<Pair<Integer,
    // String>>) params[1]);
    @FunctionalInterface
    public interface CoreFunc<R> {
        R call(Object... args);
    }

    // Condition variables
    @FunctionalInterface
    public interface Condition {
        Boolean check();
    }

    // 2 T params + return -> BiFunction
    @FunctionalInterface
    public interface BiFunction<T1, T2, R> {
        R call(T1 t1, T2 t2);
    }

    // 1 T param + return -> Function e.g : Func<String, String> t = s -> s;
    @FunctionalInterface
    public interface Func<T, R> {
        R call(T args);
    }

    // 1 T param -> Action e.g : Func<String> t = s -> System.out.prinln(s);
    @FunctionalInterface
    public interface Action<T> {
        void call(T args);
    }

    // #endregion

    // #region MATH

    public static class Vector2 {
        public float x;
        public float y;

        public Vector2() {
            x = 0.0f;
            y = 0.0f;
        }

        public Vector2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(Vector2 other) {
            return (x == other.x && y == other.y);
        }
    }

    public static class Vector2Int {
        public int x;
        public int y;

        public Vector2Int() {
            x = 0;
            y = 0;
        }

        public Vector2Int(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(Vector2Int other) {
            return (x == other.x && y == other.y);
        }
    }

    // #endregion
}