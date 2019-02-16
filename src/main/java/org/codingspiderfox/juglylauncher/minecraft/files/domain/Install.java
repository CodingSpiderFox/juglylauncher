package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Install {

    private String ProfileName;

    private String Target;

    private String Path;

    private String Version;

    private String FilePath;

    private String Welcome;

    private String Minecraft;

    private String MirrorList;

    private String Logo;

    private String ModList;
}
