package matveyodintsov.cloudfilestorage.config;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Validator {

    public static class Url {

        public static String encode(String url) {
            return URLEncoder.encode(url, StandardCharsets.UTF_8)
                    .replace("+", "%20")
                    .replace("%2F", "/");
        }

        public static String decode(String encodedUrl) {
            return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
        }

        public static String cross(String decodedPath) {
            return encode(decodedPath.substring(0, decodedPath.lastIndexOf("/")));

        }
    }
}
