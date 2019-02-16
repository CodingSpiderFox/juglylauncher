package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classifiers {

    public VersionJsonDownload javadoc;

    public VersionJsonDownload nativeslinux;

    public VersionJsonDownload nativesmacos;

    public VersionJsonDownload nativeswindows;

    public VersionJsonDownload nativeswindows32;

    public VersionJsonDownload nativeswindows64;

    public VersionJsonDownload sources;

    public VersionJsonDownload nativesOsx;

}
