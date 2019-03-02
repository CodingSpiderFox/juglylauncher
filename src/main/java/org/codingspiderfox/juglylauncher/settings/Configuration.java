package org.codingspiderfox.juglylauncher.settings;

import org.codingspiderfox.juglylauncher.util.RegistryHive;
import org.codingspiderfox.juglylauncher.util.RegistryKey;
import org.codingspiderfox.juglylauncher.util.RegistryValueKind;
import org.codingspiderfox.juglylauncher.util.RegistryView;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private final String sJavaExecutable = "\\bin\\javaw";
    private final String sRegPath = "Software\\Minestar\\UglyLauncher";
    private String sJavaPath = null;
    private String sJavaArch = null;

    private int iMinMemory = -1;
    private int iMaxMemory = -1;
    private int iConsole_Show = -1;
    private int iConsole_Keep = -1;
    private int iCloseLauncher = -1;
    private int iUseGC = -1;
    private String sJavaVersion = null;


    // contructor
    public Configuration() {
        iMinMemory = GetRegInt("min_memory");
        iMaxMemory = GetRegInt("max_memory");
        iConsole_Show = GetRegInt("show_console");
        iConsole_Keep = GetRegInt("keep_console");
        iCloseLauncher = GetRegInt("close_Launcher");
        iUseGC = GetRegInt("use_gc");
        sJavaVersion = GetRegString("java_version");
        GetJavaPathAuto(getJavaVersion());
    }

    // Java search methode
    public String getJavaVersion() {
        if (sJavaVersion != null) return sJavaVersion;
        else {
            return SetRegString("java_version", "auto");
        }
    }

    public void setsJavaVersion(String value) {
        sJavaVersion = value;
        SetRegString("java_version", sJavaVersion);
    }


    // Minimum memoryusage
    public int getMinimumMemory() {

        if (iMinMemory != -1) return iMinMemory;
        else return SetRegInt("min_memory", 512);
    }

    public void getMinimumMemory(int value) {
        iMinMemory = value;
        SetRegInt("min_memory", iMinMemory);
    }


    // Maximum memoryusage
    public int getMaximumMemory() {

        if (iMaxMemory != -1) return iMaxMemory;
        else return SetRegInt("max_memory", 2048);
    }

    public void setMaximumMemory(int value) {
        iMaxMemory = value;
        SetRegInt("max_memory", iMaxMemory);
    }


    // Garbage Collector
    public int getUseGC() {

        if (iUseGC != -1) return iUseGC;
        else return SetRegInt("use_gc", 0);
    }

    public void getUseGC(int value) {
        iUseGC = value;
        SetRegInt("use_gc", iUseGC);
    }


    // show console
    public int getShowConsole() {

        if (iConsole_Show != -1) return iConsole_Show;
        else return SetRegInt("show_console", 0);
    }

    public void setShowConsole(int value) {
        iConsole_Show = value;
        SetRegInt("show_console", iConsole_Show);
    }


    // keep console
    public int getKeepConsole() {

        if (iConsole_Keep != -1) return iConsole_Keep;
        else return SetRegInt("keep_console", 0);
    }

    public void setKeepConsole(int value) {
        iConsole_Keep = value;
        SetRegInt("keep_console", iConsole_Keep);
    }


    // close Launcher
    public int getCloseLauncher() {

        if (iCloseLauncher != -1) return iCloseLauncher;
        else return SetRegInt("close_Launcher", 0);
    }

    public void setCloseLauncher(int value) {
        iCloseLauncher = value;
        SetRegInt("close_Launcher", iCloseLauncher);
    }


    // Old Shit

    public String getJavaArch() {
        return sJavaArch;
    }

    public String GetJavaPath() {
        return sJavaPath;
    }


    public List<String> GetJavaVersions() {
        List<String> lVersions = new ArrayList<>();
        String[] lSubkeys;
        RegistryKey key;
        RegistryKey hklm64 = RegistryKey.OpenBaseKey(RegistryHive.LocalMachine, RegistryView.Registry64);
        RegistryKey hklm32 = RegistryKey.OpenBaseKey(RegistryHive.LocalMachine, RegistryView.Registry32);

        // get 64bit vrsions
        key = hklm64.OpenSubKey("SOFTWARE\\JavaSoft\\Java Runtime Environment");
        if (key != null) {
            lSubkeys = key.GetSubKeyNames();

            for (String version : lSubkeys) {
                if (version.length() == 3) {
                    lVersions.add(version + "_64");
                }
            }
        }
        // get 32bit versions
        key = hklm32.OpenSubKey("SOFTWARE\\JavaSoft\\Java Runtime Environment");
        if (key != null) {
            lSubkeys = key.GetSubKeyNames();

            for (String version : lSubkeys) {
                if (version.length() == 3) {
                    lVersions.add(version + "_32");
                }
            }
        }
        return lVersions;
    }


    private void GetJavaPathAuto(String sVersion) {
        if (sVersion == "auto" || sVersion == null) {
            // Get 64bit Java
            GetJavaPath64(sVersion);
            // if 64bit not found, look for 32bit Java
            if (sJavaPath == null) GetJavaPath32(sVersion);
            // if still no Java Found -> Bullshit
        } else {
            String[] versions = sVersion.split("_");
            if (versions[1] == "64") GetJavaPath64(versions[0]);
            if (versions[1] == "32") GetJavaPath32(versions[0]);
        }
    }

    private void GetJavaPath64(String sVersion) {
        RegistryKey key;
        var hklm64 = RegistryKey.OpenBaseKey(RegistryHive.LocalMachine, RegistryView.Registry64);

        if (sVersion == "auto") {
            key = hklm64.OpenSubKey("SOFTWARE\\JavaSoft\\Java Runtime Environment");
            if (key == null) return;  // no java 64 found
            sVersion = key.GetValue("CurrentVersion", null);
        }

        key = hklm64.OpenSubKey("SOFTWARE\\JavaSoft\\Java Runtime Environment\\" + sVersion);
        if (key == null) return;  // no java 64 found
        sJavaPath = key.GetValue("JavaHome", null);
        // append executable
        sJavaPath += sJavaExecutable;
        sJavaArch = "64";
    }

    private void GetJavaPath32(String sVersion) {
        RegistryKey key;
        RegistryKey hklm32 = RegistryKey.OpenBaseKey(RegistryHive.LocalMachine, RegistryView.Registry32);

        if (sVersion == "auto") {
            key = hklm32.OpenSubKey("SOFTWARE\\JavaSoft\\Java Runtime Environment");
            if (key == null) return;  // no java 32 found
            sVersion = key.GetValue("CurrentVersion", null);
        }

        key = hklm32.OpenSubKey("SOFTWARE\\JavaSoft\\Java Runtime Environment\\" + sVersion);
        if (key == null) return;  // no java 32 found
        sJavaPath = key.GetValue("JavaHome", null);
        // append executable
        sJavaPath += sJavaExecutable;
        sJavaArch = "32";
    }

    // Registry Handler
    private String GetRegString(String sRegKey) {
        /*RegistryKey key = Registry.CurrentUser.OpenSubKey(sRegPath);
        if (key == null) return null;
        return key.GetValue(sRegKey, null);
        */
        return "";
    }

    private int GetRegInt(String sRegKey) {
        /*RegistryKey key = Registry.CurrentUser.OpenSubKey(sRegPath);
        if (key == null) return -1;
        return Integer.parseInt(key.GetValue(sRegKey, -1));*/
        return -1;
    }

    private String SetRegString(String sRegKey, String sRegValue) {
        /*RegistryKey key = Registry.CurrentUser.CreateSubKey(sRegPath);
        key.SetValue(sRegKey, sRegValue, RegistryValueKind.String);
        return sRegValue;*/
        return "";
    }

    private int SetRegInt(String sRegKey, int iRegValue) {
        /*RegistryKey key = Registry.CurrentUser.CreateSubKey(sRegPath);
        key.SetValue(sRegKey, iRegValue, RegistryValueKind.DWord);
        return iRegValue;*/
        return -1;
    }
}