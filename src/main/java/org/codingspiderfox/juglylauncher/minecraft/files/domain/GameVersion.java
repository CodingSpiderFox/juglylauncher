package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameVersion {
    
    private VersionArguments arguments;

    private VersionAssetIndex assetIndex;

    private String assets;

    private VersionJsonDownloads downloads;

    private String id;

    private List<Library> libraries;

    private Logging logging;

    private String mainClass;

    private String minecraftArguments;

    private long minimumLauncherVersion;

    private OffsetDateTime releaseTime;

    private OffsetDateTime time;

    private String type;

    public static GameVersion FromJson(String json) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, GameVersion.class);
    }
}
