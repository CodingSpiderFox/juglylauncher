package org.codingspiderfox.juglylauncher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MCUserAccountProfile {

    private String id;

    private String name;

    private boolean legacy;
}
