package org.codingspiderfox.juglylauncher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MCAvailablePackVersion {

    private String version;

    private boolean downloadZip;

}
