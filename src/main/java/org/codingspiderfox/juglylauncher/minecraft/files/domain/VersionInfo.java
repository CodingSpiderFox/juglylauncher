package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionInfo {

    private Logging logging;
    private ForgeInstallerLibrary[] libraries;
    private String id;
    private String time;
    private String releaseTime;
    private String type;
    private String minecraftArguments;
    private String mainClass;
    private String inheritsFrom;
    private String jar;

}
