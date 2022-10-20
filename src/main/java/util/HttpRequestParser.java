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
    private String path;
    private Map<String, String> header = new HashMap<>();
    private String body;

    public HttpRequestParser(BufferedReader request) {
        parse(request);
    }
    private void requestLine(BufferedReader request) throws IOException {
        String[] reqLine = request.readLine().split(" ");
        if (!Objects.equals(reqLine[1].substring(0, 1), "/"))
            throw new IllegalArgumentException();
        log.debug(reqLine[1]);
        path = reqLine[1];
    }

    private void body(BufferedReader request) throws IOException {
        int contentLen = Integer.parseInt(header.get("Content-Length"));
        body = IOUtils.readData(request, contentLen);
    }

    private void parse(BufferedReader request) {
        try {
            requestLine(request);
            header(request);
            if (!Objects.isNull(header.get("Content-Length")))
                body(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void header(BufferedReader request) throws IOException {
        String line;
        while ((line = request.readLine()).length() != 0) {
            header.put(HttpRequestUtils.parseHeader(line).getKey()
                    , HttpRequestUtils.parseHeader(line).getValue());
        }
    }

    public boolean isSignedIn() throws IOException {
        try {
            return Boolean.parseBoolean(HttpRequestUtils
                    .parseCookies(header.get("Cookie")).get("logined"));
        } catch (NullPointerException e) {
            return false;
        }
    }

    public String resourcePath() {
        return path;
    }

    public String header(String key) throws IOException {
        return header.get(key);
    }

    public String getBody() {
        return body;
    }
}
