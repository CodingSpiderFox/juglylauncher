package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgeVersion {

    private ForgeArguments arguments;

    private String id;

    private List<Library> libraries;

    private String mainClass;

    private OffsetDateTime releaseTime;

    private OffsetDateTime time;

    private String type;

    private GameElement[] game;
}