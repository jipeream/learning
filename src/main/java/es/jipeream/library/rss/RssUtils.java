package es.jipeream.library.rss;

import com.sun.syndication.feed.synd.SyndEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RssUtils {
    public static List<String> getAuthorList(SyndEntry syndEntry) {
        Map<String, Void> authorMap = new HashMap<>();
        for (String author : syndEntry.getAuthor().split("/")) {
            authorMap.put(author.trim(), null);
        }
        for (Object author : syndEntry.getAuthors()) {
            authorMap.put(author.toString(), null);
        }
        for (Object contributor : syndEntry.getContributors()) {
            authorMap.put(contributor.toString(), null);
        }
        return new ArrayList<>(authorMap.keySet());
    }

}
