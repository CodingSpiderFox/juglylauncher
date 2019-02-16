package org.codingspiderfox.juglylauncher.minecraft.files;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.codingspiderfox.juglylauncher.internet.DownloadHelper;
import org.codingspiderfox.juglylauncher.minecraft.files.domain.*;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class FilesForge {

    private final String sForgeTree = "/net/minecraftforge/forge/";
    private final String sForgeMaven = "https://files.minecraftforge.net/maven";
    private DownloadHelper downloadHelper;
    private String sForgeVersion;
    private boolean post_1_13;

    @Getter
    @Setter
    private String libraryDir;

    @Getter
    @Setter
    private boolean offlineMode;

    public FilesForge(DownloadHelper downloadHelper) {
        this.downloadHelper = downloadHelper;
    }

    public Dictionary<String, String> installForge(String sForgeVersion) throws IOException {
        this.sForgeVersion = sForgeVersion;

        Dictionary<String, String> ClassPath = new Hashtable<>(); // Library list for startup
        String localLibraryPath = libraryDir + sForgeTree.replace('/', '\\') + sForgeVersion;
        String localFilePath = localLibraryPath + "forge-" + sForgeVersion + "-installer.jar";
        String remoteFile = sForgeMaven + sForgeTree + sForgeVersion + "/forge-" + sForgeVersion + "-installer.jar";

        //check if file exists
        File localFile = new File(localFilePath);
        if (!localFile.exists()) {
            downloadHelper.downloadFileTo(remoteFile, localFilePath, true, null, null);
        }

        // always extract files
        List<String> extractList = Arrays.asList(
                "install_profile.json",
                "version.json",
                "forge-" + sForgeVersion + "-universal.jar"
        );

        downloadHelper.extractZipFiles(localFilePath, localLibraryPath, extractList);

        // post 1.13 files
        File versionDotJsonFile = new File(libraryDir + sForgeTree.replace('/', '\\') + sForgeVersion + "/version.json");
        if (versionDotJsonFile.exists()) {
            post_1_13 = true;

            String fileContents = FileUtils.readFileToString(versionDotJsonFile, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> map = mapper.readValue(json, Map.class);

            ForgeVersion MCForge = ForgeVersion.FromJson(File.ReadAllText(libraryDir + sForgeTree.replace('/', '\\') + sForgeVersion +
            "/version.json").trim());
            // download Forge libraries
            ClassPath = DownloadForgeLibraries(MCForge);
        }

        // pre 1.13 files
        File installProfileJsonFile = new File(libraryDir + sForgeTree.replace('/', '\\') + sForgeVersion + "/install_profile.json");
        else if (installProfileJsonFile.exists())
        {
            post_1_13 = false;
            ForgeInstaller MCForge = ForgeInstaller.FromJson(File.ReadAllText(libraryDir + sForgeTree.replace('/', '\\') + sForgeVersion +
            "/install_profile.json").trim());
            // download Forge libraries
            ClassPath = DownloadForgeLibraries(MCForge);
        }

        // append Forge to classpath
        ClassPath.put("net.minecraftforge:forge", localLibraryPath + "/forge-" + sForgeVersion + "-universal.jar");

        return ClassPath;
    }

    public GameVersion MergeArguments(GameVersion MCMojang) {
        if (post_1_13 == true) {
            ForgeVersion MCForge = ForgeVersion.FromJson(File.ReadAllText(libraryDir + sForgeTree.replace('/', '\\') + sForgeVersion +
            "/version.json").trim());
            // replace vanilla values
            MCMojang.setMainClass(MCForge.getMainClass());
            // append forge arguments
            List<GameElement> itemList = Arrays.asList(MCMojang.getArguments().getGame());
            List<GameElement> moreItems = Arrays.asList(MCForge.getArguments().getGame());
            itemList.addAll(moreItems);
            MCMojang.getArguments().setGame(itemList.toArray(GameElement[]::new));
        } else {
            ForgeInstaller MCForge = ForgeInstaller.FromJson(File.ReadAllText(libraryDir + sForgeTree.replace('/', '\\') + sForgeVersion +
            "/install_profile.json").trim());
            // replace vanilla settings
            MCMojang.setMainClass(MCForge.getVersionInfo().getMainClass());
            MCMojang.setMinecraftArguments(MCForge.getVersionInfo().getMinecraftArguments());
        }
        return MCMojang;
    }

    private Dictionary<String, String> DownloadForgeLibraries(ForgeInstaller Forge) throws IOException {
        Dictionary<String, String> ClassPath = new Hashtable<>(); // Library list for startup

        for(ForgeInstallerLibrary Lib : Forge.getVersionInfo().getLibraries())
        {
            String sLocalPath = libraryDir;
            String sRemotePath = "https://libraries.minecraft.net/";
            String sLibPath = null;
            String[] sLibName = Lib.getName().split("\\:");

            // use download url from Forge
            if (Lib.getUrl() != null) sRemotePath = Lib.getUrl().getPath();

            // fix for typesafe libraries
            if (sLibName[0].contains("com.typesafe")) {
                sRemotePath = "http://central.maven.org/maven2/";
            }

            sLibPath = String.format("{0}/{1}/{2}/{1}-{2}.jar", sLibName[0].replace('.', '/'), sLibName[1], sLibName[2]);
            sLocalPath += "\\" + sLibPath.replace('/', '\\');
            sRemotePath += sLibPath;

            // dont download Forge itself
            if (!sLibName[0].equals("net.minecraftforge") || !sLibName[1].equals("forge")) {
                downloadHelper.downloadFileTo(sRemotePath, sLocalPath, true, null, null);

                // add to classpath (replace)
                if (((Hashtable<String, String>) ClassPath).contains(sLibName[0] + ":" + sLibName[1])) {
                    ClassPath.remove(sLibName[0] + ":" + sLibName[1]);
                }
                ClassPath.put(sLibName[0] + ":" + sLibName[1], sLocalPath);
            }
        }
        return ClassPath;
    }

    private Dictionary<String, String> DownloadForgeLibraries(ForgeVersion forge) throws IOException {
        Dictionary<String, String> classPath = new Hashtable<>(); // Library list for startup

        for(Library lib : forge.getLibraries())
        {
            String[] sLibName = lib.getName().split("\\:");
            VersionJsonDownload download;

            // dont download Forge itself
            if (sLibName[0].equals("net.minecraftforge") && sLibName[1].equals("forge")) continue;

            download = lib.getDownloads().getArtifact();
            download.setPath(libraryDir + "\\" + download.getPath().replace(" / ", "\\"));

            // fix for typesafe libraries
            if (sLibName[0].contains("org.apache.logging.log4j")) {
                download.setUrl(new URL("http://central.maven.org/maven2/" + sLibName[0].replace('.', '/')
                        + "/" + sLibName[1] + "/" + sLibName[2] + "/" + sLibName[1] + "-" + sLibName[2] + ".jar"));
            }
            downloadHelper.downloadFileTo(download.getUrl(), download.getPath(), true, null, null);

            // fix for modlauncher (api)
            if (sLibName.length == 4 && sLibName[0].equals("cpw.mods") && sLibName[1].equals("modlauncher") && sLibName[3].equals("api")) {
                sLibName[1] += "-api";
                continue;
            }

            // add to classpath (replace)
            classPath.put(sLibName[0] + ":" + sLibName[1], download.getPath());
        }
        return classPath;
    }
}