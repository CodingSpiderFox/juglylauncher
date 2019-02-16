package org.codingspiderfox.juglylauncher.minecraft;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingspiderfox.juglylauncher.accountmanager.Manager;
import org.codingspiderfox.juglylauncher.domain.*;
import org.codingspiderfox.juglylauncher.internet.DownloadHelper;
import org.codingspiderfox.juglylauncher.internet.Http;
import org.codingspiderfox.juglylauncher.minecraft.files.domain.GameElement;
import org.codingspiderfox.juglylauncher.minecraft.files.domain.GameVersion;
import org.codingspiderfox.juglylauncher.minecraft.files.domain.JvmElement;
import org.codingspiderfox.juglylauncher.minecraft.files.domain.JvmRule;
import org.codingspiderfox.juglylauncher.settings.Configuration;
import org.codingspiderfox.juglylauncher.util.Directory;
import org.codingspiderfox.juglylauncher.util.DirectoryInfo;
import org.codingspiderfox.juglylauncher.util.Environment;
import org.codingspiderfox.juglylauncher.util.FileInfo;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

class Launcher
{
    // events
    //public event EventHandler<FormWindowStateEventArgs> RestoreWindow;
    // objects
    //private FrmConsole _console;
    private static MCAvailablePacks PacksAvailable = new MCAvailablePacks();
    private static MCPacksInstalled PacksInstalled = new MCPacksInstalled();
    // Strings
    public static final String _sPackServerBaseURL = "http://uglylauncher.de";
    public static final String _sDataDir = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + "/.UglyLauncher";
    public static final String _sPacksDir = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + "/.UglyLauncher/packs";
    public final String _sAssetsDir = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + "/.minecraft/assets";
    public final String _sLibraryDir = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + "/.minecraft/libraries";
    public final String _sVersionDir = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + "/.minecraft/versions";
    public final String _sNativesDir = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + "/.UglyLauncher/natives";
    // boolean
    private final boolean offline = false;
    // Lists


    private DownloadHelper downloadHelper;


    // constructor
    public Launcher(boolean OfflineMode)
    {
        downloadHelper = new DownloadHelper();
        offline = OfflineMode;
    }

    // Open Pack folder
    public void OpenPackFolder(String sSelectedPack)
    {
        if(Directory.Exists(_sPacksDir + "/" + sSelectedPack)) Process.Start(_sPacksDir + "/" + sSelectedPack);
    }

    // Check Directories
    public void CheckDirectories()
    {
        if (!Directory.Exists(_sDataDir)) Directory.CreateDirectory(_sDataDir);
        if (!Directory.Exists(_sLibraryDir)) Directory.CreateDirectory(_sLibraryDir);
        if (!Directory.Exists(_sAssetsDir)) Directory.CreateDirectory(_sAssetsDir);
        if (!Directory.Exists(_sAssetsDir + "/indexes")) Directory.CreateDirectory(_sAssetsDir + "/indexes");
        if (!Directory.Exists(_sAssetsDir + "/objects")) Directory.CreateDirectory(_sAssetsDir + "/objects");
        if (!Directory.Exists(_sAssetsDir + "/virtual")) Directory.CreateDirectory(_sAssetsDir + "/virtual");
        if (!Directory.Exists(_sVersionDir)) Directory.CreateDirectory(_sVersionDir);
        if (!Directory.Exists(_sPacksDir)) Directory.CreateDirectory(_sPacksDir);
        if (!Directory.Exists(_sNativesDir)) Directory.CreateDirectory(_sNativesDir);
    }

    // load Packlist from server
    public void LoadAvailablePacks(String sPlayerName,String sMCUID) throws IOException {
        try
        {
            String sPackListJson = Http.get(_sPackServerBaseURL + "/packs.php?player=" + sPlayerName + "&uid=" + sMCUID);
            ObjectMapper objectMapper = new ObjectMapper();

            PacksAvailable = MCAvailablePacks.fromJson(sPackListJson);
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }


    public MCAvailablePack GetAvailablePack(String sPackName)
    {
        for (MCAvailablePack Pack:PacksAvailable.getPacks())
        {
            if (Pack.getName() == sPackName)
            {
                return Pack;
            }
        }
        return null;
    }

    // get pack icon
    public Image GetPackIcon(MCAvailablePack Pack) {
        InputStream inputStream;
        try {
        inputStream = Http.downloadToStream(_sPackServerBaseURL + "/packs/" + Pack.getName() + "/" + Pack.getName() + ".png");
        } catch (Exception ex) {
            //TODO error message
        }

        if(Directory.Exists(_sPacksDir + "/" + Pack.getName()))
        {
            FileStream file = new FileStream(_sPacksDir + "/" + Pack.getName() + "/" + Pack.getName() + ".png", FileMode.Create, FileAccess.Write);
            inputStream.WriteTo(file);
        }

        return Image.FromStream(inputStream);
    }

    // get pack icon
    public Image GetPackIconOffline(MCPacksInstalledPack Pack)
    {
        if (!File.Exists(_sPacksDir + "/" + Pack.Name + "/" + Pack.Name + ".png")) return null;

        MemoryStream ms = new MemoryStream();

        FileStream fileStream = File.OpenRead(_sPacksDir + "/" + Pack.Name + "/" + Pack.Name + ".png");
        ms.SetLength(fileStream.Length);
        //read file to MemoryStream
        fileStream.Read(ms.GetBuffer(), 0, (int)fileStream.Length);

        return Image.FromStream(ms);
    }

    // Get installes packages
    public void LoadInstalledPacks()
    {
        List<String> dirs = new ArrayList<String>(Directory.EnumerateDirectories(_sPacksDir));
        PacksInstalled = new MCPacksInstalled();
        for (var dir:dirs)
        {
            if (File.Exists(dir + "/version") && File.Exists(dir + "/pack.json"))
            {
                MCPacksInstalledPack pack = new MCPacksInstalledPack();
                pack.setName(dir.SubString(dir.LastIndexOf("//") + 1));
                pack.setCurrentVersion(File.ReadAllText(dir + "/version").Trim());

                if (File.Exists(dir + "/selected")) pack.getSelectedVersion() = File.ReadAllText(dir + "/selected").Trim();
                    else pack.getSelectedVersion() = "recommended";
                PacksInstalled.packs.add(pack);
            }
        }
    }

    // Get pack liste
    public MCPacksInstalled GetInstalledPacks()
    {
        return PacksInstalled;
    }

    // get installed Pack
    public MCPacksInstalledPack GetInstalledPack(String sPackName)
    {
        for (MCPacksInstalledPack Pack:PacksInstalled.packs)
        if (Pack.getName().equals(sPackName)) return Pack;
        return null;
    }

    // Check if Pack is Installed (and the right version)
    public boolean IsPackInstalled(String sPackName, String sPackVersion)
    {
        // get pack
        MCPacksInstalledPack Pack = GetInstalledPack(sPackName);
        // return false if pack not found
        if (Pack == null) return false;
        // return true if no version is given
        if (sPackVersion == null) return true;
        // check version of installed Pack
        // if recommended version, getting the version from available packs
        if (sPackVersion.equals("recommended")) sPackVersion = GetRecommendedVersion(sPackName);
        // check if version is installed
        if (!Pack.getCurrentVersion().equals(sPackVersion)) return false;
        // Pack is fine :)
        return true;
    }

    // Get ModFolderContents
    public List<String> GetModFolderContents(String sPackname, ArrayList<String> sFileExtensions)
    {
        List<String> Mods = new List<String>();
        try
        {
            String sModsPath = String.format(@"{0}/{1}/minecraft/mods/", _sPacksDir, sPackname);
            Mods =  Directory.EnumerateFiles(sModsPath, "*.*")
                    .Where(f => sFileExtensions.Contains(Path.GetExtension(f).ToLower()))
                    .ToList();
        }
        catch (Exception ex)
        {
            MessageBox.Show("Kein Mod Verzeichniss gefunden. Vanilla ?", "kein Modverzeichniss", MessageBoxButtons.OK,MessageBoxIcon.Error);
            throw ex;
        }

        return Mods;
    }

    // Get mcmod.info file as String
    public String GetMcModInfo(String sFileName)
    {
        FileStream fs = new FileStream(sFileName, FileMode.Open, FileAccess.Read);
        ZipFile zf = new ZipFile(fs);
        ZipEntry ze = zf.GetEntry("mcmod.info");
        String result = null;
        byte[] ret = null;
        if (ze != null)
        {
            Stream s = zf.GetInputStream(ze);
            ret = new byte[ze.Size];
            s.Read(ret, 0, ret.Length);
            result = System.Text.Encoding.UTF8.GetString(ret).Trim();
        }
        zf.Close();
        fs.Close();

        return result;
    }

    public String GetRecommendedVersion(String sPackName)
    {
        if (offline == false)
        {
            MCAvailablePack Pack = GetAvailablePack(sPackName);
            return Pack.RecommendedVersion;
        }

        else
        {
            MCPacksInstalledPack Pack = GetInstalledPack(sPackName);
            return Pack.CurrentVersion;
        }

    }

    public void SetSelectedVersion(String sPackName, String sVersion)
    {
        File.WriteAllText(_sPacksDir + "/" + sPackName + "/selected", sVersion);
        Application.DoEvents(); // wait a little bit :)
        LoadInstalledPacks();
    }

    public String GetInstalledPackVersion(String sPackName)
    {
        MCPacksInstalledPack Pack = GetInstalledPack(sPackName);
        return Pack.getSelectedVersion();
    }

    public void StartPack(String sPackName, String sPackVersion) throws Exception {
        Dictionary<String, String> ClassPath = new Hashtable<>(); // Library list for startup

        FilesMojang MCMojangFiles = new FilesMojang(downloadHelper);
        MCMojangFiles.setLibraryDir(_sLibraryDir);
        MCMojangFiles.setVersionDir(_sVersionDir);
        MCMojangFiles.setNativesDir(_sNativesDir);
        MCMojangFiles.setAssetsDir(_sAssetsDir);
        MCMojangFiles.setOfflineMode(offline);

        // check if pack is installed with given version
        if (!IsPackInstalled(sPackName, sPackVersion))
        {
            InstallPack(sPackName, sPackVersion);
        }

        // getting pack json file
        MCPack pack = MCPack.FromJson(File.ReadAllText(_sPacksDir + "/" + sPackName + "/pack.json").Trim());
        // vanilla Minecraft
        MCMojangFiles.DownloadVersionJson(pack.getMCVersion());
        GameVersion MCMojang = GameVersion.FromJson(File.ReadAllText(_sVersionDir + "/" + pack.getMCVersion() + "/" + pack.getMCVersion() + ".json").Trim());
        // download game jar
        MCMojangFiles.DownloadClientJar(MCMojang);
        // download libraries if needed
        ClassPath = MCMojangFiles.DownloadClientLibraries(MCMojang);
        // download assets if needed
        MCMojangFiles.DownloadClientAssets(MCMojang);

        // additional things for forge
        if (pack.getType().equals("forge"))
        {
            Dictionary<String, String> ForgeClassPath = new Dictionary<String, String>(); // Library list for startup
            FilesForge MCForgeFiles = new FilesForge(downloadHelper);
            MCForgeFiles.setLibraryDir(_sLibraryDir);
            MCForgeFiles.setOfflineMode(offline);

            // Install Forge
            ForgeClassPath = MCForgeFiles.installForge(pack.getForgeVersion());

            //Merge Classpath
            for (KeyValuePair<String, String> entry:ForgeClassPath)
            {
                if (ClassPath.ContainsKey(entry.Key))
                {
                    ClassPath.Remove(entry.Key);
                }
                ClassPath.Add(entry.Key, entry.Value);
            }

            // Merge startup parameter
            MCMojang = MCForgeFiles.MergeArguments(MCMojang);
        }

        // set selected version
        SetSelectedVersion(sPackName, sPackVersion);
        // start the pack
        Start(BuildArgs(MCMojang, sPackName, ClassPath), sPackName);
        // close bar if open
        if (downloadHelper.isBarVisible() == true) downloadHelper.hideBar();
        //if (_bar.Visible == true) _bar.Hide();
    }

    private void Start(String args,String sPackName)
    {

        Configuration C = new Configuration();
        Process minecraft = new Process();

        // check for "minecraft" folder
        if (!Directory.Exists(_sPacksDir + "/" + sPackName + "/minecraft")) Directory.CreateDirectory(_sPacksDir + "/" + sPackName + "/minecraft");

        minecraft.StartInfo.FileName = C.GetJavaPath();
        minecraft.StartInfo.WorkingDirectory = _sPacksDir + "/" + sPackName + "/minecraft";
        minecraft.StartInfo.Arguments = args;
        minecraft.StartInfo.RedirectStandardOutput = true;
        minecraft.StartInfo.RedirectStandardError = true;
        minecraft.StartInfo.UseShellExecute = false;
        minecraft.StartInfo.CreateNoWindow = true;
        minecraft.OutputDataReceived += new DataReceivedEventHandler(Minecraft_OutputDataReceived);
        minecraft.ErrorDataReceived += new DataReceivedEventHandler(Minecraft_ErrorDataReceived);
        minecraft.Exited += new EventHandler(Minecraft_Exited);
        minecraft.EnableRaisingEvents = true;

        // load console
        if (C.getShowConsole() == 1)
        {
            CloseOldConsole();
            _console = new FrmConsole();
            _console.Show();
            _console.Clear();
            _console.AddLine(String.format("UglyLauncher-Version: {0}", Application.ProductVersion),Color.Blue);
            _console.AddLine("Using Java-Version: " + C.GetJavaPath() + " (" + C.GetJavaArch()+ "bit)", Color.Blue);
            _console.AddLine("Startparameter:" + args, Color.Blue);
        }

        // start minecraft
        minecraft.Start();
        minecraft.BeginOutputReadLine();
        minecraft.BeginErrorReadLine();

        // raise event
        EventHandler<FormWindowStateEventArgs> handler = RestoreWindow;
        FormWindowStateEventArgs args2 = new FormWindowStateEventArgs
        {
            WindowState = FormWindowState.Minimized,
                    MCExitCode = -1
        };
        handler?.Invoke(this, args2);
    }

    private void CloseOldConsole()
    {
        FormCollection fc = Application.OpenForms;

        for (Form frm:fc)
        {
            if (frm.Name == "FrmConsole")
            {
                frm.Close();
                return;
            }
        }
    }

    private void Minecraft_ErrorDataReceived(object sender, DataReceivedEventArgs e)
    {
        if (!String.IsNullOrEmpty(e.Data))
        {
            try
            {
                _console.AddLine(e.Data,Color.Red);
            }
            catch (Exception)
            {
            }
        }
    }

    private void Minecraft_Exited(object sender, EventArgs e)
    {
        Configuration C = new Configuration();
        Process minecraft = sender as Process;

        if (C.getKeepConsole() == 0 && minecraft.ExitCode == 0)
        {
            try
            {
                _console.BeginInvoke(new Action(() =>
                        {
                                _console.Dispose();
                        }
                    ));
            }
            catch (Exception)
            {
            }
        }

        // raise event
        EventHandler<FormWindowStateEventArgs> handler = RestoreWindow;
        FormWindowStateEventArgs args = new FormWindowStateEventArgs
        {
            WindowState = FormWindowState.Normal,
                    MCExitCode = minecraft.ExitCode
        };
        handler?.Invoke(this, args);
    }

    private void Minecraft_OutputDataReceived(object sendingProcess, DataReceivedEventArgs outLine)
    {
        if (!String.IsNullOrEmpty(outLine.Data))
        {
            try
            {
                _console.AddLine(outLine.Data,Color.Black);
            }
            catch (Exception)
            {
            }
        }
    }

    private String BuildArgs(GameVersion MC, String sPackName, Dictionary<String, String> ClassPath)
    {
        String args = null;
        String classpath = null;
        Configuration C = new Configuration();
        Manager U = new Manager();
        MCUserAccount Acc = U.GetAccount(U.GetDefault());
        MCUserAccountProfile Profile = U.GetActiveProfile(Acc);

        // Garbage Collector
        if (C.getUseGC() == 1) args += " -XX:SurvivorRatio=2 -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+AggressiveOpts";

        // force 64bit
        if (C.getJavaArch() == "64") args += " -d64";

        // Java Memory
        args += String.format(" -Xms{0}m -Xmx{1}m -Xmn128m", C.getMinimumMemory(), C.getMaximumMemory());

        if (MC.getArguments() != null)
        {
            // JVM
            for(JvmElement jvme:MC.getArguments().getJvm())
            {
                if(jvme.JvmClass != null)
                {
                    // skip non windows libraries
                    if (jvme.JvmClass.Rules != null)
                    {
                        boolean bWindows = false;
                        for (JvmRule Rule:jvme.JvmClass.Rules)
                        {
                            if (Rule.Action == "allow")
                            {
                                if (Rule.Os == null) bWindows = true;
                                else if (Rule.Os.Name == null || Rule.Os.Name == "windows")
                                {
                                    bWindows = true;
                                    // check version
                                    if (Rule.Os.Version != null)
                                    {
                                        String text = Environment.OSVersion.Version.ToString();
                                        Regex r = new Regex(Rule.Os.Version, RegexOptions.IgnoreCase);
                                        Match m = r.Match(text);
                                        if (!m.Success) bWindows = false;
                                    }

                                    //check Arch
                                    if (Rule.Os.Arch != null)
                                    {
                                        if (Rule.Os.Arch == "x86" && C.getJavaArch() == "64") bWindows = false;
                                    }
                                }
                            }
                            if (Rule.Action == "disallow" && Rule.Os.Name == "windows") bWindows = false;
                        }
                        if (bWindows == false) continue;
                    }

                    // one value
                    if (jvme.JvmClass.Value.getString() != null)
                    {
                        args += " " + jvme.JvmClass.Value.getString();
                    }

                    // multiple values

                    if (jvme.JvmClass.Value.getStringArray() != null)
                    {
                        for(String value:jvme.JvmClass.Value.getStringArray())
                        {
                            // fix spaces in Json path
                            if(value.split("=").Last().Contains(" "))
                            {
                                args += " " + value.split("=").First() + "=/"" + value.split("=").Last() + "/"";
                            }
                            else
                            {
                                args += " " + value;
                            }
                        }
                    }
                }
                else
                {
                    args += " " + jvme.String;
                }
            }

            // startup class
            args += " " + MC.getMainClass();

            // Game
            for (GameElement ge:MC.getArguments().getGame())
            {
                if (ge.GameClass != null)
                {

                }
                else
                {
                    args += " " + ge.String;
                }
            }
        }
        else
        {
            // fucking Mojang drivers Hack
            args += " -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump";
            // Tweaks (forge)
            args += " -Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true";
            // Path to natives
            args += " -Djava.library.path=${natives_directory}";
            // Libs
            args += " -cp ${classpath}";
            // startup class
            args += " " + MC.getMainClass();
            // minecraft arguments
            args += " " + MC.getMinecraftArguments();
        }

        // libraries
        for (KeyValuePair<String, String> entry:ClassPath)
        {
            classpath += String.format("/"{0}/";", entry.Value);
        }

        // version .jar
        classpath += String.format("/"{0}//{1}//{1}.jar/" ", _sVersionDir, MC.getId());

        // fill placeholders
        args = args.replace("${auth_player_name}", Profile.getName());
        args = args.replace("${version_name}", MC.getId());
        args = args.replace("${game_directory}", String.format("/"{0}//{1}//minecraft/"", _sPacksDir, sPackName));
        args = args.replace("${assets_root}", String.format("/"{0}/"", _sAssetsDir));
        args = args.replace("${game_assets}", String.format("/"{0}//virtual//legacy/"", _sAssetsDir));
        args = args.replace("${assets_index_name}", MC.getAssets());
        args = args.replace("${auth_uuid}", Profile.getId());
        args = args.replace("${auth_access_token}", Acc.getAccessToken());
        args = args.replace("${auth_session}", String.format("token:{0}:{1}", Acc.getAccessToken(), Profile.getId()));
        args = args.replace("${user_properties}", "{}");
        args = args.replace("${user_type}", "Mojang");
        args = args.replace("${version_type}", MC.getType());
        args = args.replace("${natives_directory}", "/"" + _sNativesDir + "/" + MC.getId() +"/"");
        args = args.replace("${classpath}", classpath);
        //args = args.replace("${launcher_name}", Application.ProductName); //TODO
        //args = args.replace("${launcher_version}", Application.ProductVersion); //TODO

        return args;
    }

    private MCAvailablePackVersion GetAvailablePackVersion(String sPackName, String sPackVersion)
    {
        MCAvailablePack pack = GetAvailablePack(sPackName);

        for (MCAvailablePackVersion version:pack.getVersions())
        {
            if (version.getVersion().equals(sPackVersion))
            {
                return version;
            }
        }

        return new MCAvailablePackVersion();
    }

    private void InstallPack(String sPackName, String sPackVersion) throws IOException {
        // delete pack if installed
        if (IsPackInstalled(sPackName, null))
        {
            DeletePack(sPackName);
        }

        // if recommended version, getting the version from available packs
        if (sPackVersion == "recommended")
        {
            sPackVersion = GetRecommendedVersion(sPackName);
        }

        // delete old download
        File oldDownloadedFile = new File(_sPacksDir + "/" + sPackName + "-" + sPackVersion + ".zip");
        if (oldDownloadedFile.exists())
        {
            oldDownloadedFile.delete();
        }

        MCAvailablePackVersion version = GetAvailablePackVersion(sPackName, sPackVersion);
        if (version.isDownloadZip() == true)
        {
            // download pack
            downloadHelper.downloadFileTo(_sPackServerBaseURL + "/packs/" + sPackName + "/" + sPackName + "-" + sPackVersion + ".zip",
                    _sPacksDir + "/" + sPackName + "-" + sPackVersion + ".zip", true, "Downloading Pack " + sPackName, null);

            // Hide Bar
            downloadHelper.hideBar();

            // unzip pack
            downloadHelper.extractZipFiles(_sPacksDir + "/" + sPackName + "-" + sPackVersion + ".zip", _sPacksDir);

            // delete zip file
            File zipFile = new File(_sPacksDir + "/" + sPackName + "-" + sPackVersion + ".zip");
            zipFile.delete();
        }
        else
        {
            // write pack.json file
            MCPack pack = new MCPack();
            pack.setType("vanilla");
            pack.setMCVersion(version.getVersion());

            File.WriteAllText(_sPacksDir + "/" + sPackName +  "/pack.json", pack.ToJson());

            // write version file
            File.WriteAllText(_sPacksDir + "/" + sPackName + "/version", version.getVersion());
        }

        // check for "minecraft" folder
        File minecraftFolder = new File(_sPacksDir + "/" + sPackName + "/minecraft");
        if (!minecraftFolder.exists())
        {
            minecraftFolder.mkdir();
        }
    }

    private void DeletePack(String sPackName)
    {
        String PackDir = _sPacksDir + "/" + sPackName;

        if (!Directory.Exists(PackDir)) return; // Pack did not exist
        // Delete Directories
        if (Directory.Exists(PackDir + "/minecraft/logs")) Directory.Delete(PackDir + "/minecraft/logs", true);
        if (Directory.Exists(PackDir + "/minecraft/mods")) Directory.Delete(PackDir + "/minecraft/mods", true);
        if (Directory.Exists(PackDir + "/minecraft/modclasses")) Directory.Delete(PackDir + "/minecraft//modclasses", true);
        if (Directory.Exists(PackDir + "/minecraft/config")) Directory.Delete(PackDir + "/minecraft/config", true);
        if (Directory.Exists(PackDir + "/minecraft/stats")) Directory.Delete(PackDir + "/minecraft/stats", true);
        if (Directory.Exists(PackDir + "/minecraft/crash-reports")) Directory.Delete(PackDir + "/minecraft/crash-reports", true);
        // Deleting .log Files
        for (FileInfo f:new DirectoryInfo(PackDir + "/minecraft").GetFiles("*.log"))
        f.Delete();
        // Deleting .lck Files
        for (FileInfo f:new DirectoryInfo(PackDir + "/minecraft").GetFiles("*.lck"))
        f.Delete();
        // Deleting .1 Files
        for (FileInfo f:new DirectoryInfo(PackDir + "/minecraft").GetFiles("*.1"))
        f.Delete();
        // Deleting pack.json
        if (File.Exists(PackDir + "/pack.json")) File.Delete(PackDir + "/pack.json");
        // Deleting version
        if (File.Exists(PackDir + "/version")) File.Delete(PackDir + "/version");
    }

    /*
    public class FormWindowStateEventArgs : EventArgs
    {
        public FormWindowState WindowState { get; set; }
        public int MCExitCode { get; set; }
    }
    */
}
