package org.codingspiderfox.juglylauncher.util;

public class RegistryKey {


    public static RegistryKey OpenBaseKey(RegistryHive localMachine, RegistryView registry32) {

        return new RegistryKey();
    }

    public RegistryKey OpenSubKey(Object o) {

        return new RegistryKey();
    }

    public String GetValue(String javaHome, Object o) {

        return "";

    }


    public String[] GetSubKeyNames() {

        return new String[0];
    }

    public void SetValue(String sRegKey, int iRegValue, RegistryValueKind dWord) {


    }
}
