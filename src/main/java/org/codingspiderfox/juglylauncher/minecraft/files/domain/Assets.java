package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Dictionary;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Assets {
    private Dictionary<String, AssetObject> objects;

    private boolean virtual;
}