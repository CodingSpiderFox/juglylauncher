package org.codingspiderfox.juglylauncher.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MCPack {
    
       
    private String type;

       
    private String mCVersion;


    private String forgeVersion;

    public static MCPack FromJson(String json) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, MCPack.class);
    }
}
