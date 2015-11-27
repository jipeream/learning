package com.fs.fsnews.config;

import com.google.common.collect.Lists;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class FsnTwitterConfig {

    public static Authentication createAuthentication() {
        // Datos jipeream
        String consumerKey = "WUbe7rRkEZxLclBUxgVEK4tzI";
        String consumerSecret = "QaUeWy0NZVqPdy7FB12DgIubHhZAdkfWnSrQXbd8vDqrokM8NV";
        String accessToken = "197066312-QQLCZ5qbLu2t4IFizULAuYqX64SduT2puTMvtbYn";
        String accessTokenSecret = "u9ATwZmpp4DwCQflrsYqq42qd4gXTQFU5vFclYcyKWSPt";

        Authentication authentication = new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        // Authentication authentication = new BasicAuth(username, password);

        return authentication;
    }

    public static StatusesFilterEndpoint createStatusesFilterEndpoint(String... keywords) {
        StatusesFilterEndpoint statusesFilterEndpoint = new StatusesFilterEndpoint();
        statusesFilterEndpoint.trackTerms(Lists.newArrayList(keywords));
        return statusesFilterEndpoint;
    }

    public static UserstreamEndpoint createUserstreamEndpoint(String user) {
        UserstreamEndpoint userstreamEndpoint = new UserstreamEndpoint();
        userstreamEndpoint.allReplies(true);
        userstreamEndpoint.withUser(true);
        userstreamEndpoint.withFollowings(true);
        return userstreamEndpoint;
    }

    public static StatusesSampleEndpoint createStatusesSampleEndpoint() {
        StatusesSampleEndpoint statusesSampleEndpoint = new StatusesSampleEndpoint();
        return statusesSampleEndpoint;
    }
}