package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgeInstallerLibrary {
    
    private String name;

    private URL url;

    private boolean serverreq;

    private String[] checksums;

    private boolean clientreq;
}
