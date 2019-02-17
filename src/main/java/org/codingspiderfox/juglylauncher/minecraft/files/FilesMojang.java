package org.codingspiderfox.juglylauncher.minecraft.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codingspiderfox.juglylauncher.internet.DownloadHelper;
import org.codingspiderfox.juglylauncher.internet.Http;
import org.codingspiderfox.juglylauncher.minecraft.files.domain.*;
import org.codingspiderfox.juglylauncher.settings.Configuration;
import org.codingspiderfox.juglylauncher.util.Directory;
import org.codingspiderfox.juglylauncher.util.FileInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
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

    public List<String> GetVersions(boolean bSnapshots, boolean bBeta, boolean bAlpha) throws IOException {
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
            File jsonFile = new File(versionDir + "/" + mcversion + "/" + mcversion + ".json");
            if (jsonFile.exists()) {
                jsonFile.delete();
            }

            downloadHelper.downloadFileTo(version.getUrl(), versionDir + "/" + mcversion + "/" + mcversion + ".json", false, null, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Map<String, String> DownloadClientLibraries(GameVersion MC) throws IOException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        Configuration c = new Configuration();
        String sJavaArch = c.getJavaArch();

        Map<String, String> ClassPath = new Hashtable<>(); // Library list for startup

        for (Library lib : MC.getLibraries()) {
            VersionJsonDownload download;

            // skip non windows libraries
            if (lib.getRules() != null) {
                boolean bWindows = false;
                for (LibraryRule Rule : lib.getRules()) {
                    if (Rule.Action == "allow") {
                        if (Rule.Os == null) bWindows = true;
                        else if (Rule.Os.Name == null || Rule.Os.Name.equals("windows")) bWindows = true;
                    }
                    if (Rule.Action .equals("disallow") && Rule.Os.Name.equals("windows")) bWindows = false;
                }
                if (bWindows == false) continue;
            }

            // Natives ?
            if (lib.getNatives() != null) {

                Class<?> clazz = Class.forName("org.codingspiderfox.juglylauncher.minecraft.files.domain.Classifiers");

                //TODO support windows/osx
                String fieldName = lib.getNatives().getLinux()
                        .replace("${arch}", sJavaArch).replace("-", "");

                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                download = (VersionJsonDownload) field.get(lib.getDownloads().getClassifiers());


            } else {
                download = lib.getDownloads().getArtifact();
            }
            download.setPath(libraryDir + "/" + download.getPath().replace("/", "\\"));

            downloadHelper.downloadFileTo(download.getUrl(), download.getPath(), true, null, null);

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

    public void DownloadClientAssets(GameVersion MC) throws IOException {
        // get assetIndex Json
        downloadHelper.downloadFileTo(MC.getAssetIndex().getUrl(), assetsDir + "/indexes/" + MC.getAssetIndex().getId() + ".json", true, null, MC.getAssetIndex().getSha1());

        // load assetIndex Json File
        String fileContents = FileUtils.readFileToString(
                new File(assetsDir + "/indexes/" + MC.getAssetIndex().Id + ".json"), Charset.forName("UTF-8"));
        Assets assets = Assets.FromJson(fileContents.trim());

        for (Map.Entry<String, AssetObject> Asset : assets.getObjects().entrySet()) {
            String sRemotePath = assetsFileServerURL + "/" + Asset.getValue().getHash().substring(0, 2) + "/" + Asset.getValue().getHash();
            String sLocalPath = assetsDir + "/objects/" + Asset.getValue().getHash().substring(0, 2) + " / " + Asset.getValue().getHash();

            if (!Directory.Exists(sLocalPath.substring(0, sLocalPath.lastIndexOf("/")))) Directory.CreateDirectory(sLocalPath.substring(0, sLocalPath.lastIndexOf("/")));

            // Download the File
            downloadHelper.downloadFileTo(sRemotePath, sLocalPath, true, null, null);

            if (assets.isVirtual() == true) {
                String slegacyPath = assetsDir + "/virtual/legacy/" + Asset.getKey().replace(" / ", "/");
                if (!Directory.Exists(slegacyPath.substring(0, slegacyPath.lastIndexOf("/")))) Directory.CreateDirectory(slegacyPath.substring(0, slegacyPath.lastIndexOf("/")));

            }
        }
    }

    public void downloadClientJar(GameVersion MC) throws Exception {

        boolean download = false;
        long filesize;
        String fileSHA;

        try {
            File jarFile = new File(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar");
            if (jarFile.exists()) {
                // check filesize
                filesize = new FileInfo(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar").getLength();
                if (MC.getDownloads().getClient().getSize() != filesize) {

                    jarFile.delete();
                    download = true;
                }

                // check SHA
                fileSHA = downloadHelper.computeHashSHA(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar");
                if (!MC.getDownloads().getClient().getSha1().equals(fileSHA)) {
                    jarFile.delete();
                    download = true;
                }
            } else download = true;

            // download jar
            if (download == true) {
                downloadHelper.downloadFileTo(MC.getDownloads().getClient().getUrl(),
                        versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar", true, null, null);
            }

            // post download check
            // check filesize
            filesize = new FileInfo(versionDir + "/" + MC.getId() + "/" + MC.getId() + ".jar").getLength();
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

    public void downloadServerJar(GameVersion MC, String localPath) throws Exception {

        boolean download = false;
        long filesize;
        String fileSHA;
        String localFilePath = versionDir + "/" + MC.getId();

        // overwrite download Path
        if (localPath != null) {
            localFilePath = localPath;
        }

        try {
            File jarFile = new File(localFilePath + "/" + MC.getId() + ".jar");
            if (jarFile.exists()) {
                // check filesize
                filesize = new FileInfo(localFilePath + "/" + MC.getId() + ".jar").getLength();
                if (MC.getDownloads().getServer().getSize() != filesize) {
                    jarFile.delete();
                    download = true;
                }

                // check SHA
                fileSHA = downloadHelper.computeHashSHA(localFilePath + "/" + MC.getId() + ".jar");
                if (!MC.getDownloads().getServer().getSha1().equals(fileSHA)) {
                    jarFile.delete();
                    download = true;
                }
            } else download = true;

            // download jar
            if (download == true) {
                downloadHelper.downloadFileTo(MC.getDownloads().getServer().getUrl(),
                        localFilePath + "/" + MC.getId() + ".jar", true, null, null);
            }

            // post download check
            // check filesize
            filesize = new FileInfo(localFilePath + "/" + MC.getId() + ".jar").getLength();
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