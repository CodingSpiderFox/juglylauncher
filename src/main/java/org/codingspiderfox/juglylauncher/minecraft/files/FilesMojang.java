package org.codingspiderfox.juglylauncher.minecraft.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.codingspiderfox.juglylauncher.internet.DownloadHelper;
import org.codingspiderfox.juglylauncher.internet.Http;
import org.codingspiderfox.juglylauncher.minecraft.files.domain.*;
import org.codingspiderfox.juglylauncher.settings.Configuration;
import org.codingspiderfox.juglylauncher.util.Directory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FilesMojang {
    // remote path
    private final String assetsFileServerURL = "https://resources.download.minecraft.net";
    private final String versionManifestURL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";

    @Getter
    @Setter
    private String libraryDir;
    @Getter
    @Setter
    private String versionDir;
    @Getter
    @Setter
    private String nativesDir;
    @Getter
    @Setter
    private String assetsDir;
    @Getter
    @Setter
    private boolean offlineMode;

    private GameVersionManifest _versions = null;

    private DownloadHelper downloadHelper;

    public FilesMojang(DownloadHelper downloadHelper) {
        this.downloadHelper = downloadHelper;
    }

    private void GetVersionManifest() throws IOException {
        try {
            String sVersionManifest = Http.get(versionManifestURL);


            ObjectMapper objectMapper = new ObjectMapper();
            _versions = objectMapper.readValue(sVersionManifest, GameVersionManifest.class);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<String> GetVersions(boolean bSnapshots, boolean bBeta, boolean bAlpha) {
        List<String> versions = new ArrayList<>();

        try {
            if (_versions == null) GetVersionManifest();

            for (VersionsVersion version : _versions.getVersions()) {
                switch (version.getType()) {
                    case Snapshot:
                        if (bSnapshots == true) versions.add(version.getId());
                        break;
                    case OldBeta:
                        if (bBeta == true) versions.add(version.getId());
                        break;
                    case OldAlpha:
                        if (bAlpha == true) versions.add(version.getId());
                        break;
                    default:
                        versions.add(version.getId());
                        break;
                }
            }
            return versions;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private VersionsVersion GetVersion(String sVersion) throws Exception {

        VersionsVersion oVersion = null;

        try {
            if (_versions == null) GetVersionManifest();

            for (VersionsVersion version : _versions.getVersions()) {
                if (version.getId().equals(sVersion)) {
                    oVersion = version;
                    break;
                }
            }
            // throw execption when version not found
            if (oVersion == null) throw new Exception("Minecraft version not found.");

            //return verison object
            return oVersion;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void DownloadVersionJson(String mcversion) throws Exception {
        try {
            // create directory if not exists
            if (!Directory.Exists(versionDir + "/" + mcversion)) {
                Directory.CreateDirectory(versionDir + "/" + mcversion);
            }

            VersionsVersion version = GetVersion(mcversion);

            // delete and download json
            if (File.Exists(versionDir + "/" + mcversion + "/" + mcversion + ".json")) {
                File.Delete(versionDir + "/" + mcversion + "/" + mcversion + ".json");
            }

            downloadHelper.downloadFileTo(version.getUrl(), versionDir + "/" + mcversion + "/" + mcversion + ".json", false, null, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Dictionary<String, String> DownloadClientLibraries(GameVersion MC) throws IOException {
        Configuration c = new Configuration();
        String sJavaArch = c.getJavaArch();

        Dictionary<String, String> ClassPath = new Hashtable<>(); // Library list for startup

        for (Library lib : MC.getLibraries()) {
            VersionJsonDownload download;

            // skip non windows libraries
            if (lib.getRules() != null) {
                boolean bWindows = false;
                for (LibraryRule Rule : lib.getRules()) {
                    if (Rule.Action == "allow") {
                        if (Rule.Os == null) bWindows = true;
                        else if (Rule.Os.Name == null || Rule.Os.Name == "windows") bWindows = true;
                    }
                    if (Rule.Action == "disallow" && Rule.Os.Name == "windows") bWindows = false;
                }
                if (bWindows == false) continue;
            }

            // Natives ?
            if (lib.getNatives() != null) {
                download = lib.getDownloads().getClassifiers().GetType().GetProperty(lib.getNatives().getWindows()
                        .replace("${arch}", sJavaArch).replace("-", "")).GetValue(lib.getDownloads().getClassifiers(), null);
            } else {
                download = lib.getDownloads().getArtifact();
            }
            download.setPath(libraryDir + "/" + download.getPath().replace("/", "\\"));

            downloadHelper.downloadFileTo(download.getUrl(), download.getPath());

            // extract pack if needed
            if (lib.getExtract() != null) {
                if (!Directory.Exists(nativesDir + "/" + MC.getId()))
                    Directory.CreateDirectory(nativesDir + "/" + MC.getId());
                downloadHelper.extractZipFiles(download.getPath(), nativesDir + "/" + MC.getId());
            } else {
                //lLibraries.Add(download.Path); // files needed for startup
                String[] libname = lib.getName().split("\\:");

                //natives could lead to already exists keys
                if (lib.getNatives() != null) {
                    libname[1] += "-native";
                }
                ClassPath.put(libname[0] + ":" + libname[1], download.getPath());
            }
        }
        return ClassPath;
    }

    public void DownloadClientAssets(GameVersion MC) {
        // get assetIndex Json
        downloadHelper.downloadFileTo(MC.getAssetIndex().getUrl(), assetsDir + "/indexes/" + MC.getAssetIndex().getId() + ".json", true, null, MC.getAssetIndex().getSha1());

        // load assetIndex Json File
        Assets assets = Assets.FromJson(File.ReadAllText(assetsDir + @ "\indexes\" + MC.AssetIndex.Id + ".json
        ").Trim());

        for (Set<String, AssetObject> Asset : assets.getObjects()) {
            String sRemotePath = assetsFileServerURL + "/" + Asset.Value.Hash.SubString(0, 2) + "/" + Asset.Value.Hash;
            String sLocalPath = assetsDir + @ "\objects\" + Asset.Value.Hash.SubString(0, 2) + " / " + Asset.Value.Hash;

            if (!Directory.Exists(sLocalPath.SubString(0, sLocalPath.LastIndexOf( @
            "\")))) Directory.CreateDirectory(sLocalPath.SubString(0, sLocalPath.LastIndexOf(@"\")));

            // Download the File
            downloadHelper.downloadFileTo(sRemotePath, sLocalPath, true, null, null);

            if (assets.isVirtual() == true) {
                String slegacyPath = assetsDir + @ "\virtual\legacy\" + Asset.Key.Replace(" / ", @"\");
                if (!Directory.Exists(slegacyPath.SubString(0, slegacyPath.LastIndexOf( @
                "\")))) Directory.CreateDirectory(slegacyPath.SubString(0, slegacyPath.LastIndexOf(@"\")));
                File.Copy(sLocalPath, slegacyPath, true);
            }
        }
    }

    public void DownloadClientJar(GameVersion MC) throws Exception {
        boolean download = false;
        long filesize;
        String fileSHA;

        try {
            if (File.Exists(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar")) {
                // check filesize
                filesize = new FileInfo(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar").Length;
                if (MC.getDownloads().getClient().getSize() != filesize) {
                    File.Delete(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar");
                    download = true;
                }

                // check SHA
                fileSHA = downloadHelper.ComputeHashSHA(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar");
                if (!MC.Downloads.Client.Sha1.Equals(fileSHA)) {
                    File.Delete(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar");
                    download = true;
                }
            } else download = true;

            // download jar
            if (download == true) {
                downloadHelper.downloadFileTo(MC.getDownloads().getClient().getUrl(), versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar", true, null, null);
            }

            // post download check
            // check filesize
            filesize = new FileInfo(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar").Length;
            if (MC.getDownloads().getClient().getSize() != filesize) {
                throw new Exception("Error downloading file: " + MC.getId() + ".jar (filesize mismatch)");
            }

            // check SHA
            fileSHA = downloadHelper.computeHashSHA(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar");
            if (!MC.getDownloads().getClient().getSha1().equals(fileSHA)) {
                throw new Exception("Error downloading file: " + MC.getId() + ".jar (SHA1 mismatch)");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void DownloadServerJar(GameVersion MC, String localPath =null) throws Exception {
        boolean download = false;
        long filesize;
        String fileSHA;
        String localFilePath = versionDir + "/" + MC.getId();

        // overwrite download Path
        if (localPath != null) {
            localFilePath = localPath;
        }

        try {
            if (File.Exists(localFilePath + "/" + MC.getId() + ".jar")) {
                // check filesize
                filesize = new FileInfo(localFilePath + "/" + MC.getId() + ".jar").Length;
                if (MC.getDownloads().getServer().getSize() != filesize) {
                    File.Delete(localFilePath + "/" + MC.getId() + ".jar");
                    download = true;
                }

                // check SHA
                fileSHA = downloadHelper.computeHashSHA(localFilePath + "/" + MC.getId() + ".jar");
                if (!MC.getDownloads().getServer().getSha1().equals(fileSHA)) {
                    File.Delete(localFilePath + "/" + MC.getId() + ".jar");
                    download = true;
                }
            } else download = true;

            // download jar
            if (download == true) {
                downloadHelper.downloadFileTo(MC.getDownloads().getServer().getUrl(), localFilePath + "/" + MC.getId() + ".jar", true, null, null);
            }

            // post download check
            // check filesize
            filesize = new FileInfo(localFilePath + "/" + MC.getId() + ".jar").Length;
            if (MC.getDownloads().getServer().getSize() != filesize) {
                throw new Exception("Error downloading file: " + MC.getId() + ".jar (filesize mismatch)");
            }

            // check SHA
            fileSHA = downloadHelper.computeHashSHA(localFilePath + "/" + MC.getId() + ".jar");
            if (!MC.getDownloads().getServer().getSha1().equals(fileSHA)) {
                throw new Exception("Error downloading file: " + MC.getId() + ".jar (SHA1 mismatch)");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}