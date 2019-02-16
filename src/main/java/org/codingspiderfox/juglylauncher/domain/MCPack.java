package org.codingspiderfox.juglylauncher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MCPack {
    
       
    private String type;

       
    private String mCVersion;


    private String forgeVersion;
}
