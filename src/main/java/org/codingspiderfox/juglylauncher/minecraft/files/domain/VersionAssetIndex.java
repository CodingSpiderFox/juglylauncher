package org.codingspiderfox.juglylauncher.minecraft.files.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionAssetIndex {

    public String Id;

    public String Sha1;

    public long Size;

    public long TotalSize;

    public URL Url;
}
