package matveyodintsov.cloudfilestorage.config;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AppConfig {

    private static final String REGEX_VALID_NAME = "^(?!.*\\s{2})[а-яА-Яa-zA-Z0-9_\\-\\s]{1,255}$";
    public static final String LOGIN_REGEX = "^(?![-.])[a-z0-9._-]{4,20}(?<![-.])$";
    public static final String BUCKET_NAME = "user-files";

    public static class Url {
        public static String encode(String url) {
            return URLEncoder.encode(url, StandardCharsets.UTF_8)
                    .replace("+", "%20")
                    .replace(" ", "%20")
                    .replace("%2F", "/");
        }

        public static String decode(String encodedUrl) {
            return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
        }

        public static String cross(String decodedPath) {
            return encode(decodedPath.substring(0, decodedPath.lastIndexOf("/")));

        }
    }

    public static class ContentName {

        public static String getValidFilename(String oldName, String newName) {
            String fileExtension = oldName.substring(oldName.lastIndexOf("."));
            String newFilename = getValidFolderName(newName);

            return newFilename + fileExtension;
        }

        public static boolean isValidLogin(String userName) {
            return userName.matches(LOGIN_REGEX);
        }

        public static String getValidFolderName(String newName) {
            String validName = newName.trim();
            if (!validName.matches(REGEX_VALID_NAME)) {
                throw new RuntimeException("Недопустимое имя файла. " +
                        "Разрешены только буквы, цифры, пробелы, дефисы и подчеркивания. " +
                        "Длина не более 255 символов.");
            }

            return validName;
        }
    }

}
