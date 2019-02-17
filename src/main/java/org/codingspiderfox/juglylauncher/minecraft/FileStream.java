package org.codingspiderfox.juglylauncher.minecraft;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileStream extends FileInputStream {
    public FileStream(String name) throws FileNotFoundException {
        super(name);
    }

    public FileStream(File file) throws FileNotFoundException {
        super(file);
    }

    public FileStream(FileDescriptor fdObj) {
        super(fdObj);
    }

}

