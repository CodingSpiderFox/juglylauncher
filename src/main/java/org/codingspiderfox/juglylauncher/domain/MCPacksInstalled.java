package org.codingspiderfox.juglylauncher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MCPacksInstalled {


    public List<MCPacksInstalledPack> packs = new ArrayList<>();
}
