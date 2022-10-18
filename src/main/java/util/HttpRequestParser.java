package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class HttpRequestParser {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);
    public static String path(BufferedReader br) {
        try {
            String[] header = new String[0];
            header = br.readLine().split(" ");
            if (header.length < 2 || !Objects.equals(header[1].substring(0, 1), "/"))
                throw new IllegalArgumentException();
            log.debug(header[1]);
            return header[1];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bodyOf(BufferedReader br) throws IOException {
        int contentLen = contentLengthOf(br);
        while (br.readLine().length() > 0) ;
        return IOUtils.readData(br, contentLen);
    }

    private static int contentLengthOf(BufferedReader br) throws IOException {
        while (br.ready()) {
            String line;
            if ((line = br.readLine()).contains("Content-Length")) {
                String[] keyVal = line.split(":");
                return Integer.valueOf(keyVal[1].substring(1));
            }
            log.debug(line);
        }
        return 0;
    }

    public static boolean getLogin(BufferedReader br) throws IOException {
        while (br.ready()) {
            String line;
            if ((line = br.readLine()).contains("Cookie")) {
                String[] keyVal = line.split(":");
                Map<String, String> loginInfo = HttpRequestUtils.parseCookies(keyVal[1].substring(1));
                log.info(loginInfo.get("logined"));
                return Boolean.parseBoolean(loginInfo.get("logined"));
            }
        }
        return false;
    }
}
