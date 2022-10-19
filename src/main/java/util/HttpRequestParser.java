package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class HttpRequestParser {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);
    public static String requestLine(BufferedReader br) throws IOException {
        String[] header = br.readLine().split(" ");
        if (!Objects.equals(header[1].substring(0, 1), "/"))
            throw new IllegalArgumentException();
        log.debug(header[1]);
        return header[1];
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
                return Integer.valueOf(line.split(":")[1].substring(1));
            }
        }
        return 0;
    }

    public static boolean isSignedIn(BufferedReader br) throws IOException {
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
