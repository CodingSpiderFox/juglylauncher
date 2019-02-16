package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionsVersion {


    private String id;


    private TypeEnum type;


    private URL url;


    private OffsetDateTime time;


    private OffsetDateTime releaseTime;
}
