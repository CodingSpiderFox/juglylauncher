package org.codingspiderfox.juglylauncher.util;

import java.io.File;

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
}
