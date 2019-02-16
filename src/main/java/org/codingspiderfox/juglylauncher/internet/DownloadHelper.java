package org.codingspiderfox.juglylauncher.internet;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadHelper {

    private boolean downloadFinished = false;

    //public FrmProgressbar _bar = new FrmProgressbar();

    public DownloadHelper() {
        //_downloader.DownloadProgressChanged += new DownloadProgressChangedEventHandler(DownloadProgressCallback);
        //_downloader.DownloadFileCompleted += new System.ComponentModel.AsyncCompletedEventHandler(Downloader_DownloadFileCompleted);
    }

    /*private void Downloader_DownloadFileCompleted(object sender, System.ComponentModel.AsyncCompletedEventArgs e)
    {
        downloadFinished = true;
    }*/

    public boolean isBarVisible() {
        //return _bar.Visible;
        return true; //TODO
    }

    public void hideBar() {
        //_bar.Hide(); //TODO
    }

    // Progress event from downloader
    /*private void DownloadProgressCallback(object sender, DownloadProgressChangedEventArgs e)
    {
        if (_bar.Visible) _bar.UpdateBar(e.ProgressPercentage);
    }*/

    public String computeHashSHA(String filename) throws NoSuchAlgorithmException, IOException {

        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        InputStream fis = new FileInputStream(filename);
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1) {
            n = fis.read(buffer);
            if (n > 0) {
                digest.update(buffer, 0, n);
            }
        }
        return String.valueOf(digest.digest());
    }

    // download file if needed
    public void downloadFileTo(String sRemotePath, String sLocalPath, boolean bShowBar, String sBarDisplayText,
                               String sha1) throws IOException {

        downloadFileTo(new URL(sRemotePath), sLocalPath, bShowBar, sBarDisplayText, sha1);
    }

    // download file if needed
    public void downloadFileTo(URL url, String sLocalPath, boolean bShowBar, String sBarDisplayText, String sha1)
            throws IOException {

        boolean _download = false;
        File localFile = new File(sLocalPath);
        if (!localFile.exists()) {
            _download = true;
        } else {
            if (localFile.length() == 0) {
                _download = true;
            }

            // check SHA1
            if (sha1 != null) {
                try {
                    String file_sha = computeHashSHA(sLocalPath);
                    if (!file_sha.equals(sha1)) {
                        _download = true;
                    }
                } catch (NoSuchAlgorithmException ex) {
                    //TODO display error message
                }
            }
        }

        if (_download) {
            // Create Directory, if needed
            if (!localFile.exists() || !localFile.isDirectory()) {
                localFile.mkdir();
            }


            /*if (bShowBar == true)
            {
                if (_bar.Visible == false) _bar.Show();
                if (sBarDisplayText == null) _bar.SetLabel(Path.GetFileName(URL.LocalPath));
                else _bar.SetLabel(sBarDisplayText);
            }
            downloadFinished = false;
            _downloader.DownloadFileAsync(Url, sLocalPath);
            Application.DoEvents();
            while (downloadFinished == false)
                Application.DoEvents();
            */


            HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
            long completeFileSize = httpConnection.getContentLength();

            BufferedInputStream downloadedFileInputStream =
                    new BufferedInputStream(httpConnection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(url.getFile());
            BufferedOutputStream bout = new BufferedOutputStream(fileOutputStream, 1024);

            byte[] data = new byte[1024];
            long downloadedFileSize = 0;
            int x = 0;

            while ((x = downloadedFileInputStream.read(data, 0, 1024)) >= 0) {
                downloadedFileSize += x;
                if (downloadedFileSize == completeFileSize) {
                    downloadFinished = true;
                }
            }

        }
    }

    public void extractZipFiles(String archiveFilenameIn, String outFolder) throws IOException {

        ZipInputStream zipInputStream = null;
        try {
            FileInputStream fs = new FileInputStream(archiveFilenameIn);
            zipInputStream = new ZipInputStream(fs);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                // ignore the META-INF folder
                if (zipEntry.getName().contains("META-INF")) continue;

                if (zipEntry.isDirectory()) {
                    continue;           // Ignore directories
                }
                String entryFileName = zipEntry.getName();
                // to remove the folder from the entry:- entryFileName = Path.GetFileName(entryFileName);
                // Optionally match entrynames against a selection list here to skip as desired.
                // The unpacked length is available in the zipEntry.Size property.

                // Manipulate the output filename here as desired.
                Path fullZipToPath = Paths.get(outFolder, entryFileName);
                String directoryName = fullZipToPath.getParent().toString();
                if (directoryName.length() > 0) {
                    new File(directoryName).mkdir();
                }

                if (!zipEntry.isDirectory()) {
                    // Unzip file in buffered chunks. This is just as fast as unpacking to a buffer the full size
                    // of the file, but does not waste memory.
                    // The "using" will close the stream even if an exception occurs.
                    FileOutputStream fileOutputStream = new FileOutputStream(fullZipToPath.toString());
                    {
                        IOUtils.copy(zipInputStream, fileOutputStream);
                    }
                }
            }
        } finally {
            if (zipInputStream != null) {
                zipInputStream.close(); // Ensure we release resources
            }
        }
    }

    public void extractZipFiles(String archiveFilenameIn, String outFolder, List<String> filesToExtract) throws IOException {


        ZipInputStream zipInputStream = null;
        try {
            FileInputStream fs = new FileInputStream(archiveFilenameIn);
            zipInputStream = new ZipInputStream(fs);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                // ignore the META-INF folder
                if (zipEntry.getName().contains("META-INF")) continue;

                if (zipEntry.isDirectory()) {
                    continue;           // Ignore directories
                }
                String entryFileName = zipEntry.getName();
                if (filesToExtract.contains(entryFileName)) {
                    // to remove the folder from the entry:- entryFileName = Path.GetFileName(entryFileName);
                    // Optionally match entrynames against a selection list here to skip as desired.
                    // The unpacked length is available in the zipEntry.Size property.

                    // Manipulate the output filename here as desired.
                    Path fullZipToPath = Paths.get(outFolder, entryFileName);
                    String directoryName = fullZipToPath.getParent().toString();
                    if (directoryName.length() > 0) {
                        new File(directoryName).mkdir();
                    }

                    if (!zipEntry.isDirectory()) {
                        // Unzip file in buffered chunks. This is just as fast as unpacking to a buffer the full size
                        // of the file, but does not waste memory.
                        // The "using" will close the stream even if an exception occurs.
                        FileOutputStream fileOutputStream = new FileOutputStream(fullZipToPath.toString());
                        {
                            IOUtils.copy(zipInputStream, fileOutputStream);
                        }
                    }
                }
            }
        } finally {
            if (zipInputStream != null) {
                zipInputStream.close(); // Ensure we release resources
            }
        }

    }

}
