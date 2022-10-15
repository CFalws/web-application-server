package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
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

    public static String body(BufferedReader br) throws IOException {
        int contentLen = getContentLength(br);
        log.debug(String.valueOf(contentLen));
        if (contentLen > 0)
            while (br.readLine().length() > 0) ;
        log.debug("getBody end");
        if (contentLen > 0)
            return IOUtils.readData(br, contentLen);
        return null;
    }

    private static int getContentLength(BufferedReader br) throws IOException {
        while (br.ready()) {
            String line;
            if ((line = br.readLine()).contains("Content-Length")) {
                String[] keyVal = line.split(":");
                return Integer.valueOf(keyVal[1].substring(1));
            }
            log.debug(line);
        }
        return -1;
    }
}
