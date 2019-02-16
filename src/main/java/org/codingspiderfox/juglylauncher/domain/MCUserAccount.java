package org.codingspiderfox.juglylauncher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MCUserAccount {

    private UUID guid;

    private String username;

    private String accessToken;

    private String activeProfile;

    private String clientToken;

    private List<MCUserAccountProfile> profiles = new ArrayList<>();
}
