package es.jperea.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpUtils {
    public static List<String> shortUrlDomainList = new ArrayList<>();

    static {
        shortUrlDomainList.add("t.co");
        shortUrlDomainList.add("goo.gl");
        shortUrlDomainList.add("is.gd");
        shortUrlDomainList.add("bit.ly");
        shortUrlDomainList.add("amzn.to");
        shortUrlDomainList.add("tinyurl.com");
        shortUrlDomainList.add("ow.ly");
        shortUrlDomainList.add("sh.st");
        shortUrlDomainList.add("youtu.be");
    }

    public static boolean isShortUrl(URL url) {
        String host = url.getHost();
        return shortUrlDomainList.contains(host);
    }

    public static URL getUnshortenedUrl(URL url) throws Exception {
        URL result = url;
        while (url != null && isShortUrl(url)) {
            result = url;
            url = followRedirect(url);
        }
        return result;
    }

    public static URL followRedirect(URL url) throws Exception {
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setInstanceFollowRedirects(false);
//        httpUrlConnection.setRequestProperty("User-Agent", userAgent);
//        httpUrlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
//        httpUrlConnection.setReadTimeout(READ_TIMEOUT);
        int responseCode = httpUrlConnection.getResponseCode();
        switch (responseCode) {
            case HttpURLConnection.HTTP_MOVED_PERM: // 301 SC_MOVED_PERMANENTLY
            case HttpURLConnection.HTTP_MOVED_TEMP: // 302 SC_MOVED_TEMPORARILY
                return new URL(httpUrlConnection.getHeaderField("Location"));
        }
        return null;
    }

    public static String getHtmlContent(URL url) throws Exception {
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setInstanceFollowRedirects(true);
//        httpUrlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
//        httpUrlConnection.setReadTimeout(READ_TIMEOUT);
//        httpUrlConnection.setRequestProperty("User-Agent", userAgent);
//        httpUrlConnection.setRequestProperty("Accept-Encoding", "gzip,deflate");
//        httpUrlConnection.setRequestProperty("Accept", "application/json");
        httpUrlConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpUrlConnection.setRequestProperty("Accept", "text/http");
        InputStream inputStream = httpUrlConnection
                .getInputStream();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, "utf-8"));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}
