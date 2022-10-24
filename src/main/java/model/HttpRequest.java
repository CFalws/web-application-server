package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private String method;
    private String path;
    private Map<String, String> header = new HashMap<>();
    private String body;
    private Map<String, String> parameters;

    public HttpRequest(BufferedReader bufferedReader) {
        parse(bufferedReader);
    }
    private void parse(BufferedReader bufferedReader) {
        try {
            requestLine(bufferedReader);
            header(bufferedReader);
            body(bufferedReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void requestLine(BufferedReader bufferedReader) throws IOException {
        String[] reqLine = bufferedReader.readLine().split(" ");
        if (reqLine.length != 3)
            throw new IllegalArgumentException();
        method = reqLine[0];
        String[] pathAndParam = reqLine[1].split("\\?");
        path = pathAndParam[0];
        if (pathAndParam.length > 1)
            parameters = HttpRequestUtils.parseQueryString(pathAndParam[1]);
    }

    private void header(BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()).length() != 0) {
            log.debug(line);
            header.put(HttpRequestUtils.parseHeader(line).getKey()
                    , HttpRequestUtils.parseHeader(line).getValue());
        }
    }

    private void body(BufferedReader bufferedReader) throws IOException {
        try {
            body = IOUtils.readData(bufferedReader, Integer.parseInt(header.get("Content-Length")));
            parameters = HttpRequestUtils.parseQueryString(body);
        } catch (NumberFormatException e) {
            // do nothing
            log.debug("NumberFormat Exception!~~!");
        }
    }

    public boolean isSignedIn() throws IOException {
        String cookie = header.get("Cookie");
        if (Objects.nonNull(cookie)) {
            return Boolean
                    .parseBoolean(HttpRequestUtils.parseCookies(cookie).get("logined"));
        }
        return false;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String key) throws IOException {
        return header.get(key);
    }

    public String getParameters(String key) {
        return parameters.get(key);
    }
}
