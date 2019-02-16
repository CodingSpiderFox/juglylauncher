package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionJsonDownload {

    private String sha1;

    private long size;

    private URL url;

    private String path;

}
