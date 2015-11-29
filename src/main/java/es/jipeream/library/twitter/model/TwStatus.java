package es.jipeream.library.twitter.model;

import org.json.JSONObject;

public class TwStatus {
    public TwStatus(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**/

    private final JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    /**/

    public long getId() {
        return jsonObject.optLong("id", 0);
    }
    public String getText() {
        return jsonObject.optString("text", "");
    }
    public TwUser getUser() {
        return new TwUser(jsonObject.optJSONObject("user"));
    }

}

//{
//  "metadata":  {
//      "iso_language_code": "und",
//      "result_type": "recent"
//  },
//  "created_at": "Mon Oct 26 11:37:16 +0000 2015",
//  "id": 658608330156429300,
//  "id_str": "658608330156429312",
//  "text": "https://t.co/ULlFGUfpn2 https://t.co/YiGkz3ZYgz",
//  "source": "<a href="http://www.facebook.com/twitter" rel="nofollow">Facebook</a>",
//  "truncated": false,
//  "in_reply_to_status_id": null,
//  "in_reply_to_status_id_str": null,
//  "in_reply_to_user_id": null,
//  "in_reply_to_user_id_str": null,
//  "in_reply_to_screen_name": null,
//  "user":  {
//      "id": 316751683,
//      "id_str": "316751683",
//      "name": "ACLERK DETECTIVES",
//      "screen_name": "ACLERKDETECTIVE",
//      "location": "Barcelona",
//      "description": "ACLERK DETECTIVES PRIVADOS Soluciones para Empresas y Particulares. Servicios de Investigaci�n Privada. Aportaci�n de Informaci�n y pruebas.",
//      "url": "http://t.co/IjMDQHCHHN",
//      "entities":  {
//          "url":  {
//              "urls":  [
//                  {
//                      "url": "http://t.co/IjMDQHCHHN",
//                      "expanded_url": "http://www.detectives-aclerk.com",
//                      "display_url": "detectives-aclerk.com",
//                      "indices":  [
//                          0,
//                          22
//                      ]
//                  }
//              ]
//          },
//          "description":  {
//              "urls":  []
//          }
//      },
//      "protected": false,
//      "followers_count": 181,
//      "friends_count": 120,
//      "listed_count": 5,
//      "created_at": "Mon Jun 13 22:34:05 +0000 2011",
//      "favourites_count": 1,
//      "utc_offset": 3600,
//      "time_zone": "Madrid",
//      "geo_enabled": false,
//      "verified": false,
//      "statuses_count": 97,
//      "lang": "es",
//      "contributors_enabled": false,
//      "is_translator": false,
//      "is_translation_enabled": false,
//      "profile_background_color": "EDECE9",
//      "profile_background_image_url": "http://pbs.twimg.com/profile_background_images/461659385186361344/NmcVfh5A.jpeg",
//      "profile_background_image_url_https": "https://pbs.twimg.com/profile_background_images/461659385186361344/NmcVfh5A.jpeg",
//      "profile_background_tile": false,
//      "profile_image_url": "http://pbs.twimg.com/profile_images/461651490684141568/gBFxcLAS_normal.png",
//      "profile_image_url_https": "https://pbs.twimg.com/profile_images/461651490684141568/gBFxcLAS_normal.png",
//      "profile_banner_url": "https://pbs.twimg.com/profile_banners/316751683/1408646075",
//      "profile_link_color": "088253",
//      "profile_sidebar_border_color": "FFFFFF",
//      "profile_sidebar_fill_color": "E3E2DE",
//      "profile_text_color": "634047",
//      "profile_use_background_image": false,
//      "has_extended_profile": false,
//      "default_profile": false,
//      "default_profile_image": false,
//      "following": false,
//      "follow_request_sent": false,
//      "notifications": false
//  },
//  "geo": null,
//  "coordinates": null,
//  "place": null,
//  "contributors": null,
//  "is_quote_status": false,
//  "retweet_count": 0,
//  "favorite_count": 0,
//  "entities":  {
//      "hashtags":  [],
//      "symbols":  [],
//      "user_mentions":  [],
//      "urls":  [
//          {
//              "url": "https://t.co/ULlFGUfpn2",
//              "expanded_url": "http://www.elmundo.es/cronica/2015/10/26/562b47a2e2704e07768b464c.html",
//              "display_url": "elmundo.es/cronica/2015/1�",
//              "indices":  [
//                  0,
//                  23
//              ]
//          },
//          {
//              "url": "https://t.co/YiGkz3ZYgz",
//              "expanded_url": "http://fb.me/20zqfCkQL",
//              "display_url": "fb.me/20zqfCkQL",
//              "indices":  [
//                  24,
//                  47
//              ]
//          }
//      ]
//  },
//  "favorited": false,
//  "retweeted": false,
//  "possibly_sensitive": false,
//  "lang": "und"
//}
