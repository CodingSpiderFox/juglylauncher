package org.codingspiderfox.juglylauncher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MCAvailablePack {

    private String name;

    private String recommendedVersion;

    private MCAvailablePackVersion[] versions;

}
