package matveyodintsov.cloudfilestorage.config;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Validator {

    private static final String REGEX_VALID_NAME = "^[а-яА-Яa-zA-Z0-9_\\-\\s]+$";

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

    public static class ContentName {

        public static String getValidFilename(String oldName, String newName) {
            String fileExtension = oldName.substring(oldName.lastIndexOf("."));
            String newFilename = newName.replace(REGEX_VALID_NAME, "");
            newFilename = newFilename.trim();

            if (!newFilename.matches(REGEX_VALID_NAME)) {
                throw new RuntimeException("Недопустимое имя файла. " +
                        "Разрешены только буквы, цифры, пробелы, дефисы и подчеркивания");
            }

            return newFilename + fileExtension;
        }

        public static String getValidFoldername(String oldName, String newName) {
            String newFoldername = newName.replace(REGEX_VALID_NAME, "");
            newFoldername = newFoldername.trim();

            if (!newFoldername.matches(REGEX_VALID_NAME)) {
                throw new RuntimeException("Недопустимое имя файла. " +
                        "Разрешены только буквы, цифры, пробелы, дефисы и подчеркивания");
            }

            return newFoldername;
        }
    }
}
