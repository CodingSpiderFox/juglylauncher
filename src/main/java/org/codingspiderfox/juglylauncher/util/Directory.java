package org.codingspiderfox.juglylauncher.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Directory {

    public static boolean Exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void Delete(String path, boolean recursive) {
        File file = new File(path);
        file.delete();
    }

    public static void CreateDirectory(String s) {

        File newDir = new File(s);
        newDir.mkdir();
    }

    public static List<String> EnumerateDirectories(String path) {

        List<String> result = new ArrayList<>();

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for(File file : listOfFiles) {

            if(file.isDirectory()) {
                result.add(file.getName());
            }
        }

        return result;
    }

    public static List<String> EnumerateFiles(String path, String pattern) {

        List<String> result = new ArrayList<>();

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for(File file : listOfFiles) {

            if(!file.isDirectory()) {
                result.add(file.getName());
            }
        }

        return result;
    }
}
