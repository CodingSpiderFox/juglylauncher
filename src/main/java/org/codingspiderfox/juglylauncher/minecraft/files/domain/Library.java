package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Library {

    private LibraryDownloads downloads;

    private String Name;

    private Natives natives;

    private Extract extract;

    private LibraryRule[] rules;
}
