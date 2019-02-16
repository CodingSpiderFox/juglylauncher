package org.codingspiderfox.juglylauncher.internet;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

public class Http {

    public static String get(String url) throws IOException {

        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            return readAll(rd);
        } catch (Exception ex) {
            //TODO error message
        }
        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    public static InputStream downloadToStream(String url) throws IOException {
        return downloadToStream(new URL(url));
    }


    public static InputStream downloadToStream(URL url) throws IOException {

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        return httpURLConnection.getInputStream();
    }
}
