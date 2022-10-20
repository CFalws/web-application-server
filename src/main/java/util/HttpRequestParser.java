package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequestParser {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);
    private Map<String, String> header = new HashMap<>();
    private String body;

    public HttpRequestParser(BufferedReader br) {
        parse(br);
    }
    public static String requestLine(BufferedReader br) throws IOException {
        String[] header = br.readLine().split(" ");
        if (!Objects.equals(header[1].substring(0, 1), "/"))
            throw new IllegalArgumentException();
        log.debug(header[1]);
        return header[1];
    }

    public String bodyOf(BufferedReader br) throws IOException {
        int contentLen = Integer.parseInt(header.get("Content-Length"));
        return IOUtils.readData(br, contentLen);
    }

    private static int contentLengthOf(BufferedReader br) throws IOException {
        while (br.ready()) {
            String header;
            if ((header = br.readLine()).contains("Content-Length")) {
                return Integer.valueOf(HttpRequestUtils.parseHeader(header).getValue());
            }
        }
        return 0;
    }

    public String header(String key) throws IOException {
        return header.get(key);
    }

    private void parse(BufferedReader br) {
        try {
            String line;
            while ((line = br.readLine()).length() != 0) {
                header.put(HttpRequestUtils.parseHeader(line).getKey()
                        , HttpRequestUtils.parseHeader(line).getValue());
            }
            this.body = bodyOf(br);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isSignedIn(BufferedReader br) throws IOException {
        while (br.ready()) {
            String header;
            if ((header = br.readLine()).contains("Cookie")) {
                Map<String, String> userStatus = HttpRequestUtils
                        .parseCookies(HttpRequestUtils.parseHeader(header).getValue());
                return Boolean.parseBoolean(userStatus.get("logined"));
            }
        }
        return false;
    }

    public String getBody() {
        return body;
    }
}
