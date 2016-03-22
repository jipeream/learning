/*
 * Decompiled with CFR 0_115.
 */
package es.jipeream.library.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class HttpUtils {
    public static List<String> shortUrlDomainList = new ArrayList<String>();

    public static boolean isShortUrl(URL url) {
        String host = url.getHost();
        if (shortUrlDomainList.contains(host)) {
            return true;
        }
        if (host.startsWith("www.")) {
            return false;
        }
        if (host.length() > 11) {
            return false;
        }
        return true;
    }

    public static URL getUnshortenedUrl(URL url) throws Exception {
        URL result = url;
        while (HttpUtils.isShortUrl(url) && (url = HttpUtils.followRedirect(url)) != null) {
            result = url;
        }
        result = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath());
        return result;
    }

    public static URL followRedirect(URL url) throws Exception {
        HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
        httpUrlConnection.setInstanceFollowRedirects(false);
        int responseCode = httpUrlConnection.getResponseCode();
        switch (responseCode) {
            case 301: 
            case 302: {
                return new URL(httpUrlConnection.getHeaderField("Location"));
            }
        }
        return null;
    }

    public static String getHtmlContent(URL url) throws Exception {
        String line;
        HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
        httpUrlConnection.setInstanceFollowRedirects(true);
        httpUrlConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpUrlConnection.setRequestProperty("Accept", "text/http");
        InputStream inputStream = httpUrlConnection.getInputStream();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    static {
        shortUrlDomainList.add("hubs.ly");
        shortUrlDomainList.add("ow.ly");
        shortUrlDomainList.add("bit.ly");
        shortUrlDomainList.add("buff.ly");
        shortUrlDomainList.add("1url.com");
        shortUrlDomainList.add("tinyurl.com");
        shortUrlDomainList.add("cort.as");
        shortUrlDomainList.add("ww.abc.es");
        shortUrlDomainList.add("vine.es");
        shortUrlDomainList.add("besturl.es");
        shortUrlDomainList.add("t.co");
        shortUrlDomainList.add("goo.gl");
        shortUrlDomainList.add("is.gd");
        shortUrlDomainList.add("amzn.to");
        shortUrlDomainList.add("sh.st");
        shortUrlDomainList.add("youtu.be");
    }
}

