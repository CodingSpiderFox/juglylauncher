package org.codingspiderfox.juglylauncher.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MCAvailablePacks {

    private MCAvailablePack[] packs;

    public static MCAvailablePacks fromJson (String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, MCAvailablePacks.class)
    }
}
