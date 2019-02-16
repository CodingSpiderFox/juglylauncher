package org.codingspiderfox.juglylauncher.util;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class FileInfo {

    @Getter
    @Setter
    private long length;

    private String path;

    public FileInfo(String path) {

        this.path = path;
    }

    public void Delete() {

        File file = new File(path);
        file.delete();
    }
}
