package matveyodintsov.cloudfilestorage.config;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Validator {

    private static final String REGEX_VALID_NAME = "^(?!.*\\s{2})[а-яА-Яa-zA-Z0-9_\\-\\s]{1,255}$";

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
            String newFilename = getValidFoldername(newName);

            return newFilename + fileExtension;
        }

        public static String getValidFoldername(String newName) {
            String validName = newName.trim();
            if (!validName.matches(REGEX_VALID_NAME)) {
                throw new RuntimeException("Недопустимое имя файла. " +
                        "Разрешены только буквы, цифры, пробелы, дефисы и подчеркивания. " +
                        "Длина не более 255 символов.");
            }

            return validName;
        }
    }
//
//    public static class Upload {
//        public static boolean isUpload(BigDecimal cloudSize, Long fileSize) {
//
//            BigDecimal fileSizeMegabytes = new BigDecimal(fileSize)
//                    .divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP);
//            BigDecimal result = cloudSize.subtract(fileSizeMegabytes);
//
//            return fileSizeMegabytes.compareTo(BigDecimal.valueOf(FILE_SIZE)) > 0 &&
//                    result.compareTo(BigDecimal.ZERO) > 0;
//        }
//  }

}
