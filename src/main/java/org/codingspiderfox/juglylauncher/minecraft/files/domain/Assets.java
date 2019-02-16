package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Assets {
    private Map<String, AssetObject> objects;

    private boolean virtual;

    public static Assets FromJson(String json) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Assets.class);
    }
}