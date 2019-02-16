package org.codingspiderfox.juglylauncher.accountmanager;

import org.codingspiderfox.juglylauncher.domain.MCUser;
import org.codingspiderfox.juglylauncher.domain.MCUserAccount;
import org.codingspiderfox.juglylauncher.domain.MCUserAccountProfile;
import org.codingspiderfox.juglylauncher.minecraft.Launcher;


import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.UnknownServiceException;
import java.util.UUID;

public class Manager
{
    private final String xmlfile = "\\users.xml";
    private MCUser Users = new MCUser();

    // contructor
    public Manager()
    {
        File xmlFile = new File(Launcher._sDataDir + xmlfile);
        if (!xmlFile.exists()) {CreateXML() ;}
        else LoadXML();
    }

    // load XML from file
    private void LoadXML()
    {

        XMLDecoder decoder=null;
        try {
            decoder=new XMLDecoder(new BufferedInputStream(new FileInputStream(Launcher._sDataDir + xmlfile)));
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File dvd.xml not found");
        }
        Users=(MCUser) decoder.readObject();
    }

    // save XML to file
    private void SaveXML() {

        try {
            XMLEncoder encoder = null;
            try {
                encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(Launcher._sDataDir + xmlfile)));
            } catch (FileNotFoundException fileNotFound) {
                System.out.println("ERROR while serializing/writing XML file");
            }
            encoder.writeObject(Users);
            encoder.close();
        } catch (Exception ex) {

        }
    }

    // create empty XML
    private void CreateXML()
    {
        Users.setActiveAccount(null);
        SaveXML();
    }

    // get all accounts
    public MCUser GetAccounts()
    {
        return Users;
    }

    // get one account
    public MCUserAccount GetAccount(UUID iAccountId)
    {
        for (MCUserAccount Account : Users.getAccounts())
        if (Account.getGuid() == iAccountId) return Account;
        return null;
    }

    // get num accounts
    public int GetNumAccounts()
    {
        return Users.getAccounts().size();
    }

    // get default account
    public UUID GetDefault()
    {
        return Users.getActiveAccount();
    }

    // set default account
    public void SetDefault(UUID iAccountId)
    {
        Users.setActiveAccount(iAccountId);
        SaveXML();
    }

    // get active profile from account
    public MCUserAccountProfile GetActiveProfile(MCUserAccount Account)
    {
        for (MCUserAccountProfile Profile : Account.getProfiles())
        if (Profile.getId() == Account.getActiveProfile()) return Profile;
        return null;
    }

    // delete account
    public void DeleteAccount(UUID iAccountId)
    {
        MCUserAccount Account = GetAccount(iAccountId);
        Users.getAccounts().remove(Account);
        if (Users.getActiveAccount() == iAccountId) Users.setActiveAccount(null);
        SaveXML();
    }

    // get ingame player name
    public String GetPlayerName(UUID iAccountId)
    {
        MCUserAccountProfile Profile = GetActiveProfile(GetAccount(iAccountId));
        return Profile.getName();
    }

    // get minecraft Profile ID
    public String GetMCProfileID(UUID iAccountId)
    {
        MCUserAccountProfile Profile = GetActiveProfile(GetAccount(iAccountId));
        return Profile.getId();
    }

    public void AddAccount(MCUserAccount Account)
    {
        Users.getAccounts().add(Account);
        SaveXML();
    }

    public void SaveAccount(MCUserAccount Account)
    {
        // this needs a better way
        boolean bWasDefault = false;
        if (Account.getGuid() == Users.getActiveAccount()) bWasDefault = true;

        DeleteAccount(Account.getGuid());
        AddAccount(Account);

        if (bWasDefault) SetDefault(Account.getGuid());
    }
}
